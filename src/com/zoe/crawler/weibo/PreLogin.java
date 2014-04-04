package com.zoe.crawler.weibo;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.zoe.DBProcess.DBBasic;

/**
 * main class of sina weibo log in
 * @author zhanglm
 */
@SuppressWarnings("deprecation")
public class PreLogin {
	private HttpClient client;
    private String userName;
    private String pwd;
    private String su;
    private String sp;
    private String servertime;
    private String nonce;
    private String pubkey;
    private String rsakv;
    
    private String location;
    
    private Vector<String> urlSet;
    
    private static DBBasic db = new DBBasic();
    /**
     * the constructor of this class, and with some parameters:
     * String username, String password and Vector<String> urlSet
     * @param userName, pwd, urlSet
     */
    public PreLogin(String userName,String pwd,Vector<String> urlSet){
        client = new DefaultHttpClient();
        this.userName = userName;
        this.pwd = pwd;
        this.urlSet = urlSet;
    }
    /**
     * the prelogin function contains the processes of prelog and log in 
     * weibo 
     * @return boolean type
     */
    public boolean preLogin(){
        boolean flag = false;
        try{
            //sina prelog in
            su = new String(Base64.encodeBase64(URLEncoder.encode(userName,"UTF-8").getBytes()));
            long curTime = System.currentTimeMillis();
            String preUrl = "http://login.sina.com.cn/sso/prelogin.php?entry=account&callback=sinaSSOController.preloginCallBack&" +
                    "su=" + su + "&rsakt=mod&client=ssologin.js(v1.4.14)&_=" + String.valueOf(curTime);
            HttpGet get = new HttpGet(preUrl);
            HttpResponse response = client.execute(get);
            HttpEntity rentity = response.getEntity();
            String preContent = EntityUtils.toString(rentity);
            
            servertime = getCode(preContent,"servertime");
            nonce = getCode(preContent,"nonce");
            pubkey = getCode(preContent,"pubkey");
            rsakv = getCode(preContent,"rsakv");
            String pwdString = servertime + "\t" + nonce + "\n" + pwd;
            sp = new BigIntegerRSA().rsaCrypt(pubkey, "10001", pwdString);
            
            //log in process
            String urlPre = "https://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.14)&_="+System.currentTimeMillis();//"http://login.sina.com.cn";
            HttpPost post = new HttpPost(urlPre);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("entry","weibo"));
            params.add(new BasicNameValuePair("gateway","1"));
            params.add(new BasicNameValuePair("from",""));
            params.add(new BasicNameValuePair("savestate","7"));
            params.add(new BasicNameValuePair("useticket","1"));
            params.add(new BasicNameValuePair("ssosimplelogin","1"));
            params.add(new BasicNameValuePair("pagerefer","http://weibo.com/a/download"));
            params.add(new BasicNameValuePair("url","http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
            params.add(new BasicNameValuePair("su",su));
            params.add(new BasicNameValuePair("vsnf","1"));
            params.add(new BasicNameValuePair("vsnval","0"));
            params.add(new BasicNameValuePair("service","miniblog"));
            params.add(new BasicNameValuePair("servertime",servertime));
            params.add(new BasicNameValuePair("nonce",nonce));
            params.add(new BasicNameValuePair("pwencode","rsa2"));
            params.add(new BasicNameValuePair("rsakv",rsakv));
            params.add(new BasicNameValuePair("sp",sp));
            params.add(new BasicNameValuePair("encoding","UTF-8"));
            params.add(new BasicNameValuePair("cdult","3"));
            params.add(new BasicNameValuePair("return","META"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,"UTF-8");
            post.setEntity(entity);
            
            response = client.execute(post);
            
            rentity = response.getEntity();
            String content = EntityUtils.toString(rentity);
            location = getLocation(content);
            get = new HttpGet(location);
            response = client.execute(get);
            
            //get cookie from successful log in process
            List<Cookie> cookies = ((AbstractHttpClient) client).getCookieStore().getCookies();
            StringBuilder cookieString = new StringBuilder();
            boolean first = true;
            for (Cookie cookie : cookies) {
                if (first) {
                    first = false;
                } else {
                    cookieString.append("; ");
                }
                cookieString.append(cookie.getName()).append("=");
                cookieString.append(cookie.getValue());
            }
            
            get.getParams().setParameter(
                    ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
            
            //crawl comments and reposts process
            for(String url:urlSet){
            	crawlWeibo(client,response,rentity,cookieString,url);
            }
            
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }catch(ClientProtocolException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }catch(InvalidKeyException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return flag;
    }
    
    /**
     * this function using the cookies to crawl the specific weibo page
     * @param client, response,rentity, cookieString,url
     */
    public static void crawlWeibo(HttpClient client,HttpResponse response,HttpEntity rentity,StringBuilder cookieString,String url){
    	client = new DefaultHttpClient();
        
        HttpGet newGet = new HttpGet(url);
        newGet.setHeader("Accept-Language", "en-us,en;q=0.5");
        newGet.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        newGet.setHeader("Accept-Charset","ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        newGet.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.2; en-US; rv:1.9.0.7) Gecko/2009021910 Firefox/3.0.7 (.NET CLR 3.5.30729)");
        //set cookies
        newGet.setHeader("Cookie", cookieString.toString());
        
        try {
            response = client.execute(newGet);
            //get source code from url
            rentity = response.getEntity();
            String content = EntityUtils.toString(rentity);
            //302 request
            String location = getLocation(content);
            newGet = new HttpGet(location);
            response = client.execute(newGet);
            rentity = response.getEntity();
            content = EntityUtils.toString(rentity);
            //initializing parameters
            GetComments gc = new GetComments();
            GetRepost gr = new GetRepost();
            GetInfoNum gif = new GetInfoNum();
            HashMap<String,String> hmNum = gif.getNum(content);
            System.out.println(hmNum.toString());
            
            //get comments
            Integer comNum = Integer.valueOf(hmNum.get("评论"));
            double comPageNum = Math.ceil(comNum/25.0);
            String urlCom = "http://weibo.com/1893769523/AypyObmtG?type=comment&page=";
            for(int i=1;i<=comPageNum;i++){
            	System.out.println("crawling comments pages~~");
            	url = urlCom + String.valueOf(i);
            	newGet = new HttpGet(url);
            	response = client.execute(newGet);
            	rentity = response.getEntity();
            	content = EntityUtils.toString(rentity);
            	Vector<HashMap<String,String>> hmc = gc.getComments(gif.getScript(content));
            	if(hmc==null) continue;
            	for(int j=0;j<hmc.size();j++){
            		HashMap<String,String> hm = hmc.get(j);
            		String sql = "INSERT INTO weibocom(mid,uid,userName,content,pubTime,crawlTime,praised,reply)"
        					+ "VALUES ('"+hm.get("mid")+"','"+hm.get("uid")+"','"+hm.get("userName")+"','"+
        					hm.get("content")+"','"+hm.get("pubTime")+"','"+hm.get("crawlTime")+"','"+
        					hm.get("praised")+"','"+hm.get("reply")+"')";
            		db.insert(sql);
//            		DBBasic.insertCom(hm.get("mid"), hm.get("uid"), hm.get("userName"), hm.get("content"), 
//            				hm.get("pubTime"), hm.get("crawlTime"), hm.get("praised"), hm.get("reply"));
            		hm = null;
            	}
            }
            System.out.println("done");
            
          //get reposts
            Integer reNum = Integer.valueOf(hmNum.get("转发"));
            double rePageNum = Math.ceil(reNum/25.0);
            String urlRep = "http://weibo.com/1893769523/AypyObmtG?type=repost&page=";
            for(int i=1;i<=rePageNum;i++){
            	System.out.println("crawling reposts pages~~");
            	url = urlRep + String.valueOf(i);
            	System.out.println(url);
            	newGet = new HttpGet(url);
            	response = client.execute(newGet);
            	rentity = response.getEntity();
            	content = EntityUtils.toString(rentity);
            	Vector<HashMap<String,String>> hmr = gr.getReposts(gif.getScript(content));
            	if(hmr==null) continue;
            	for(int j=0;j<hmr.size();j++){
            		HashMap<String,String> hm = hmr.get(j);
            		System.out.println(hm.toString());
            		String sql = "INSERT INTO weiborep(mid,uid,userName,content,ouid,omid,ouname,pubTime,crawlTime,praised,repost)"
        					+ "VALUES ('"+hm.get("mid")+"','"+hm.get("uid")+"','"+hm.get("userName")+"','"+
        					hm.get("content")+"','"+hm.get("ouid")+"','"+hm.get("omid")+"','"+hm.get("ouname")+"','"+hm.get("pubTime")+
        					"','"+hm.get("crawlTime")+"','"+hm.get("praised")+"','"+hm.get("repost")+"')";
            		db.insert(sql);
//            		DBBasic.insertRepost(hm.get("mid"), hm.get("uid"), hm.get("userName"), hm.get("content"), 
//            				hm.get("ouid"),hm.get("omid"),hm.get("ouname"),hm.get("pubTime"), 
//            				hm.get("crawlTime"), hm.get("praised"), hm.get("repost"));
            		hm = null;
            	}
            }
            System.out.println("done");
            
        } catch (IOException e) {
            e.printStackTrace();
        }finally
        {
            newGet.releaseConnection();
        }
    }
    /**
     * get the code from sina send the specific page to you after 
     * prelogin 
     * @param input
     * @param mark
     * @return
     */
    public static String getCode(String input,String mark) {
        String result = null;
        Pattern p = Pattern.compile(mark+".*?,");
        Matcher m = p.matcher(input);
        if(m.find()){
            String tmp = m.group().substring(0,m.group().length()-1);
            result = tmp.trim().split(":")[1];
            if(result.contains("\"")){
                result = result.substring(1,result.length()-1);
            }
        }
        return result;
    }
    /**
     * get the location from the page that contains the "location.replace"
     * @param content the html source code of the page
     * @return the location url
     */
    public static String getLocation(String content){
        String location = null;
        Pattern p = Pattern.compile("location.*?http://.*?\"");
        Matcher m = p.matcher(content);
        if(m.find()){
            location = m.group();
            location = location.substring("location.replace(".length()+1,location.length()-1);
        }
        return location;
    }
    
    /**
     * save content in file
     * @param filePath
     * @param hm
     * @param way
     * @throws IOException
     */
    public static void saveFile(String filePath,HashMap<String,String> hm,String way) throws IOException{
    	String[] content = hm.toString().substring(1, hm.toString().length()-1).split(" ");
    	String fileName = filePath + "\\" + way + "_" + hm.get("mid")+".txt";
    	File fout = new File(fileName);
    	BufferedWriter bw = new BufferedWriter(new FileWriter(fout,true));
    	for(String entry:content){
    		bw.write(entry+"\n");
    	}
    	bw.write("\n");
    	bw.flush();
    	bw.close();
    }
    /**
     * main entry
     * @param args
     */
    public static void main(String[] args){
        String userName = "aster1989@163.com";
        String pwd = "su19880922";
        Vector<String> urlSet = new Vector<String>();
        String url = "http://weibo.com/1893769523/AypyObmtG";
        urlSet.add(url);
        PreLogin p = new PreLogin(userName,pwd,urlSet);
        p.preLogin();
        
    }
}

package com.zoe.crawler.weibo;


import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.zoe.DBProcess.DBBasic;

/**
 * s.weibo.com crawler
 * @author zhanglm
 *
 */
public class SinaWeiboCrawler {
	static DBBasic db = new DBBasic();
	public static LinkedList<String> q = new LinkedList<String>();
	public static void main(String[] args){
		
		//parameters initialization
		String[] searchWords = {"空调"};
		int totalPage = 50;
		
		
		//main crawling processing
		for(int i=0;i<searchWords.length;i++) {
			String searchWord = searchWords[i];
			String filePath = "E:\\zhanglemei\\crawlerFile\\sweibo\\"+searchWord;
			File f = new File(filePath);
			f.mkdir();
			String html = null;
			System.out.println("begin to crawl page with word "+searchWord+"...");
			
			for(int j=1;j<=totalPage;j++) {
				String htmlLink = "http://s.weibo.com/weibo/"+searchWord+"&nodup=1&page="+String.valueOf(i);
				String isValid = null;
				try{
					html = getHtml(htmlLink);//get html source code
					if(html==null) {
						j--;
						continue;
					}
					isValid = isValidHTML(html);//judging whether the html is valid
					if(isValid == "NoResult"){
						j = totalPage+1;
						continue;
					}else {
						if(isValid == "VerificationCode"){
							System.out.println("****十秒内输入验证码****");
	                        Toolkit.getDefaultToolkit().beep();//蜂鸣提示需要输入验证码
	                        runBroswer("http://s.weibo.com/weibo/%25E6%259D%258E%25E9%259B%25AA%25E5%25B1%25B1hakka&Refer=index");
	                        Thread.sleep(10000);//你有十秒时间可以填入验证码
	                        j--;
						}else{
							save2DB(html,searchWord);
						}
					}
					
				}catch(ClientProtocolException e){
					System.out.println("ClientProtocolException...");
//					e.printStackTrace();
				}catch(IOException e){
					System.out.println("IOException...");
					e.printStackTrace();
				}catch(InterruptedException e){
					System.out.println("InterruptedException from isValid...");
					e.printStackTrace();
				}catch(URISyntaxException e){
					System.out.println("URISyntaxException from runBrowser...");
					e.printStackTrace();
				}
				
			}
		}
		System.out.println("#########################mid size:\t"+q.size());
	}
	
	/**
	 * judging whether the page is valid
	 * @param html 
	 * @return 
	 * @throws InterruptedException
	 */
	public static String isValidHTML(String html) throws InterruptedException {
        // TODO Auto-generated method stub
        String isValid = null;
            Pattern pNoResult = Pattern.compile("\\\\u60a8\\\\u53ef\\\\u4ee5\\\\u5c1d\\\\u8bd5"
                    +"\\\\u66f4\\\\u6362\\\\u5173\\\\u952e\\\\u8bcd\\\\uff0c\\\\u518d\\\\u6b21"
                    +"\\\\u641c\\\\u7d22\\\\u3002");//您可以尝试更换关键词，再次搜索。
            Pattern pVerify = Pattern.compile("\\\\u4f60\\\\u7684\\\\u884c\\\\u4e3a\\\\u6709"
                    +"\\\\u4e9b\\\\u5f02\\\\u5e38\\\\uff0c\\\\u8bf7\\\\u8f93\\\\u5165\\\\u9a8c"
                    +"\\\\u8bc1\\\\u7801\\\\uff1a");//你的行为有些异常，请输入验证码
            Matcher mNoResult = pNoResult.matcher(html);
            if(mNoResult.find()){
            	isValid = "NoResult";
            }else {
            	Matcher mVerity = pVerify.matcher(html);
            	if(mVerity.find()){
            		isValid = "VerificationCode";
            	}
            }
        return isValid;
    }
	
	public static String getHtml(String url) throws ClientProtocolException, IOException {
		// cookie 
		CookieSpecProvider easySpecProvider = new CookieSpecProvider(){
            public CookieSpec create(HttpContext context) {
                return new BrowserCompatSpec() {
                    @Override
                    public void validate(Cookie cookie,CookieOrigin origin)
                            throws MalformedCookieException {
                        // Oh, I am easy
                    }
                };
            }
        };
        Registry<CookieSpecProvider> r = RegistryBuilder
                .<CookieSpecProvider>create()
                .register(CookieSpecs.BEST_MATCH,new BestMatchSpecFactory())
                .register(CookieSpecs.BROWSER_COMPATIBILITY,
                        new BrowserCompatSpecFactory())
                .register("easy",easySpecProvider).build();
        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec("easy")
                .setSocketTimeout(5000)//设置socket超时时间
                .setConnectTimeout(5000)//设置connect超时时间
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieSpecRegistry(r)
                .setDefaultRequestConfig(requestConfig).build();
		
		
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        String html = null;
        try{
            CloseableHttpResponse response = httpClient.execute(httpGet);
            html= EntityUtils.toString(response.getEntity());
        }catch(IOException e){
            System.out.println("****连接超时，程序自动重连****");
        }
        return html;
	}
	
	/**
     * 用默认浏览器打开指定网址
     * @param url
     * @throws URISyntaxException
     * @throws IOException
     */
    public static void runBroswer(String url) throws URISyntaxException,IOException { 
            Desktop desktop = Desktop.getDesktop(); 
            if (Desktop.isDesktopSupported()&& desktop.isSupported(Desktop.Action.BROWSE)) { 
                URI uri = new URI(url); 
                desktop.browse(uri);  
                }
    }
    
    public static void save2DB(String html,String searchWord) {
    	System.out.println("begin to parse html page...");
    	Document doc = Jsoup.parse(html);
    	Elements scripts = doc.getElementsByTag("script");
    	Pattern ps = Pattern.compile("\"pid\":\"pl_weibo_direct\"");
    	for(org.jsoup.nodes.Element script:scripts) {
    		String spt = script.html();
    		Matcher ms = ps.matcher(spt);
    		if(ms.find()){
    			Vector<String> weiboDivs = getDiv(spt);
    			for(int i=0;i<weiboDivs.size();i++) {
    				String div = weiboDivs.get(i);
    				String com = getComDl(div);
    				if(com!=null){
    					String comUrl = getWeiboUrl(com);
    					System.out.println("comUrl:\t"+comUrl);
    					div = div.replace(com, "");
    				}
    				String content = getContent(div);
    				String url = getWeiboUrl(div);
        			String mid = getMid(div);
        			if(!q.contains(mid)){
        				q.add(mid);
        			}
        			String uid = getUid(div);
        			String pubTime = getPubtime(div);
        			String forNum = getForNum(div);
        			String comNum = getComNum(div);
        			if(content!=null){
        				String sql = "select * from weibo where mid='"+mid+"'";
        				if(!db.query(sql)){
        					sql = "INSERT INTO weibo(url,mid,uid,content,pub_time,forwarded,comment,keyword)"
        							+ "VALUES ('"+url+"','"+mid+"','"+uid+"','"+
        							content+"','"+pubTime+"','"+forNum+"','"+comNum+"','"+searchWord+"')";
        					db.insert(sql);
        				}
        			}
    			}
    		}
    	}
	}
	
	/**
	 * get eligible divs containing all weibo content of one page of javascript
	 * @param script //get JavaScript from .html file
	 * @return Vector //contain all eligible divs of one page
	 */
	public static Vector<String> getDiv(String script) {
		Vector<String> weiboDivs = new Vector<String>();
		Pattern pd = Pattern.compile("<dl class=(\\\\[\"\'])feed_list W_linecolor.*?clear\\W.*?\\/dl>");
		Matcher md = pd.matcher(script);
		while(md.find()){
			weiboDivs.add(md.group());
		}
		return weiboDivs;
	}
	
	/**
	 * get comments dl of one div that forwarded others words
	 * @param div //div containing one people's article
	 * @return the String of comment dl script
	 */
	public static String getComDl(String div) {
		String comDl = null;
		Pattern pc = Pattern.compile("<dl class=\\\\[\"\'](comment W_textc W_linecolor W_bgcolor)(\\2).*?\\/dl>");
		Matcher mc = pc.matcher(div);
		if(mc.find()){
			comDl = mc.group();
		}
		return comDl;
	}
	
	/**
	 * get the weibo's original url from one div without comments
	 * @param divWithoutCom
	 * @return the eiligible url
	 */
	public static String getWeiboUrl(String divWithoutCom) {
		String url = null;
		Pattern pa = Pattern.compile("http:\\\\/\\\\/weibo.com\\\\/[0-9]{10}\\\\/[a-zA-Z0-9]*?\\\\");
		Matcher ma = pa.matcher(divWithoutCom);
		while(ma.find()){
			url = ma.group().replace("\\", "");
		}
		return url;
	}
	
	/**
	 * get weiboID(mid) of the weibo which is always being a 16 long sequences
	 * @param div //the div of each people
	 * @return the String sequence of mid
	 */
	public static String getMid(String div) {
		String mid = null;
		Pattern p = Pattern.compile("\"mid=[0-9]{16}");
		Matcher m = p.matcher(div);
		if(m.find()){
			mid = m.group().substring(m.group().indexOf("mid")+4);
		}
		return mid;
	}
	
	/**
	 * get userID of the weibo which is always being a 10 long sequences
	 * @param div //the div of each people
	 * @return the String sequence of uid
	 */
	public static String getUid(String div) {
		String uid = null;
		Pattern p = Pattern.compile("weibo_nologin_name:[0-9]{10}");
		Matcher m = p.matcher(div);
		while(m.find()){
			uid = m.group().substring(m.group().indexOf("name:")+5);
		}
		return uid;
	}
	
	/**
	 * get published date of the weibo
	 * @param div
	 * @return
	 */
	public static String getPubtime(String div) {
		String pubTime = null;
		Pattern p = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}\\s[0-9]{2}:[0-9]{2}");
		Matcher m = p.matcher(div);
		while(m.find()){
			pubTime = m.group();
		}
		return pubTime;
	}
	
	/**
	 * get the forwarded number of the weibo
	 * @param divWithoutCom
	 * @return
	 */
	public static String getForNum(String divWithoutCom) {
		//get rid of the forwarded weibo
		Vector<String> filter = new Vector<String>();
		Pattern pf = Pattern.compile("comment W_textc W_linecolor W_bgcolor.*?\\/dl>");
		Matcher mf = pf.matcher(divWithoutCom);
		String sp = divWithoutCom;
		while(mf.find()){
			filter.add(mf.group());
		}
		for(int i=0;i<filter.size();i++) {
			String tmp = filter.get(i);
			tmp = sp.substring(sp.indexOf(tmp)-"<dl class=\\\"".length(), sp.indexOf(tmp)) + tmp;
			sp = sp.replace(tmp, "");
		}
		String forNum = null;
		Pattern p = Pattern.compile(".u8f6c.u53d1(.[0-9]*.)?<");
		Matcher m = p.matcher(sp);
		if(m.find()){
			if(m.group().indexOf("(")==-1){
				forNum = "0";
			}else{
				forNum = m.group().substring(m.group().indexOf("(")+1, m.group().indexOf(")"));
			}
		}
		return forNum;
	}
	
	/**
	 * get the commented number of the weibo
	 * @param divWithoutCom
	 * @return
	 */
	public static String getComNum(String divWithoutCom) {
		//get rid of the forwarded weibo
		Vector<String> filter = new Vector<String>();
		Pattern pc = Pattern.compile("comment W_textc W_linecolor W_bgcolor.*?\\/dl>");
		Matcher mc = pc.matcher(divWithoutCom);
		String sp = divWithoutCom;
		while(mc.find()){
			filter.add(mc.group());
		}
		for(int i=0;i<filter.size();i++) {
			String tmp = filter.get(i);
			tmp = sp.substring(sp.indexOf(tmp)-"<dl class=\\\"".length(), sp.indexOf(tmp)) + tmp;
			sp = sp.replace(tmp, "");
		}
		String comNum = null;
		Pattern p = Pattern.compile(".u8bc4.u8bba(.[0-9]*.)?<");
		Matcher m = p.matcher(sp);
		while(m.find()){
			if(m.group().indexOf("(")==-1){
				comNum = "0";
			}else{
				comNum = m.group().substring(m.group().indexOf("(")+1, m.group().indexOf(")"));
			}
		}
		return comNum;
	}
	
	/**
	 * get weibo content without comments or forwards
	 * @param divWithoutCom
	 * @return
	 */
	public static String getContent(String divWithoutCom) {
		String content = null;
		Pattern pc = Pattern.compile("<em>.*?color:red.*?<\\\\/em>");
		Matcher mc = pc.matcher(divWithoutCom);
		String tmp = null;
		if(mc.find()){
			tmp = mc.group();
			Pattern pf = Pattern.compile("<.*?>|\\n");
			Matcher mf = pf.matcher(mc.group());
			while(mf.find()) {
				tmp = tmp.replace(mf.group(), "");
			}
		}
		Pattern p = Pattern.compile("\\\\/");
		Matcher m = p.matcher(tmp);
		if(m.find()){
			tmp = tmp.replace(m.group(), "");
		}
		content = revert(tmp);
		return content;
	}
	
	/**
     * convert unicode into Chinese character
     *
     * @param str //unicode word waiting for being converted
     *            
     * @return Chinese character
     */
	public static String revert(String str) {
        if (str != null && str.trim().length() > 0) {
            String un = str.trim();
            StringBuffer sb = new StringBuffer();
            int idx = un.indexOf("\\u");
            while (idx >= 0) {
                if (idx > 0) {
                    sb.append(un.substring(0, idx));
                }

                String hex = un.substring(idx + 2, idx + 2 + 4);
                sb.append((char) Integer.parseInt(hex, 16));
                un = un.substring(idx + 2 + 4);
                idx = un.indexOf("\\u");
            }
            sb.append(un);
            return sb.toString();
        }
        return "";
    }
}

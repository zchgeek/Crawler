package com.zoe.crawler.weibo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * get specific reposts of a particular weibo
 * @author zhanglm
 *
 */
public class GetRepost {
	public Vector<HashMap<String,String>> getReposts(String script){
		if(script==null){
			return null;
		}
		Vector<HashMap<String,String>> hmr = getRep(script);
		return hmr;
	}
	
	public Vector<HashMap<String,String>> getRep(String script){
		Vector<HashMap<String,String>> hmr = new Vector<HashMap<String,String>>();
		Pattern pd = Pattern.compile("<dl.*?comment_list S_line1 clearfix.*?\\\\/dl>");
		Matcher md = pd.matcher(script);
		while(md.find()){
			HashMap<String,String> hmtmp = new HashMap<String,String>(11);
			String dl = md.group();
			String mid = dl.substring(dl.indexOf("mid")+6,dl.indexOf("mid")+22);
			hmtmp.put("mid", mid);
			Pattern pdd = Pattern.compile("<dd.*?\\\\/dd>");
			Matcher mdd = pdd.matcher(dl);
			if(mdd.find()){
				String dd = mdd.group();
				String uid = dd.substring(dd.indexOf("\"id=")+4,dd.indexOf("\"id=")+14);
				hmtmp.put("uid", uid);
				String userName = getUserName(dd);
				hmtmp.put("userName", userName);
				Pattern pi = Pattern.compile("<div.*?info clearfix.*?\\/div>");
				Matcher mi = pi.matcher(dd);
				String dinfo = null;
				if(mi.find()){
					dinfo = mi.group();
				}
				String dWithoutInfo = dd.replace(dinfo, "");
				String content = getContent(dWithoutInfo);
				hmtmp.put("content", content);
				String pubTime = getPubTime(dinfo);
				hmtmp.put("pubTime", pubTime);
				String crawlTime = getCrawlTime();
				hmtmp.put("crawlTime", crawlTime);
				String praised = getPraised(dinfo);
				hmtmp.put("praised", praised);
				String repost = getRepost(dinfo);
				hmtmp.put("repost", repost);
				
				//ouid,omid
				String odiv = getOdiv(script);
				String ouid = getOuid(odiv);
				String omid = getOmid(odiv);
				String ouname = getOuname(odiv);
				hmtmp.put("ouid", ouid);
				hmtmp.put("omid", omid);
				hmtmp.put("ouname", ouname);
				
				hmr.add(hmtmp);
				
			}
//			System.out.println(hmtmp.toString());
		}
		return hmr;
	}
	
	public String getOdiv(String script){
		String odiv = null;
		Pattern po = Pattern.compile("<div class=\\\\\"WB_detail.*?WB_from.*?\\\\/div>");
		Matcher mo = po.matcher(script);
		if(mo.find()){
			odiv = mo.group();
		}
		return odiv;
	}
	
	public String getOuid(String odiv){
		String ouid = null;
		Pattern pou = Pattern.compile("ouid=[0-9]{10}");
		Matcher mou = pou.matcher(odiv);
		if(mou.find()){
			ouid = mou.group().substring(5);
		}
		return ouid;
	}
	
	public String getOuname(String odiv){
		String ouname = null;
		Pattern pn = Pattern.compile("nick-name=\\\\\".*?\"");
		Matcher mn = pn.matcher(odiv);
		if(mn.find()){
			ouname = mn.group().substring(12,mn.group().length()-2);
		}
		return ouname;
	}
	
	public String getOmid(String odiv){
		String omid = null;
		Pattern pom = Pattern.compile("mid=\\\\\"[0-9]{16}");
		Matcher mom = pom.matcher(odiv);
		if(mom.find()){
			omid = mom.group().substring(6);
		}
		return omid;
	}
	
	public String getUserName(String dd){
		String userName = null;
		Pattern pa = Pattern.compile("<a.*?usercard.*?\\\\/a>");
		Matcher ma = pa.matcher(dd);
		if(ma.find()){
			String a = ma.group();
			userName = a.substring(a.indexOf(">")+1,a.indexOf("<\\/a>"));
		}
		return userName;
	}
	
	public String getContent(String dWithoutInfo){
		String content = dWithoutInfo;
		Pattern de = Pattern.compile("<em>.*?\\\\/em>");
		Matcher me = de.matcher(content);
		if(me.find()){
			content = me.group();
		}
		Pattern dfilter = Pattern.compile("<.*?>");
		Matcher mfilter = dfilter.matcher(content);
		while(mfilter.find()){
			content = content.replace(mfilter.group(), "");
		}
		Pattern df = Pattern.compile("\\\\/|\\s|\\\\n");
		Matcher mf = df.matcher(content);
		while(mf.find()){
			content = content.replace(mf.group(), "");
		}
		return content;
	}
	
	public String getPubTime(String dinfo){
		String pubTime = null;
		Pattern ps = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}");
		Matcher ms = ps.matcher(dinfo);
		if(ms.find()){
			pubTime = ms.group();
		}
		return pubTime;
	}
	
	public String getCrawlTime(){
		String crawlTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
		Date date = new Date();
		crawlTime = sdf.format(date);
		return crawlTime;
	}
	
	public String getPraised(String dinfo){
		String praised = null;
		Pattern pp = Pattern.compile("<em class=\\\\\"W_ico20 icon_praised_b.*?<\\\\/a>");
		Matcher mp = pp.matcher(dinfo);
		if(mp.find()){
			praised = mp.group();
			if(praised.contains("(")){
				praised = praised.substring(praised.indexOf("(")+1,praised.indexOf(")"));
			}else{
				praised = "0";
			}
		}
		return praised;
	}
	
	public String getRepost(String dinfo){
		String repost = null;
		Pattern pr = Pattern.compile("转发.*?\\\\/a>");
		Matcher mr = pr.matcher(dinfo);
		if(mr.find()){
			repost = mr.group();
			if(repost.contains("(")){
				repost = repost.substring(repost.indexOf("(")+1,repost.indexOf(")"));
			}else{
				repost = "0";
			}
		}
		return repost;
	}
}

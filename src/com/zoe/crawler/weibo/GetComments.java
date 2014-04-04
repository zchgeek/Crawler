package com.zoe.crawler.weibo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * get specific comments of a particular weibo  
 * @author zhanglm
 *
 */
public class GetComments {
	/**
	 * get all comments from a particular Weibo
	 * @param script
	 * @return A vector containing hashmap
	 */
	public Vector<HashMap<String,String>> getComments(String script){
		if(script==null){
			return null;
		}
		Vector<HashMap<String,String>> hmc = getCom(script);
		return hmc;
	}
	
	public Vector<HashMap<String,String>> getCom(String script){
		Vector<HashMap<String,String>> hmc = new Vector<HashMap<String,String>>();
		Pattern pd = Pattern.compile("<dl.*?comment_list S_line1.*?\\\\/dl>");
		Matcher md = pd.matcher(script);
		while(md.find()){
			HashMap<String,String> hmtmp = new HashMap<String,String>(8);
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
				Pattern pi = Pattern.compile("<div.*?info.*?\\/div>");
				Matcher mi = pi.matcher(dd);
				String dinfo = null;
				if(mi.find()){
					dinfo = mi.group();
				}
				String dWithoutInfo = dd.replace(dinfo, "");
				String content = getContent(dWithoutInfo);
				hmtmp.put("content", content);
				String pubTime = getPubTime(dWithoutInfo);
				hmtmp.put("pubTime", pubTime);
				String crawlTime = getCrawlTime();
				hmtmp.put("crawlTime", crawlTime);
				String praised = getPraised(dinfo);
				hmtmp.put("praised", praised);
				String reply = getReply(dinfo);
				hmtmp.put("reply", reply);
				hmc.add(hmtmp);
			}
		}
		return hmc;
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
		Pattern dfd = Pattern.compile("<div.*?WB_media_expand repeat S_line1 S_bg1.*?btn.*?\\\\/div>");
		Matcher mfd = dfd.matcher(content);
		if(mfd.find()){
			content = content.replace(mfd.group(), "");
		}
		Pattern dfilter = Pattern.compile("<.*?>|\\\\n|\\\\t|");
		Matcher mfilter = dfilter.matcher(content);
		while(mfilter.find()){
			content = content.replace(mfilter.group(), "");
		}
		Pattern df = Pattern.compile("\\\\/|\\s");
		Matcher mf = df.matcher(content);
		while(mf.find()){
			content = content.replace(mf.group(), "");
		}
		return content;
	}
	
	public String getPubTime(String dWithoutInfo){
		String pubTime = null;
		Pattern ps = Pattern.compile("<span.*?S_txt2.*?\\\\/span>");
		Matcher ms = ps.matcher(dWithoutInfo);
		if(ms.find()){
			pubTime = ms.group().substring(ms.group().indexOf("(")+1,ms.group().indexOf(")"));
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
	
	public String getReply(String dinfo){
		String reply = null;
		Pattern pr = Pattern.compile("回复.*?\\\\/a>");
		Matcher mr = pr.matcher(dinfo);
		if(mr.find()){
			reply = mr.group();
			if(reply.contains("(")){
				reply = reply.substring(reply.indexOf("(")+1,reply.indexOf(")"));
			}else{
				reply = "0";
			}
		}
		return reply;
	}
	
	
}

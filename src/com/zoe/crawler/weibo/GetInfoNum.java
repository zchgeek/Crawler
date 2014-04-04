package com.zoe.crawler.weibo;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * get information from weibo
 * @author zhanglm
 *
 */
public class GetInfoNum {
	/**
	 * find the script of the full html source
	 * @param content
	 * @return
	 */
	public String getScript(String content){
		String script = null;
		Pattern ps = Pattern.compile("<script.*?pl.content.weiboDetail.index.*?/script>");
		Matcher ms = ps.matcher(content);
		if(ms.find()){
			script = ms.group();
		}
//		System.out.println(script);
		return script;
	}
	
	/**
	 * get changing number 
	 * @param script
	 * @return a hashmap of the number of repost,comments,preased and favorite 
	 */
	public HashMap<String,String> getNum(String content){
		String script = getScript(content);
		System.out.println(script);
		HashMap<String,String> hm = new HashMap<String,String>(4);
		Pattern pd = Pattern.compile("<div class=\\\\\"WB_handle.*?\\\\/div>");
		Pattern pp = Pattern.compile("W_ico20 icon_praised_b.*?\\\\/a>");
		Pattern pr = Pattern.compile("转发.*?<");
		Pattern pc = Pattern.compile("评论.*?<");
		Pattern pf = Pattern.compile("收藏.*?<");
		//find the div which contains everthing
		Matcher md = pd.matcher(script);
		while(md.find()){
			String div = md.group();
			//match praise
			Matcher mp = pp.matcher(div);
			while(mp.find()){
				String tmp = mp.group();
				if(tmp.contains("(")){
					tmp = tmp.substring(tmp.indexOf("(")+1,tmp.indexOf(")"));
				}else{
					tmp = "0";
				}
				hm.put("攒", tmp); 
			}
			//match repost
			Matcher mr = pr.matcher(div);//match praise
			while(mr.find()){
				String tmp = mr.group();
				if(tmp.contains("(")){
					tmp = tmp.substring(tmp.indexOf("(")+1,tmp.indexOf(")"));
				}else{
					tmp = "0";
				}
				hm.put("转发", tmp); 
			}
			//match comment
			Matcher mc = pc.matcher(div);//match praise
			while(mc.find()){
				String tmp = mc.group();
				if(tmp.contains("(")){
					tmp = tmp.substring(tmp.indexOf("(")+1,tmp.indexOf(")"));
				}else{
					tmp = "0";
				}
				hm.put("评论", tmp); 
			}
			//match favorite
			Matcher mf = pf.matcher(div);//match praise
			while(mf.find()){
				String tmp = mf.group();
				if(tmp.contains("(")){
					tmp = tmp.substring(tmp.indexOf("(")+1,tmp.indexOf(")"));
				}else{
					tmp = "0";
				}
				hm.put("收藏", tmp); 
			}
		}
		return hm;
	}
}

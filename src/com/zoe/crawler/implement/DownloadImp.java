package com.zoe.crawler.implement;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zoe.crawler.model.DownloadModel;
/**
 * ruling the download law 
 * @author zhanglm
 *
 */
public class DownloadImp extends DownloadModel {
//	DBBasic db = new DBBasic();
	@Override
	public void downloadArticle(String url) {
		// TODO Auto-generated method stub
		this.exception = null;
		Connection conn = null;
		try{
			conn = Jsoup.connect(url).timeout(10000);
		}catch(IllegalArgumentException e){
			this.exception = "IllegalArgumentException";
			return;
		}
		try {
			Connection.Response response = conn.execute();
			int statusCode = response.statusCode();
			if(statusCode == 200|statusCode == 302|statusCode == 304){
				HashMap<String,String> result = new HashMap<String,String>();
				Document doc = conn.get();
				Element articleDiv = doc.getElementsByAttributeValue("class", "blkContainerSblk").first();
				Element artTitle = articleDiv.getElementById("artibodyTitle");//get article title
				Element artDate = articleDiv.getElementById("pub_date");//get published date
				Element artDiv = articleDiv.getElementById("artibody");//get article body
				String artText = artDiv.ownText();//get text not in any tags
				Elements articleElm = articleDiv.getElementsByTag("p");//get text of p tags
				Date date = new Date();
				String nowDate = date.toString();//get download time
				String content = artText.trim();
				for(Element text:articleElm) 
					content += text.text().trim()+"\n";
				//get origin
				Element div = doc.getElementById("media_name");
				String origin = null;
				try{
					Element a = div.getElementsByTag("a").first();
					origin = a.text();
				}catch(NullPointerException e){
					origin = div.text();
				}
				//get tag
				Element tagDiv = doc.getElementsByAttributeValue("class", "blkBreadcrumbLink").first();
				Element artTag = tagDiv.getElementsByTag("a").first();
				String tag = artTag.ownText(); 
				if(tag==null){
					Element atag = tagDiv.getElementById("articleDiv");
					Element a = atag.getElementsByTag("a").last();
					tag = a.ownText();
				}
				//save result in a hashmap
				if(verification(content)){
					result.put("download_date", nowDate);
					result.put("url", url);
					result.put("title", artTitle.text());
					result.put("content", content);
					result.put("date", artDate.text());
					result.put("origin", origin);
					result.put("tag", tag);
					saveMethod(result);
				}
			}
		} catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			conn = null;
			System.out.println("connection error...");
			conn = null;
			this.exception = "IOException";
		} catch (NullPointerException e){
			conn = null;
			this.exception = "NullPointerException";
		} catch (IOException e){
			conn = null;
			this.exception = "IOException";
		}
	}
	/**
	 * checking the verification content making sure that it could not be blank
	 * @param content
	 * @return
	 */
	public boolean verification(String content){
		boolean flag = true;
		Pattern p = Pattern.compile("^\\s*$");
		Matcher m = p.matcher(content);
		if(m.find()){
			flag = false;
		}
		return flag;
	}

	/**
	 * get article if the news has more than one pages
	 * @param docs
	 * @return extra content
	 * @throws IOException
	 */
	public String getContent(Document docs) throws IOException{
		String content = null;
		try{
			Element e = docs.getElementsByClass("page").first();
			Elements as = e.getElementsByTag("a");
			for(Element a: as){
				String url = a.getElementsByAttribute("href").text();
				Connection conn = Jsoup.connect(url).timeout(10000);
				try {
					Connection.Response response = conn.execute();
					int statusCode = response.statusCode();
					if(statusCode == 200|statusCode == 302|statusCode == 304){
						Document doc = conn.get();
						Element articleDiv = doc.getElementsByAttributeValue("class", "blkContainerSblk").first();
						Element artDiv = articleDiv.getElementById("artibody");//get article body
						String artText = artDiv.ownText();//get text not in any tags
						Elements articleElm = articleDiv.getElementsByTag("p");//get text of p tags
						content = artText.trim();
						for(Element text:articleElm) 
							content += text.text().trim()+"\n";
				}
				}catch(Exception es){
					conn = null;
				}
				conn = null;
			}
			
		}catch(Exception e){
			
		}
		return content;
	}
	@Override
	public void saveMethod(HashMap<String, String> result) {
		// TODO Auto-generated method stub
		String content = result.get("content").replaceAll("'", "''");
		String sql = "INSERT INTO news(title,date,content,url,source,download_date,tag,origin)"
				+ "VALUES ('"+result.get("title")+"','"+result.get("date")+"','"+content+"','"+
				result.get("url")+"','"+"sina news"+"','"+result.get("download_date")
				+"','"+result.get("tag")+"','"+result.get("origin")+"')";
		System.out.println(result.get("url"));
		CrawlerImp.db.insert(sql);
		
	}

}











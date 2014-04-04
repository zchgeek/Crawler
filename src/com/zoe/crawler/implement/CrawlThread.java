package com.zoe.crawler.implement;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Set;

import com.zoe.crawler.implement.HttpParserTool.LinkFilter;
import com.zoe.crawler.model.CrawlThreadModel;
/**
 * the crawling thread containing all the crawling process 
 * such as writing the log, recheck the url, extract urls from 
 * the specific page and download the needed content and save them
 * @author zhanglm
 *
 */
public class CrawlThread extends CrawlThreadModel {
	private String[] regex;
	private String logUrl;
	private int MAX_NUM = 100000;
	private LogImp log = new LogImp();
	/**
	 * class constructor using to pass the regex rules
	 * @param regex
	 */
	public CrawlThread(String[] regex) {
		this.regex = regex;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(LinkQueue.visitedNum <= MAX_NUM) {
			if(LinkQueue.unvisitedURLsEmpty()){
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("waiting for url is interrupted...");
				}
				if(LinkQueue.unvisitedURLsEmpty())
					return;
			}
			String visitUrl = null;
			try {
				visitUrl = getVisitedUrl();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			logUrl = visitUrl+"\t";
			String flag = null;
			flag = downloadProcess(visitUrl);
			if(flag == null) flag = "done";
			logUrl = logUrl+"\t"+flag;
			
			logOperation();
			
			setFilterMethod(visitUrl);
		}
	}
	/**
	 * get an unvisited url from the unvisited url queue
	 * @return the string of url
	 * @throws InterruptedException
	 */
	public synchronized String getVisitedUrl() throws InterruptedException{
		String visitUrl = null;
		while(true)
		{
			try{
				visitUrl = (String)LinkQueue.unvisitedURLDeQueue();
			}catch (NoSuchElementException e){
				sleep(5000);
				continue;
			}
			if(setRecheckMethod(visitUrl))
				continue;
			else{
				SimpleBloomFilter.add(visitUrl);
				break;
			}
		}
		return visitUrl;
	}

	@Override
	public void setFilterMethod(String visitedUrl) {
		// TODO Auto-generated method stub
		LinkFilter filter = new LinkFilter() {//only get URL containing "news.sina.com"
			public boolean accept(String url) {
				boolean flag = false;
				for(int i=0;i<regex.length;i++) {
					if(url.indexOf(regex[i]) != -1 && !url.contains(".PDF"))
						flag = true;
				}
				return flag;
			}
		};
		
		Set<String> links = HttpParserTool.extracLinks(visitedUrl, filter);//extract URLs from visited URL
		for(String link:links) {
			LinkQueue.addUnvisitedURL(link);
		}
	}

	@Override
	public boolean setRecheckMethod(String url) {
		// TODO Auto-generated method stub
		return SimpleBloomFilter.contain(url);
	}

	@Override
	public void logOperation() {
		// TODO Auto-generated method stub
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = myFormat.format(new Date());
		logUrl = logUrl + date +"\n";
		LogQueue.addLogQueue(logUrl);
		logUrl = null;
		log.logWriter();
	}

	@Override
	public String downloadProcess(String url) {
		// TODO Auto-generated method stub
		DownloadImp download = new DownloadImp();
		download.downloadArticle(url);
		return download.exception;
	}

}

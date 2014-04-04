package com.zoe.crawler.implement;


import java.util.HashMap;
import java.util.Vector;

import com.zoe.DBProcess.DBBasic;
import com.zoe.crawler.model.CrawlModel;
import com.zoe.crawler.model.LogModel;
/**
 * the main task of this class is initializing the whole url seeds
 * and build a origin recheck queue. And the most important thing is start 
 * the whole crawling threads
 * @author zhanglm
 *
 */
public class CrawlerImp extends CrawlModel {
	static DBBasic db = new DBBasic();
	LogImp log = new LogImp();
	
	SimpleBloomFilter bf = new SimpleBloomFilter();
	/**
	 * constructor extends the base class
	 * @param threadCount
	 */
	public CrawlerImp(int threadCount) {
		super(threadCount);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initCrawlerSeeds() {
		// TODO Auto-generated method stub
		//init with seeds
		for(int i=0;i<urlSeeds.length;i++) {
			System.out.println("seeds size: "+ String.valueOf(urlSeeds.length));
			LinkQueue.addUnvisitedURL(urlSeeds[i]);
			System.out.println("unvisitedURL size: "+ LinkQueue.unvisitedURLNum());
		}
		//init with log
		String[] name = new String[urlSeeds.length];
		for(int i=0;i<urlSeeds.length;i++){
			name[i] = urlSeeds[i].substring(urlSeeds[i].indexOf("/")+2, urlSeeds[i].indexOf("."));
		}
		Vector<String> urlLog = log.logReader(name);
		if(LogModel.logFlag&& urlLog.size()!=0){
			for(int i=0;i<urlLog.size();i++) {
				LinkQueue.addUnvisitedURL(urlLog.get(i));
			}
		}
		//init with DataBase
		String sql = "select * from news;";
		Vector<HashMap<String,String>> result = db.queryAll(sql);
		for(int i=0;i<result.size();i++){
			SimpleBloomFilter.add(result.get(i).get("url"));
		}
	}

	@Override
	public void crawling(String[] seeds) {
		// TODO Auto-generated method stub
		String logpath = new CrawlConfig().logPath;
		log.setLogPath(logpath);
		setUrlSeeds(seeds);
		initCrawlerSeeds();
		setRegx();
		Thread[] crawlThread = new CrawlThread[this.threadCount];
		for(int i=0;i<crawlThread.length;i++){
			crawlThread[i] = new CrawlThread(this.regex);
			crawlThread[i].start();
		}
	}

	@Override
	public void setUrlSeeds(String[] urlSeeds) {
		// TODO Auto-generated method stub
		this.urlSeeds = urlSeeds;
	}
	

	@Override
	public void setRegx() {
		// TODO Auto-generated method stub
		int len = urlSeeds.length;
		this.regex = new String[len];
		for(int i=0;i<len;i++){
			this.regex[i] = urlSeeds[i].substring(urlSeeds[i].indexOf("/")+2);
		}
	}

}

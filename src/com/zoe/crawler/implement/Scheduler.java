package com.zoe.crawler.implement;

import com.zoe.crawler.model.*;
/**
 * a scheduler control the whole crawling process
 * @author zhanglm
 *
 */
public class Scheduler {
	/**
	 * an inner class that extends SchedulerModel 
	 * @author zhanglm
	 *
	 */
	class Scheduling extends SchedulerModel {
		CrawlConfig config = new CrawlConfig();
		String[] seeds = config.urlSeeds;
		CrawlModel ci = config.cm;
		TimerModel tm = config.tm;
		
		public Scheduling() {
			initSeeds(seeds);
			crawlProcess(ci);
			setTimer(tm);
		}
		
		@Override
		public void initSeeds(String[] seeds) {
			// TODO Auto-generated method stub
			this.urlSeeds = seeds;
		}

		@Override
		public void crawlProcess(CrawlModel cm) {
			// TODO Auto-generated method stub
			this.cm = cm;
		}

		@Override
		public void setTimer(TimerModel tm) {
			// TODO Auto-generated method stub
			this.tm = tm;
		}
	}
	/**
	 * main function
	 * @param args
	 */
	public static void main(String[] args){
		Scheduler s = new Scheduler();
		Scheduling schedul = s.new Scheduling();
		schedul.cm.crawling(schedul.seeds);
	}
}

package com.zoe.crawler.implement;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.zoe.crawler.model.TimerModel;

/**
 * this class contains the definition of time schedule rules
 * @author zhaglm
 *
 */
public class TimeTask extends TimerModel {
	@Override
	public void setTimeTask(){
		Listener listener = new Listener();
		listener.contextInitialized(null);
	}
	/**
	 * monitor the time and determine whether the job should be started
	 * @author zhanglm
	 *
	 */
	public class Listener implements ServletContextListener {
		private Timer timer = null;
		Mytask mytask = new Mytask();
		
		@Override
		public void contextDestroyed(ServletContextEvent arg0) {
			// TODO Auto-generated method stub
			timer.cancel();
		}

		@Override
		public void contextInitialized(ServletContextEvent arg0) {
			// TODO Auto-generated method stub
			timer = new Timer();
			timer.scheduleAtFixedRate(mytask, 0, 2*60*60*1000);
		}
		
	}
	/**
	 * the specific job that needs to be scheduled in a fixed period
	 * @author zhanglm
	 *
	 */
	class Mytask extends TimerTask{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Scheduler.main(null);
		}
		
	}
	
}

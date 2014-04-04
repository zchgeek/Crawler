package com.zoe.crawler.implement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.zoe.crawler.model.*;

/**
 * the implementation of LogModel
 * @author zhanglm
 *
 */
public class LogImp extends LogModel {
	/**
	 * pass parameter of logPath
	 */
	@Override
	public void setLogPath(String logPath) {
		// TODO Auto-generated method stub
		this.logPath = logPath;
	}
	/**
	 * read the exiting log record and find the failed crawling 
	 * urls and add them to unvisited queue
	 */
	@Override
	public Vector<String> logReader(String[] logName) {
		// TODO Auto-generated method stub
		Vector<String> urlLog = new Vector<String>();
		for(int i=0;i<logName.length;i++){
			File log = new File(logPath+logName[i]+"LOG.txt");
			SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long difference = 0L;//time difference between now and the newest log time
			long mindif = 0L;
			long max_logdif = 2 * 60 * 60 * 1000L;
			long max_clawdif = 1 * 60 *60 * 1000L;
			try {
				java.util.Date nowDate = myFormat.parse(myFormat.format(new Date()));
				if(!log.exists()){
					log.createNewFile();
				}else{
					BufferedReader reader = null;
					reader = new BufferedReader(new FileReader(log));
					String tmpLine = null;
					while((tmpLine = reader.readLine())!=null){
						String[] arry = tmpLine.trim().split("\t");
						if(arry.length==3){
							String logTime = arry[arry.length-1];//find log written time
							java.util.Date logDate = myFormat.parse(logTime);
							difference = nowDate.getTime() - logDate.getTime();
							if(difference<mindif) {
								mindif = difference;
							}
							if(difference < max_logdif && arry[1].indexOf("done")==-1){
								urlLog.add(tmpLine.trim().substring(0, tmpLine.indexOf("\t")));
							}
						}else{
							urlLog.add(arry[0]);
						}
					}
				}
			}catch(java.text.ParseException e) {
				System.out.println("Parsing Time Format wrong...");
				e.printStackTrace();
			}catch(IOException e){
				System.out.println("There is something wrong with LOG.txt...");
				e.printStackTrace();
			}
			if(mindif > max_clawdif){
				LogImp.logFlag = false;//using urlSeeds
			}else{
				LogImp.logFlag = true;//not using urlSeeds
			}
		}
		return urlLog;
	}
	/**
	 * write into log file with urls and their completing statement
	 */
	@Override
	public synchronized void logWriter() {
		// TODO Auto-generated method stub
		try {
			FileWriter fw = null;
			if(LogQueue.isLogQueueEmpty()){
				return;
			}else{
				while(!LogQueue.isLogQueueEmpty()) {
					String logUrl = (String)LogQueue.deLogQueue();
					String name = null;
					try{
						name = logUrl.substring(logUrl.indexOf("/")+2, logUrl.indexOf("."));
					}catch(StringIndexOutOfBoundsException e){
						System.out.println(logUrl);
						continue;
					}
					String path = this.logPath+name;
					File f = new File(path);
					fw = new FileWriter(f,true);
					fw.write(logUrl);
					fw.close();
				}
				
			}
			
		}catch(IOException e){
			System.out.println("LOG does not exit...");
			e.printStackTrace();
		}
	}
	
}

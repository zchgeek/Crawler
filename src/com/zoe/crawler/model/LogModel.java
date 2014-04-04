package com.zoe.crawler.model;

import java.util.Vector;
/**
 * a class containing all the operations with log
 * @author zhanglm
 *
 */
public abstract class LogModel {
	/**saving log path*/
	public String logPath;
	/**boolean type*/
	public static boolean logFlag = false;
	/**
	 * set log path
	 * @param logPath
	 */
	public abstract void setLogPath(String logPath);
	/**
	 * read the log of the specific logname
	 * @param logName
	 * @return result vector
	 */
	public abstract Vector<String> logReader(String[] logName);
	/**
	 * write log method
	 */
	public abstract void logWriter();

}

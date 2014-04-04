package com.zoe.crawler.model;

import java.util.HashMap;
/**
 * a class being used to download website from a specific url
 * @author zhanglm
 *
 */
public abstract class DownloadModel {
	/**whether the process ended abruptly*/
	public String exception = null;
	/**
	 * definite method that to parse page and get content
	 * @param url
	 */
	public abstract void downloadArticle(String url);
	/**
	 * definite saving content method whether to using database for local file
	 * @param results
	 */
	public abstract void saveMethod(HashMap<String,String> results);
}

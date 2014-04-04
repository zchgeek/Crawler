package com.zoe.crawler.implement;
/**
 * an simple hash definition
 * @author zhanglm
 *
 */
public class SimpleHash {
	private int cap;
	private int seed;
	/**
	 * constructor
	 * @param cap
	 * @param seed
	 */
	public SimpleHash(int cap, int seed) {
		this.cap = cap;
		this.seed = seed;
	}
	
	public int hash(String value) {
		int result = 0;
		int len = value.length();
		for (int i=0;i<len;i++) {
			result = seed *result + value.charAt(i);
		}
		return (cap-1) & result;
	}
}

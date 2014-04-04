package com.zoe.crawler.implement;

import java.util.BitSet;
/**
 * the realizing of simple bloom filter
 * and also see principle as follow:
 * http://www.eecs.harvard.edu/~michaelm/postscripts/ton2002.pdf
 * http://blog.csdn.net/jiaomeng/article/details/1495500
 * @author admin
 *
 */
public class SimpleBloomFilter {
	private static final int DEFAULT_SIZE = 2 << 20;
	private static final int[] seeds = new int[] {2, 5, 7, 11, 13, 23, 31, 37, 41, 47, 61, 71, 89};
	private static BitSet bits = new BitSet(DEFAULT_SIZE);
	public static SimpleHash[] func = new SimpleHash[seeds.length];
	
	public SimpleBloomFilter() {
		for(int i=0;i<seeds.length;i++) {
			func[i] = new SimpleHash(DEFAULT_SIZE,seeds[i]);
		}
	}
	
	public static synchronized void add(String value) {
		for (SimpleHash f: func) {
			bits.set(f.hash(value), true);
		}
	}
	
	public static synchronized boolean contain(String value) {
		if(value==null)
			return false;
		boolean ret = true;
		for(SimpleHash f: func) {
			ret = ret && bits.get(f.hash(value));
		}
		return ret;
	}
}

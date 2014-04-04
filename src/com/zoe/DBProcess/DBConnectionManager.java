package com.zoe.DBProcess;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
/**
 * DBConnectionManager is a class being used to 
 * construct some database connection pools and also
 * containing some operations of the pools
 * @author zhanglm
 *
 */
public class DBConnectionManager {
	/**an instance of class*/
	static private DBConnectionManager instance;
	/**set database connection timeout*/
	private long timeout;
	/**get a database connection pools matching hashmap type*/
	private HashMap<String,DBConnectionPool> pools = new HashMap<String,DBConnectionPool>();
	/**
	 * class constructor
	 * @param dbc DBConfig parameter
	 */
	public DBConnectionManager(DBConfig...dbc){
		this.createPools(dbc);
	}
	/**
	 * get a DBConnectionManager instance
	 * @return DBConnectionManager instance
	 */
	public static synchronized DBConnectionManager getInstance(){
		if(instance==null){
			instance = new DBConnectionManager();
		}
		return instance;
	}
	/**
	 * using name to get specific DBPool and get the free connection
	 * @param name the DBConnectionPool name using to differentiate 
	 * 		different DBPool
	 * @return Connection from DBPool
	 */
	public synchronized Connection getConnection(String name){
		DBConnectionPool pool = null;
		Connection conn = null;
		pool = (DBConnectionPool)pools.get(name);
		conn = pool.getConnection(timeout);
		return conn;
	}
	/**
	 * set the specific connection free so that it could reuse afterwards
	 * @param name the DBConnectionPool name using to differentiate 
	 * 		different DBPool
	 * @param conn Connection from DBPool
	 */
	public synchronized void freeConnection(String name,Connection conn){
		DBConnectionPool pool = pools.get(name);
		pool.freeConnection(conn);
	}
	/**
	 * release all of the connections of the specific DBPool according to the name
	 * @param name the DBConnectionPool name using to differentiate 
	 * 		different DBPool
	 */
	public synchronized void release(String name){
		if(name=="all"){
			Iterator<Entry<String,DBConnectionPool>> allpools = pools.entrySet().iterator();
			while(allpools.hasNext()){
				DBConnectionPool pool = (DBConnectionPool)allpools.next();
				if(pool!=null) pool.release();
			}
			pools.clear();
		}else{
			DBConnectionPool pool = pools.get(name);
			if(pool!=null) pool.release();
			pools.remove(name);
			if(pools.isEmpty()) pools.clear();
		}
	}
	/**
	 * create database connection pool using parameters of DBConfig
	 * @param dbc DBConfig
	 */
	public void createPools(DBConfig...dbc){
		int ind = 0;
		while(ind<dbc.length){
			DBConnectionPool dbcp = new DBConnectionPool();
			dbcp.setDriver(dbc[ind].dirver);
			dbcp.setName(dbc[ind].name);
			dbcp.setPassword(dbc[ind].pwd);
			dbcp.setUrl(dbc[ind].url);
			dbcp.setUser(dbc[ind].user);
			dbcp.setMaxConn(dbc[ind].maxConn);
			dbcp.setMinConn(dbc[ind].minConn);
			dbcp.setConnection(dbc[ind].connection);
			dbcp.initPools();
			pools.put(dbc[ind].name, dbcp);
			ind++;
		}
	}
}







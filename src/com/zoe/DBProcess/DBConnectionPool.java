package com.zoe.DBProcess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * DBConnectionPool is a class being used to initialize
 * one database pool and also containing some basic operations 
 * @author zhanglm
 *
 */
public class DBConnectionPool {
	/**record data connection number that has been in use*/
	private int inUsed = 0;
	/**free connections in array list that could be used*/
	private ArrayList<Connection> freeConnections = new ArrayList<Connection>();
	/**the minimum number of connections*/
	private int minConn;
	/**the maximum number of connections*/
	private int maxConn;
	private String password;
	private String name;
	private String url;
	private String driver;
	private String user;
	private String connection;
	/**
	 * set the specific connection free and put it into free
	 * array list
	 * @param conn database connection 
	 */
	public synchronized void freeConnection(Connection conn){
		this.freeConnections.add(conn);
		this.inUsed--;
	}
	/**
	 * initialize database connection pool 
	 * and construct connections of minimum number
	 */
	public void initPools(){
		Connection conn = null;
		while(freeConnections.size()<minConn){
			conn = newConnection();
			if(conn!=null){
				freeConnections.add(conn);
			}
		}
	}
	/**
	 * get a connection from database pool
	 * @param timeout set connection time out
	 * @return connection
	 */
	public synchronized Connection getConnection(long timeout){
		Connection conn = null;
		if(this.freeConnections.size()>0){
			conn = this.freeConnections.get(0);
			if(conn==null) 
				conn = getConnection(timeout);
			this.freeConnections.remove(0);
		}else if(inUsed >= maxConn){
			conn = null;
		}else{
			conn = newConnection();
			if(conn!=null){
				this.inUsed++;
			}
		}
		return conn;
	}
	/**
	 * get a new connection. If the pools has been full, 
	 * then return null
	 * @return connection
	 */
	public synchronized Connection newConnection(){
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(connection);
//			conn = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("sorry, cannot find the driver...");
		} catch (SQLException e){
			e.printStackTrace();
			System.out.println("sorry,cannot get the connection...");
		}
		return conn;
	}
	/**
	 * release all connections from pool
	 */
	public synchronized void release(){
		Iterator<Connection> allConns = this.freeConnections.iterator();
		while(allConns.hasNext()){
			Connection conn = allConns.next();
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("connection cannot be released...");
			}
		}
		this.freeConnections.clear();
	}
	/**
	 * set database pool's connection
	 * @param connection
	 */
	public void setConnection(String connection){
		this.connection = connection;
	}
	/**
	 * get database pool's connection's information
	 * @return
	 */
	public String getConnection(){
		return connection;
	}
	/**
	 * set database pool's name
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}
	/**
	 * get database pool's name
	 */
	public String getName(){
		return name;
	}
	/**
	 * set database pool's driver
	 * @param driver
	 */
	public void setDriver(String driver){
		this.driver = driver;
	}
	/**
	 * get database pool's driver
	 */
	public String getDriver(){
		return driver;
	}
	/**
	 * set database pool's password
	 * @param pwd
	 */
	public void setPassword(String pwd){
		this.password = pwd;
	}
	/**
	 * get database pool's password
	 */
	public String getPassword(){
		return password;
	}
	/**
	 * set database pool's user
	 * @param user
	 */
	public void setUser(String user){
		this.user = user;
	}
	/**
	 * get database pool's user
	 */
	public String getUser(){
		return user;
	}
	/**
	 * set database pool's url
	 * @param url
	 */
	public void setUrl(String url){
		this.url = url;
	}
	/**
	 * get database pool's url
	 */
	public String getUrl(){
		return url;
	}
	/**
	 * set database pool's max number of connection
	 * @param maxConn
	 */
	public void setMaxConn(int maxConn){
		this.maxConn = maxConn;
	}
	/**
	 * get database pool's max connection
	 */
	public int getMaxConn(){
		return maxConn;
	}
	/**
	 * set database pool's min number of connection
	 * @param minConn
	 */
	public void setMinConn(int minConn){
		this.minConn = minConn;
	}
	/**
	 * get database pool's min connection
	 */
	public int getMinConn(){
		return minConn;
	}
}













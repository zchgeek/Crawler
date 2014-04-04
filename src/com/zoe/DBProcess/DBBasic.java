package com.zoe.DBProcess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;


/**
 * DBBasic is a class containing some basic operations of database,
 * like insert,delete,query etc.
 * @author zhanglm
 *
 */
public class DBBasic {
	private Connection conn;
	private Statement stmt;
	
	private String name;
	private DBConfig db;
	private DBConnectionManager dbm;
	
	/**
	 * constructor using to initialize private parameters 
	 * such as DBConnectionManager with class DBConfig
	 */
	public DBBasic(){
		this.db = new DBConfig();
		this.dbm = new DBConnectionManager(db);
		this.name = db.name;
	}
	/**
	 * insert into database 
	 * @param sql insertion statement
	 * @return boolean flag showing whether succeed
	 */
	public synchronized boolean insert(String sql){
		boolean flag = false;
		conn = dbm.getConnection(name);
		try {
			stmt = (Statement)conn.createStatement();
			stmt.executeUpdate(sql);
			dbm.freeConnection(name, conn);
			flag = true;
			System.out.println("insert one record successful~~");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dbm.freeConnection(name, conn);
		}
		return flag;
	}
	/**
	 * update one record in database
	 * @param sql update database
	 * @return boolean flag
	 */
	public synchronized boolean update(String sql){
		boolean flag = false;
		conn = dbm.getConnection(name);
		try {
			stmt = (Statement)conn.createStatement();
			stmt.executeUpdate(sql);
			dbm.freeConnection(name, conn);
			flag = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dbm.freeConnection(name, conn);
		}
		return flag;
	}
	/**
	 * query records satisfying some conditions
	 * @param sql query statement
	 * @return boolean flag
	 */
	public synchronized boolean query(String sql){
		boolean flag = false;
		conn = dbm.getConnection(name);
		try {
			stmt = (Statement) conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				flag = true;
				System.out.println(rs.toString());
			}
			dbm.freeConnection(name, conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("query failed...");
			dbm.freeConnection(name, conn);
		}
		
		return flag;
	}
	/**
	 * get all records from the table
	 * @param sql get all records statement
	 * @return a Vector that containing all result set
	 */
	public synchronized Vector<HashMap<String,String>> queryAll(String sql){
		Vector<HashMap<String,String>> results = new Vector<HashMap<String,String>>();
		conn = dbm.getConnection(name);
		try {
			stmt = (Statement) conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				ResultSetMetaData rsmd = rs.getMetaData() ; 
				int columnCount = rsmd.getColumnCount();
				HashMap<String,String> tmp = new HashMap<String,String>(columnCount);
				for(int i=1;i<=columnCount;i++){
					tmp.put(rsmd.getColumnName(i), rs.getString(i));
				}
				results.add(tmp);
			}
			dbm.freeConnection(name, conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("query failed...");
			dbm.freeConnection(name, conn);
		}
		return results;
	}
	/**
	 * delete some records that satisfying some conditions
	 * @param sql delete statement
	 * @return boolean flag
	 */
	public synchronized boolean delete(String sql){
		boolean flag = false;
		conn = dbm.getConnection(name);
		try {
			stmt = (Statement) conn.createStatement();
			stmt.executeUpdate(sql);
			dbm.freeConnection(name, conn);
			flag = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("delete failed...");
			dbm.freeConnection(name, conn);
		}
		return flag;
	}
	/**
	 * get total number from a table
	 * @param sql getTotalNum statement
	 * @return the number of all records
	 */
	public synchronized int getTotalNum(String sql){
		int sum = 0;
		conn = dbm.getConnection(name);
		try {
			stmt = (Statement) conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				sum++;
			}
			dbm.freeConnection(name, conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("query failed...");
			dbm.freeConnection(name, conn);
		}
		return sum;
	}
}

package com.zoe.DBProcess;
/**
 * DBConfig used to configure some parameters of database operations
 * @author zhanglm
 *
 */
public class DBConfig {
	public String dirver = "";//database driver, e.g. com.mysql.jdbc.Driver
	public String url = "";//database url
	public String name = "";//database table name
	public String user = "";//user name
	public String pwd = "";//password
	//when come up with the charset problem, the connection part is a solution, remember adding characterEncoding part
	public String connection = "";//e.g. jdbc:mysql://localhost:3306/newsDB?user=root&password=123&useUnicode=true&characterEncoding=utf8
	public int maxConn = 20;//the maximum connection number of database
	public int minConn = 10;//the minimum connection number of database
}

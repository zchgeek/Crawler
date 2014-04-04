## INTRUDUCTION ##
This java project is a web crawler tools. 

It has four packages which contain a framework of the web craler and a database
management pools and the implementation of the framework. And we use it mainly to crawl www.news.sina.com.cn pages and 
other branches of it. 

The fourth package is the implementation of the sina weibo crawling containing weibo mock sign in, 
crawling of the specific weibo reposts and comments and the contents of the s.weibo.com pages.
_________________________________________________________________

## MODULE INSTRUCTIONS ##
+ package [com.zoe.crawler.model]
  ```java
  public abstract class SchedulerModel;
  ```
  It contains a frame work of crawling process. Therefore it has many abstract classes. If you want to use this frame you 
  should extends the classes and override their functions. It's mainly used to initiate the url seeds, call crawlProcess and 
  define time tasks like crawling pages according to the regular time period if you want to.
  ```java
  public abstract class CrawlModel;
  public abstract class TimerModel;
  
  public abstract class CrawlThreadModel;
  public abstract class LogModel;
  public abstract class DownloadModel;
  ```
  The abstract class CrawlModel defines the number of the thread using to crawl pages and call the Log operations to initialize
  the crawl queue and download the file that meet the needs. TimerModel usually should contains two inner classes that 
  implements ServeletContextListner and TimerTask interface which have been embeded in the java lib.
  The CrawlThreadModel should instantiate class DownloadModel and call the log operations and define the url recheck rules.
  The DownloadModel should deal with the specific url source code and decide the ways to save the extracted contents.
  The LogModel should document the url status during the crawling process, and dealing with the restoration process when the 
  program break down. Also could use it to define url rechecking process.
  
+ package [com.zoe.DBProcess]
  this package defines some database pools and the basic operations of operating database.
  ```java
  public class DBConnectionManager;
  public class DBConnectionPool;
  public class DBBasic;
  public class DBConfig;
  ```
  DBConnectionManager initalize different DBConnectionPools with different names and the Pools could have their own connection
  pools using different connection ports. 
  DBConnectionPool define a database connection pool and some operations like get connection from pool and so on.
  DBBasic contains a lot of database operations such as insert, query, delete and so on. 
  DBConfig is used to initialize the parameters related to database pools and construct database connections.
  
+ package [com.zoe.crawler.implementation]
  This package extends the package [com.zoe.crawler.model], and implement an instantiation used to crawl news.sina.com.cn
  pages and its subpages.Then using MySQL to save the extracted contents and some information.Using log to initialize 
  url seeds and using bloom filter to recheck url queue.
  The package contains main function and could run if you configure all the parameters.
  
+ package [com.zoe.crawler.weibo]
  This package contains some operations about sinaweibo crawling, such as crawling comments and reposts for the specific weibo id, and crawling 
  s.weibo.com with keywords etc.
  
________________________________________________________________

## HOW TO USE ##

You could take the package [com.zoe.crawler.implementation] as an example to crawl, and replace the class DownloadImp and 
url seeds and filter rules to costomize your own mini crawler.

__________________________________________________________________

## DEPENDENCY ##
The .jar files which are required for the package have been contained in the file lib. 

______________________________________________________________________

## BUG REPORT && SUGGESTIONS ##
+ zchgeek@gmail.com
  


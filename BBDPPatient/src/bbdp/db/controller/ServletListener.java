package bbdp.db.controller;
 
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import bbdp.push.fcm.PushTimerServer;
 

public class ServletListener implements ServletContextListener {
 

    @Override
    public void contextInitialized(ServletContextEvent event) {
 
    	ServletContext sc = event.getServletContext();		//取得Context裡的資料
    	 
    	String url = sc.getInitParameter("url");
    	String user_name = sc.getInitParameter("user_name");
    	String password = sc.getInitParameter("password");
    	String database = sc.getInitParameter("database");
    	//http://www.openjry.url.tw/index.php/2016/09/11/apache-tomcat-jdbc-connection-pool-setting/
    	PoolProperties p = new PoolProperties();
    	p.setUrl(url + database + "?characterEncoding=UTF-8");
    	p.setDriverClassName("com.mysql.jdbc.Driver");
        p.setUsername(user_name);
        p.setPassword(password);
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(100);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
                  "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                  + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer"
                  );
        DataSource datasource = new DataSource();
        datasource.setPoolProperties(p);       

    	sc.setAttribute("db", datasource);
    }
 

    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }
 
}
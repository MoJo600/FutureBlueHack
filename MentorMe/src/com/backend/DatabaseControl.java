package backend;

import java.sql.*;
import java.util.Set;

import com.ibm.nosql.json.api.BasicDBList;
import com.ibm.nosql.json.api.BasicDBObject;
import com.ibm.nosql.json.util.JSON;


import admin.ServerProperties;


public class DatabaseControl
 {
	
	private String dataBaseUrl;
	private static DatabaseControl instance = null;
	
	//default setting
	private String databaseHost = ServerProperties.domain;
	private int port = ServerProperties.port;
	private String databaseName = ServerProperties.mainDataBase;
	private String user = ServerProperties.userName;
	private String password = ServerProperties.password;
	private String url = "myurl";
	
	public static DatabaseControl getInstance() {
	      if(instance == null) {
	         instance = new DatabaseControl();
	      }
	      return instance;
	}
	
	
	private DatabaseControl(){
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");

		if (VCAP_SERVICES != null) {
			// parse the VCAP JSON structure
			BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
			String thekey = null;
			Set<String> keys = obj.keySet();
			// Look for the VCAP key that holds the SQLDB information
			for (String eachkey : keys) {
				// Just in case the service name gets changed to lower case in the future, use toUpperCase
				if (eachkey.toUpperCase().contains("SQLDB")) {
					thekey = eachkey;
				}
			}
			if (thekey == null) {
				return;
			}
			BasicDBList list = (BasicDBList) obj.get(thekey);
			obj = (BasicDBObject) list.get("0");
			// parse all the credentials from the vcap env variable
			obj = (BasicDBObject) obj.get("credentials");
			databaseHost = (String) obj.get("host");
			databaseName = (String) obj.get("db");
			port = (int)obj.get("port");
			user = (String) obj.get("username");
			password = (String) obj.get("password");
			url = (String) obj.get("jdbcurl");
		}
	}
	
	
	
	public  Connection getConnection() throws ClassNotFoundException, SQLException{
		//loading drivers for mysql
		try{
		Class.forName("com.ibm.db2.jcc");
		}catch(Exception e1){
		Class.forName("com.mysql.jdbc.Driver");
		}
		Connection con = DriverManager.getConnection(url, user ,password);
		
		
		 //creating connection with the database
		/**
		return DriverManager.getConnection
                ("jdbc:mysql://" + ServerProperties.domain
                		+ ":"+ServerProperties.port + "/"+ ServerProperties.mainDataBase
                		,ServerProperties.userName,ServerProperties.password);
                		*/
		return con;
	}
	
	
     public  boolean checkUser(String database, String name,String pass) 
     {
      boolean st =false;
      try{


 	 //creating connection with the database 
         Connection con= getConnection();
         PreparedStatement ps =con.prepareStatement
                             ("select * from ? where name=? and pass=?");
         ps.setString(1, database);
         ps.setString(2, name);
         ps.setString(3, pass);
         ResultSet rs =ps.executeQuery();
         st = rs.next();
        
      }catch(Exception e)
      {
          e.printStackTrace();
      }
         return st;                 
  }   
     public ResultSet getUser(String database, String email){
    	 boolean st =false;
    	 try{

        	 Connection con= getConnection();
             PreparedStatement ps =con.prepareStatement
                                 ("select * from ? where email=?");
             ps.setString(1, database);
             ps.setString(2, email);
             ResultSet rs =ps.executeQuery();
             st = rs.next();
             if (st){
            	 return rs;
             }
            
          }catch(Exception e)
          {
              e.printStackTrace();
          }
    	 return null;
     }
     public  boolean checkExistence(String database, String email) 
     {
      boolean st =false;
      try{

    	 Connection con= getConnection();
         PreparedStatement ps =con.prepareStatement
                             ("select * from ? where email=?");
         ps.setString(1, database);
         ps.setString(2, email);
         ResultSet rs =ps.executeQuery();
         st = rs.next();
        
      }catch(Exception e)
      {
          e.printStackTrace();
      }
         return st;                 
  }   
     
     public  ResultSet GetAllUsers(String database) 
     {
      try{

    	 Connection con= instance.getConnection();
         PreparedStatement ps =con.prepareStatement
                             ("select * from ?");
         ps.setString(1, database);
         ResultSet rs =ps.executeQuery();
         return rs;
        
      }catch(Exception e)
      {
          e.printStackTrace();
      }
         return null;                 
  }   
     
     
}
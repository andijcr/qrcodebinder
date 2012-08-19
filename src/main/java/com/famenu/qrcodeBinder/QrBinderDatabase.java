package com.famenu.qrcodeBinder;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class QrBinderDatabase {

	final static String CREATE_LAST_HASH_TABLE= "CREATE TABLE IF NOT EXISTS \"lastHashIdIndex\" ( value bigint ) WITH (OIDS = FALSE);";
	
	Connection dbConnection;
	
	QrBinderDatabase(URI db) throws SQLException{
        String username = db.getUserInfo().split(":")[0];
        String password = db.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + db.getHost() + db.getPath();

        dbConnection= DriverManager.getConnection(dbUrl, username, password);
	}
	
	public void initSchema() throws SQLException{
		Statement init=dbConnection.createStatement();
		init.execute(CREATE_LAST_HASH_TABLE);
	}
	
}

package com.famenu.qrcodeBinder;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class QrBinderDatabase {

	/**
	 * table lastHashIdIndex ( value )  
	 * table tagToUrlMap (tag, url, coupled) dove coubled e un booleano che indica se al tag e` associato un url
	 */
	final static String LAST_HASH_TABLE = "\"lastHashIdIndex\"";
	final static String CREATE_LAST_HASH_TABLE = "CREATE TABLE IF NOT EXISTS " + LAST_HASH_TABLE + " ( value bigint ) WITH (OIDS = FALSE);";
	final static String SIZE_LAST_HASH_TABLE = "select count(*) from " + LAST_HASH_TABLE + ";";
	final static String INSERT_LAST_HASH_SEED ="insert into " + LAST_HASH_TABLE + " values (1337);";
	final static String GET_HASH_ID = "select value from " + LAST_HASH_TABLE + " order by value desc;";
	final static String SET_HASH_ID = "update "  + LAST_HASH_TABLE + " set value=? where value= (select value from  "+ LAST_HASH_TABLE + " limit 1);";

	final static String TAG_TO_URL_TABLE = "\"tagToUrlMap\"";
	final static String CREATE_TAG_TO_URL_TABLE = "CREATE TABLE IF NOT EXISTS " + TAG_TO_URL_TABLE + " ( tag character varying(7) NOT NULL, url text, coupled boolean DEFAULT false, PRIMARY KEY (tag) ) WITH ( OIDS = FALSE );";
	final static String INSERT_NEW_TAG = "insert into " + TAG_TO_URL_TABLE + " (tag) values (?);";

	final static String SELECT_TAG = "select tag, url, coupled from " + TAG_TO_URL_TABLE + " where tag= ?;";
	final static String UPDATE_TAG = "update " + TAG_TO_URL_TABLE + " set url=?, coupled=TRUE where tag=?;";

	public class CatastroficStorageException extends Exception{};
	public class DepletedStorageException extends Exception{};

	Connection dbConnection;

	QrBinderDatabase(URI db) throws SQLException{
		String username = db.getUserInfo().split(":")[0];
		String password = db.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + db.getHost() + db.getPath();

		dbConnection= DriverManager.getConnection(dbUrl, username, password);
	}

	public void initSchema() throws CatastroficStorageException{
		try {
			Statement init;
			init = dbConnection.createStatement();
			//create table designated to save the integer used to generate the last qrcode produced
			init.execute(CREATE_LAST_HASH_TABLE);

			//insert if not exist the base integer to generate qrcodes
			ResultSet rs=init.executeQuery(SIZE_LAST_HASH_TABLE);
			rs.next();
			if(rs.getInt(1)==0){	//if the table contains 0 rows, we insert the seed
				init.executeUpdate(INSERT_LAST_HASH_SEED);
			}
			rs.close();
			//create table designate to map a tag to a different url
			init.execute(CREATE_TAG_TO_URL_TABLE);

			init.close();
		} catch (SQLException e) {
			throw new CatastroficStorageException();
		}
	}

	public int getLastHashId() throws CatastroficStorageException{
		try {
			Statement lastId;
			lastId = dbConnection.createStatement();

			ResultSet rs=lastId.executeQuery(GET_HASH_ID);
			rs.next();
			int result=rs.getInt(1);
			lastId.close();
			rs.close();
			return result;
		} catch (SQLException e) {
			throw new CatastroficStorageException();
		}
	}

	public void insertLastHashId(int value) throws CatastroficStorageException{
		try {
			PreparedStatement lastId=dbConnection.prepareStatement(SET_HASH_ID);
			lastId.setInt(1, value);
			lastId.executeUpdate();
			lastId.close();
		} catch (SQLException e) {
			throw new CatastroficStorageException();
		}
	}

	public void insertNewTags(ArrayList<String> tags) throws CatastroficStorageException, DepletedStorageException {
		PreparedStatement newTag=null;
		try{
			newTag=dbConnection.prepareStatement(INSERT_NEW_TAG);
		} catch (SQLException e) {
			throw new CatastroficStorageException();
		}
		try{
			for(String t: tags){
				newTag.setString(1, t);
				newTag.addBatch();
			}

			newTag.executeBatch();

			newTag.close();
		} catch (SQLException e) {
			throw new DepletedStorageException();
		}
	}

	public boolean isTagAssociable(String tag) throws CatastroficStorageException {
		try{
			PreparedStatement selTag=dbConnection.prepareStatement(SELECT_TAG);
			selTag.setString(1, tag);

			ResultSet rs=selTag.executeQuery();

			boolean free=false;

			if(rs.next()){
				free = !rs.getBoolean(3);
			}

			selTag.close();
			rs.close();
			return 	free;	//ritorna la negazione della collonna coupled. coupled e` false per i tag vergini.
		} catch (SQLException e) {
			throw new CatastroficStorageException();
		}
	}

	public void associateTagUrl(String tag, String url) throws CatastroficStorageException {
		try{
			PreparedStatement upTag=dbConnection.prepareStatement(UPDATE_TAG);
			upTag.setString(1, url);
			upTag.setString(2, tag);

			upTag.executeUpdate();

			upTag.close();
		} catch (SQLException e) {
			throw new CatastroficStorageException();
		}
	}

	public String getUrlFromTag(String tag) throws CatastroficStorageException {
		try{
			PreparedStatement selTag=dbConnection.prepareStatement(SELECT_TAG);
			selTag.setString(1, tag);

			ResultSet rs=selTag.executeQuery();

			String url=null;
			if(rs.next() && rs.getBoolean(3)){			//se esiste la riga e il tag e` coupled
				url=rs.getString(2);
			}

			selTag.close();
			rs.close();
			return url;
		} catch (SQLException e) {
			throw new CatastroficStorageException();
		}
	}
}

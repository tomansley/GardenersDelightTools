package com.gdelight.tools.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public abstract class BaseDAO {

	//these are the JNDI connection pool names for the databases
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	protected static final String PROP_CONTEXT_LOOKUP = "lookup";
	protected static final String PROP_DB_URL = "db_url";
	protected static final String PROP_DB_USERNAME = "db_username";
	protected static final String PROP_DB_PASSWORD = "db_password";
	protected static final String PROP_DB_CLASS = "db_class";

	private static Logger log = Logger.getLogger(BaseDAO.class);
	private Connection con;

	/**
	 * Method to try and close all connections.
	 */
	protected void close() {
		try {
			if (con != null) {
				con.close();
				con = null;
			}
		} catch (SQLException e) { }
	}

	/**
	 * Convenience method.  If data needs to be written to a single table then this method can 
	 * be used rather than having to create a whole new DAO.
	 * @param database the database the data is to be retrieved from.
	 * @param tablename the table the data is to be retrieved from.
	 * @param properties the properties used for the INSERT SQL statement.
	 * @return whether the data was written successfully or not.
	 * @throws SQLException
	 */
	protected boolean addSimpleData(String tablename, Properties properties) throws SQLException {
		log.debug("Starting addSimpleData");
		
		boolean result = false;

		try {

			//put the properties into a linked hashmap which is ordered.
			Map<String,Object> orderedProps = new LinkedHashMap<String,Object>();
			for (Object field: properties.keySet()) {
				orderedProps.put((String) field, properties.get(field));
			}

			//INSERT INTO (
			StringBuffer sql = new StringBuffer("INSERT INTO " + tablename + "(");

			//field1, field2, field3 .....
			for (String key: orderedProps.keySet()) {
				sql.append(key + ",");
			}
			sql.delete(sql.lastIndexOf(","), sql.length()); //get rid of the last comma

			//) VALUES (
			sql.append(") VALUES (");
			
			//value1, value2, value3 .....
			for (String key: orderedProps.keySet()) {
				if (orderedProps.get(key) instanceof Integer || orderedProps.get(key) instanceof Long) {
					sql.append(orderedProps.get(key));
				} else {
					sql.append("'" + orderedProps.get(key) + "'");
				}
				sql.append(", ");
			}
			sql.delete(sql.lastIndexOf(","), sql.length()); //get rid of the last comma
			sql.append(")");
			
			log.debug("SQL = " + sql);
			PreparedStatement ps = getConnection().prepareStatement(sql.toString());

			int count = ps.executeUpdate(sql.toString());

			if (count > 0) {
				result = true;
			} 
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close();
		}

		return result;

	}
	
	/**
	 * Convenience method.  If data needs to be written to a single table then this method can 
	 * be used rather than having to create a whole new DAO.
	 * @param database the database the data is to be retrieved from.
	 * @param tablename the table the data is to be retrieved from.
	 * @param properties the properties used for the INSERT SQL statement.
	 * @return whether the data was written successfully or not.
	 * @throws SQLException
	 */
	protected boolean updateSimpleData(String database, String tablename, Properties keys, Properties properties) throws SQLException {
		log.debug("Starting updateSimpleData");
		
		boolean result = false;

		try {

			StringBuffer sql = new StringBuffer("UPDATE " + tablename + " SET ");

			for (Object key: properties.keySet()) {
				sql.append((String) key + " = ");
				if (properties.get(key) instanceof Integer || properties.get(key) instanceof Long) {
					sql.append(properties.get(key));
				} else {
					sql.append("'" + properties.get(key) + "'");
				}
				sql.append(", ");
			}
			sql.delete(sql.lastIndexOf(","), sql.length()); //get rid of the last comma
			sql.append(" WHERE ");
			
			for (Object key: keys.keySet()) {
				sql.append((String) key + " = ");
				if (keys.get(key) instanceof Integer || keys.get(key) instanceof Long) {
					sql.append(keys.get(key));
				} else {
					sql.append("'" + keys.get(key) + "'");
				}
				sql.append(", ");
			}
			sql.delete(sql.lastIndexOf(","), sql.length()); //get rid of the last comma
			
			log.debug("SQL = " + sql);
			PreparedStatement ps = getConnection().prepareStatement(sql.toString());

			int count = ps.executeUpdate(sql.toString());

			if (count > 0) {
				result = true;
			} 
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close();
		}

		return result;

	}

	/**
	 * Method which is more for convenience.  If data needs to be retrieved from a single table then this method can be used rather
	 * than having to create a whole new DAO.
	 * @param database the database the data is to be retrieved from.
	 * @param tablename the table the data is to be retrieved from.
	 * @param properties the properties used for the WHERE clause.
	 * @return the data as a list of SimpleData
	 * @throws SQLException
	 */
	protected List<SimpleData> getSimpleData(String database, String tablename, Properties properties) throws SQLException {
		log.debug("Starting getSimpleData");

		List<SimpleData> data = null;

		try {

			//put the properties into a linked hashmap which is ordered.
			Map<String,Object> orderedProps = new LinkedHashMap<String,Object>();
			for (Object field: properties.keySet()) {
				orderedProps.put((String) field, properties.getProperty((String) field));
			}

			StringBuffer sql = new StringBuffer("SELECT * FROM " + tablename + " WHERE true = true ");

			for (String key: orderedProps.keySet()) {
				sql.append("AND " + key + "=? ");

			}

			log.debug("SQL = " + sql);
			PreparedStatement ps = getConnection().prepareStatement(sql.toString());

			//set the properties into the query
			int i = 1;
			for (String key: orderedProps.keySet()) {
				ps.setObject(i, properties.get(key));
				i++;
			}

			ResultSet rs = ps.executeQuery();

			data = getDataFromResultSet(rs);

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close();
		}

		return data;
	}

	/**
	 * Method to turn a result set into a list of JobData objects
	 * @param rs the result set being translated.
	 * @return the list of JobData objects.
	 * @throws JobDataException
	 */
	private static List<SimpleData> getDataFromResultSet(ResultSet rs) {

		List<SimpleData> dataList = new ArrayList<SimpleData>();

		try {
			//get the result sets meta data
			ResultSetMetaData metaData = rs.getMetaData();

			//go through the rows adding the data
			SimpleData data = null;	
			while (rs.next()) {

				data = new SimpleData();

				//go through the columns adding the data using the column names as keys
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					data.setProperty(metaData.getColumnName(i), rs.getString(metaData.getColumnName(i)));
				}

				dataList.add(data);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataList;
	}

	/**
	 * Method to get a connection given a set of properties that determine where the connection will be retrieved from
	 * This method can get a connection from JNDI or use JDBC based on the provided properties.
	 * @param properties the connection details.
	 * @return the database connection.
	 * @throws SQLException
	 */
	protected Connection getConnection() throws SQLException {
		if (con == null || con.isClosed()) {

			try {
				Class.forName("com.mysql.jdbc.Driver");
				
				//String url = "jdbc:mysql://localhost:3306/gdelight?user=root&password=Afr1cansky";
			    //con = DriverManager.getConnection(url);
				
			    con = DriverManager.getConnection(System.getProperty("JDBC_CONNECTION_STRING"));
			} catch (Exception e) {
				e.printStackTrace();
				log.warn("Initial Context could not get datasource named '" + System.getProperty("JDBC_CONNECTION_STRING") + "' through lookup");
			}
		}
		
		return con;
	}

}

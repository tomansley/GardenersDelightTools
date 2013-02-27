package com.gdelight.tools.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class SimpleDAO extends BaseDAO {

	public Connection getConnection(String database) throws SQLException {
		return super.getConnection(database);
	}
}

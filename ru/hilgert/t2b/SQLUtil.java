package ru.hilgert.t2b;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUtil {

	private Connection conn;
	private String error;
	public boolean isConnected = false;

	public boolean connect(String host, String user, String pass, int port, String dbname) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+dbname,
					user, pass);
			isConnected = true;
			return true;
		} catch (Exception err) {
			error = err.toString();
			return false;
		}
	}

	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println(e.toString());
		}
	}

	public String getErorr() {
		return error;
	}

	public boolean exec(String query) {
		
		try {
			if(conn.isClosed()) return false;
			return conn.createStatement().execute(query);
		} catch (SQLException e) {
			System.out.println(e.toString());
			return false;
		}
	}

	public ResultSet get(String query) {
		try {
			return conn.createStatement().executeQuery(query);
		} catch (SQLException e) {
			System.out.println(e.toString());
			return null;
		}
	}

}

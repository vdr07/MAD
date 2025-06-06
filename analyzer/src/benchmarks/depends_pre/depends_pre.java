package benchmarks.depends_pre;

import ar.DependsOn;

import java.sql.*;
import java.util.Properties;

public class depends_pre {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public depends_pre(int id) {
		this.id = id;
		p = new Properties();
		p.setProperty("id", String.valueOf(this.id));
		Object o;
		try {
			o = Class.forName("MyDriver").newInstance();
			DriverManager.registerDriver((Driver) o);
			Driver driver = DriverManager.getDriver("jdbc:mydriver://");
			connect = driver.connect("", p);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void transaction(int key1, int key2, int val1, int val2) throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
		stmt1.setInt(1, val1);
		stmt1.setInt(2, key1);
		stmt1.executeUpdate();

		PreparedStatement stmt2 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
		stmt2.setInt(1, val2);
		stmt2.setInt(2, key2);
		stmt2.executeUpdate();
	}

	public void read_key(int key1) throws SQLException {
		PreparedStatement stmt = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt.setInt(1, key1);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int read_val = rs.getInt("VALUE");
	}
}

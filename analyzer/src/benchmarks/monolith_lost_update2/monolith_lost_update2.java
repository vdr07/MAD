package benchmarks.monolith_lost_update2;

import ar.ChoppedTransaction;

import java.sql.*;
import java.util.Properties;

public class monolith_lost_update2 {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public monolith_lost_update2(int id) {
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

	public void increment_5(int key) throws SQLException {
		PreparedStatement stmt = connect.prepareStatement("SELECT value " + "FROM " + "X" + " WHERE id = ?");
		stmt.setInt(1, key);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int read_val = rs.getInt("VALUE");
		int incremented_val = read_val + 5;

		PreparedStatement stmt2 = connect.prepareStatement("UPDATE X SET value = ?" + " WHERE id = ?");
		stmt2.setInt(1, incremented_val);
		stmt2.setInt(2, key);
		stmt2.executeUpdate();
	}
}

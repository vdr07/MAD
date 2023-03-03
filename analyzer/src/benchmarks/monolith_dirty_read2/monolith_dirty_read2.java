package benchmarks.monolith_dirty_read2;

import ar.ChoppedTransaction;

import java.sql.*;
import java.util.Properties;

public class monolith_dirty_read2 {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public monolith_dirty_read2(int id) {
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

	@ChoppedTransaction(microservice="m1")
	public void transaction(int key1) throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("UPDATE X SET value = ?" + " WHERE id = ?");
		stmt1.setInt(1, 50);
		stmt1.setInt(2, key1);
		stmt1.executeUpdate();

		PreparedStatement stmt2 = connect.prepareStatement("UPDATE Y SET value = ?" + " WHERE id = ?");
		stmt2.setInt(1, 50);
		stmt2.setInt(2, key1);
		stmt2.executeUpdate();

		PreparedStatement stmt3 = connect.prepareStatement("UPDATE X SET value = ?" + " WHERE id = ?");
		stmt3.setInt(1, 100);
		stmt3.setInt(2, key1);
		stmt3.executeUpdate();
	}

	@ChoppedTransaction(microservice="m2")
	public void read_key(int key1) throws SQLException {
		PreparedStatement stmt = connect.prepareStatement("SELECT value " + "FROM " + "X" + " WHERE id = ?");
		stmt.setInt(1, key1);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int read_val = rs.getInt("VALUE");
	}
}

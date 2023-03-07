package benchmarks.monolith_write_skew;

import ar.ChoppedTransaction;

import java.sql.*;
import java.util.Properties;

public class monolith_write_skew {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public monolith_write_skew(int id) {
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
	public void update_var(int key1, int key2, int amount2) throws SQLException {
		// read account 1
		PreparedStatement stmt = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt.setInt(1, key1);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int read_val = rs.getInt("VALUE");

		// update account 2
		if (read_val + amount2 < 1000 ) {
			PreparedStatement stmt1 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
			stmt1.setInt(1, amount2);
			stmt1.setInt(2, key2);
			stmt1.executeUpdate();
		}
	}
}

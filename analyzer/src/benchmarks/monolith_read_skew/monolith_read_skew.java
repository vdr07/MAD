package benchmarks.monolith_read_skew;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class monolith_read_skew {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public monolith_read_skew(int id) {
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

	public void update_vars(int key1, int key2, int amount1, int amount2) throws SQLException {
		if (amount1 + amount2 < 1000 ) {
			PreparedStatement stmt1 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
			stmt1.setInt(1, amount1);
			stmt1.setInt(2, key1);
			stmt1.executeUpdate();

			PreparedStatement stmt2 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
			stmt2.setInt(1, amount2);
			stmt2.setInt(2, key2);
			stmt2.executeUpdate();
		}
	}

	public void read_vars(int key1, int key2) throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt1.setInt(1, key1);
		ResultSet rs1 = stmt1.executeQuery();
		rs1.next();
		int read_val1 = rs1.getInt("VALUE");

		PreparedStatement stmt2 = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt2.setInt(1, key2);
		ResultSet rs2 = stmt2.executeQuery();
		rs2.next();
		int read_val2 = rs2.getInt("VALUE");

		System.out.println(read_val1);
		System.out.println(read_val2);
	}
}

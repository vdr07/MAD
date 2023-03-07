package benchmarks.microservices_read_skew;

import ar.ChoppedTransaction;

import java.sql.*;
import java.util.Properties;

public class microservices_read_skew {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public microservices_read_skew(int id) {
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

	@ChoppedTransaction(originalTransaction="update_vars", microservice="m1")
	public void update_var1(int key1, int amount1, int amount2) throws SQLException {
		if (amount1 + amount2 < 1000 ) {
			PreparedStatement stmt1 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
			stmt1.setInt(1, amount1);
			stmt1.setInt(2, key1);
			stmt1.executeUpdate();
		}
	}

	@ChoppedTransaction(originalTransaction="update_vars", microservice="m2")
	public void update_var2(int key2, int amount1, int amount2) throws SQLException {
		if (amount1 + amount2 < 1000 ) {
			PreparedStatement stmt1 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
			stmt1.setInt(1, amount2);
			stmt1.setInt(2, key2);
			stmt1.executeUpdate();
		}
	}

	@ChoppedTransaction(microservice="m3")
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

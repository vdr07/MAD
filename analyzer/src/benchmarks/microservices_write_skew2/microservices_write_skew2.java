package benchmarks.microservices_write_skew2;

import ar.ChoppedTransaction;

import java.sql.*;
import java.util.Properties;

public class microservices_write_skew2 {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public microservices_write_skew2(int id) {
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

	@ChoppedTransaction(originalTransaction="update_var2", microservice="m1")
	public void get_var1_val(int key1) throws SQLException {
		// read account 1
		PreparedStatement stmt = connect.prepareStatement("SELECT value " + "FROM " + "X" + " WHERE id = ?");
		stmt.setInt(1, key1);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int read_val = rs.getInt("VALUE");
	}

	@ChoppedTransaction(originalTransaction="update_var2", microservice="m2")
	public void update_var2_val(int read_val, int key2, int amount2) throws SQLException {
		// update account 2
		if (read_val + amount2 < 1000 ) {
			PreparedStatement stmt1 = connect.prepareStatement("UPDATE Y SET value = ?" + " WHERE id = ?");
			stmt1.setInt(1, amount2);
			stmt1.setInt(2, key2);
			stmt1.executeUpdate();
		}
	}

	@ChoppedTransaction(originalTransaction="update_var1", microservice="m2")
	public void get_var2_val(int key2) throws SQLException {
		// read account 2
		PreparedStatement stmt = connect.prepareStatement("SELECT value " + "FROM " + "Y" + " WHERE id = ?");
		stmt.setInt(1, key2);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int read_val = rs.getInt("VALUE");
	}

	@ChoppedTransaction(originalTransaction="update_var1", microservice="m1")
	public void update_var1_val(int read_val, int key1, int amount1) throws SQLException {
		// update account 1
		if (read_val + amount1 < 1000 ) {
			PreparedStatement stmt1 = connect.prepareStatement("UPDATE X SET value = ?" + " WHERE id = ?");
			stmt1.setInt(1, amount1);
			stmt1.setInt(2, key1);
			stmt1.executeUpdate();
		}
	}
}

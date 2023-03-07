package benchmarks.microservices_lost_update;

import ar.ChoppedTransaction;

import java.sql.*;
import java.util.Properties;

public class microservices_lost_update {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public microservices_lost_update(int id) {
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

	@ChoppedTransaction(originalTransaction="increment_5", microservice="m1")
	public void get_var_increment_5(int key) throws SQLException {
		PreparedStatement stmt = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt.setInt(1, key);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int read_val = rs.getInt("VALUE");
		int incremented_val = read_val + 5;
	}

	@ChoppedTransaction(originalTransaction="increment_5", microservice="m2")
	public void do_increment_5(int key, int incremented_val) throws SQLException {
		PreparedStatement stmt2 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
		stmt2.setInt(1, incremented_val);
		stmt2.setInt(2, key);
		stmt2.executeUpdate();
	}
}

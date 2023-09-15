package benchmarks.microservices_dirty_write;

import ar.ChoppedTransaction;

import java.sql.*;
import java.util.Properties;

public class microservices_dirty_write {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public microservices_dirty_write(int id) {
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
	public void update_var1(int key1, int amount1) throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
		stmt1.setInt(1, amount1);
		stmt1.setInt(2, key1);
		stmt1.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="update_vars", microservice="m2")
	public void update_var2(int key2, int amount2) throws SQLException {
		PreparedStatement stmt2 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
		stmt2.setInt(1, amount2);
		stmt2.setInt(2, key2);
		stmt2.executeUpdate();
	}
}

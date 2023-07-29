package benchmarks.conditional_path_test;

import ar.ChoppedTransaction;

import java.sql.*;
import java.util.Properties;

public class conditional_path_test {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public conditional_path_test(int id) {
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
	public void conditional_path(int accountId, int personId, int newValue) throws SQLException {
		PreparedStatement stmt = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt.setInt(1, accountId);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int read_val = rs.getInt("VALUE");
		if(read_val > 0) {
			PreparedStatement stmt1 = connect.prepareStatement("UPDATE PERSON SET value = ?" + " WHERE id = ?");
			stmt1.setInt(1, read_val);
			stmt1.setInt(2, personId);
			stmt1.executeUpdate();
		} else {
			PreparedStatement stmt1 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
			stmt1.setInt(1, newValue);
			stmt1.setInt(2, accountId);
			stmt1.executeUpdate();
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void update_person_account(int personId, int accountId, int personValue, int accountValue) throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("UPDATE PERSON SET value = ?" + " WHERE id = ?");
		stmt1.setInt(1, personValue);
		stmt1.setInt(2, personId);
		stmt1.executeUpdate();

		PreparedStatement stmt2 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
		stmt2.setInt(1, accountValue);
		stmt2.setInt(2, accountId);
		stmt2.executeUpdate();
	}
}

package benchmarks.conditional_path_test_chopped;

import ar.ChoppedTransaction;

import java.sql.*;
import java.util.Properties;

public class conditional_path_test_chopped {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public conditional_path_test_chopped(int id) {
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

	@ChoppedTransaction(originalTransaction="conditional_path", microservice="m1")
	public void conditional_path1(int accountId, int read_val) throws SQLException {
		PreparedStatement stmt = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt.setInt(1, accountId);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		read_val = rs.getInt("value");
	}

	@ChoppedTransaction(originalTransaction="conditional_path", microservice="m2")
	public void conditional_path2(int read_val, int personId) throws SQLException {
		if(read_val > 0) {
			PreparedStatement stmt1 = connect.prepareStatement("UPDATE PERSON SET value = ?" + " WHERE id = ?");
			stmt1.setInt(1, read_val);
			stmt1.setInt(2, personId);
			stmt1.executeUpdate();
		}
	}

	@ChoppedTransaction(originalTransaction="conditional_path", microservice="m1")
	public void conditional_path3(int read_val, int accountId, int newValue) throws SQLException {		
		if(read_val <= 0) {
			PreparedStatement stmt1 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
			stmt1.setInt(1, newValue);
			stmt1.setInt(2, accountId);
			stmt1.executeUpdate();
		}
	}

	@ChoppedTransaction(originalTransaction="update_person_account", microservice="m2")
	public void update_person_account1(int personId, int personValue) throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("UPDATE PERSON SET value = ?" + " WHERE id = ?");
		stmt1.setInt(1, personValue);
		stmt1.setInt(2, personId);
		stmt1.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="update_person_account", microservice="m1")
	public void update_person_account2(int accountId, int accountValue) throws SQLException {
		PreparedStatement stmt2 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
		stmt2.setInt(1, accountValue);
		stmt2.setInt(2, accountId);
		stmt2.executeUpdate();
	}
}

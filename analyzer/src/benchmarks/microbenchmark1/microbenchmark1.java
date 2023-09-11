package benchmarks.microbenchmark1;

import java.sql.*;
import java.util.Properties;

public class microbenchmark1 {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public microbenchmark1(int id) {
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

	public void update_vars(int memberId, int newStatus, int itemId, int newPrice) throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("UPDATE MEMBER SET status = ?" + " WHERE id = ?");
		stmt1.setInt(1, newStatus);
		stmt1.setInt(2, memberId);
		stmt1.executeUpdate();

		PreparedStatement stmt2 = connect.prepareStatement("UPDATE ITEM SET price = ?" + " WHERE id = ?");
		stmt2.setInt(1, newPrice);
		stmt2.setInt(2, itemId);
		stmt2.executeUpdate();
	}
}

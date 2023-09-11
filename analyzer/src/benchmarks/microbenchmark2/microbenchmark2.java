package benchmarks.microbenchmark2;

import java.sql.*;
import java.util.Properties;

public class microbenchmark2 {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public microbenchmark2(int id) {
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

	public void UItem1() throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("SELECT status " + "FROM " + "MEMBER" + " WHERE id = 1");
		ResultSet rs = stmt1.executeQuery();
		rs.next();
		int read_status = rs.getInt("status");

		PreparedStatement stmt2 = connect.prepareStatement("UPDATE ITEM SET price = ?" + " WHERE id = 1");
		stmt2.setInt(1, read_status*10);
		stmt2.executeUpdate();
	}

	public void UMember2() throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("SELECT price " + "FROM " + "ITEM" + " WHERE id = 2");
		ResultSet rs = stmt1.executeQuery();
		rs.next();
		int read_price = rs.getInt("price");

		PreparedStatement stmt2 = connect.prepareStatement("UPDATE MEMBER SET status = ?" + " WHERE id = 2");
		stmt2.setInt(1, read_price*2);
		stmt2.executeUpdate();
	}
}

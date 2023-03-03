package benchmarks.microservices_order_product_dirty_read;

import ar.ChoppedTransaction;

import java.sql.*;
import java.util.Properties;

public class microservices_order_product_dirty_read {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public microservices_order_product_dirty_read(int id) {
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

	@ChoppedTransaction(originalTransaction="place_order", microservice="m1")
	public void place_order_1(int order_id) throws SQLException {
		PreparedStatement stmt = connect.prepareStatement("INSERT INTO ORDERS VALUES (?,?)");
		stmt.setInt(1, order_id);
		stmt.setString(2, "pending");
		stmt.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="place_order", microservice="m2")
	public void check_product(int product_id) throws SQLException {
		PreparedStatement stmt2 = connect.prepareStatement("SELECT quantity " + "FROM " + "PRODUCTS" + " WHERE id = ?");
		stmt2.setInt(1, product_id);
		ResultSet rs = stmt2.executeQuery();
		rs.next();
		int product_quantity = rs.getInt("QUANTITY");
	}

	@ChoppedTransaction(originalTransaction="place_order", microservice="m1")
	public void place_order_2(int product_quantity, int order_id) throws SQLException {
		if(product_quantity > 0) {
			PreparedStatement stmt3 = connect.prepareStatement("UPDATE ORDERS SET state = ?" + " WHERE id = ?");
			stmt3.setString(1, "successful");
			stmt3.setInt(2, order_id);
			stmt3.executeUpdate();
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void check_order(int order_id) throws SQLException {
		PreparedStatement stmt = connect.prepareStatement("SELECT state " + "FROM " + "ORDERS" + " WHERE id = ?");
		stmt.setInt(1, order_id);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int order_state = rs.getInt("STATE");
	}
}

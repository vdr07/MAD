package benchmarks.jpabook_items;

import ar.ChoppedTransaction;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import java.sql.Timestamp;
import java.util.Random;
import java.util.ArrayList;

public class jpabook_items {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public jpabook_items(int id) {
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
		
		r = new Random();
	}

	// ItemController
	@ChoppedTransaction(microservice="m1")
	public void itemCreate(long itemId, String itemName, int price,
			int stockQuantity) throws SQLException {
		String insertItemSQL = 
				"INSERT INTO " + "ITEMS" +
				" (id, name, price, stockQuantity) " +
				" VALUES ( ?, ?, ?, ? )";

		PreparedStatement insertItem = connect.prepareStatement(insertItemSQL);
		insertItem.setLong(1, itemId);
		insertItem.setString(2, itemName);
		insertItem.setInt(3, price);
		insertItem.setInt(4, stockQuantity);
		insertItem.executeUpdate();
	}

	@ChoppedTransaction(microservice="m1")
	public void updateItemForm(long itemId) throws SQLException {
		String getItemSQL = 
				"SELECT * FROM " + "ITEMS"+
				" WHERE id = ?";

		PreparedStatement getItem = connect.prepareStatement(getItemSQL);
		getItem.setLong(1, itemId);
		ResultSet rs = getItem.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void updateItem(long itemId, String itemName, int price,
			int stockQuantity) throws SQLException {
		String getItemSQL = 
				"SELECT id FROM " + "ITEMS"+
				" WHERE id = ?";

		String updateItemSQL = 
				"UPDATE " + "ITEMS" +
				"   SET name = ?," +
				"       price = ?," +
				"       stockQuantity = ?" +
				" WHERE id = ?";

		String insertItemSQL = 
				"INSERT INTO " + "ITEMS" +
				" (id, name, price, stockQuantity) " +
				" VALUES ( ?, ?, ?, ? )";

		PreparedStatement getItem = connect.prepareStatement(getItemSQL);
		getItem.setLong(1, itemId);
		ResultSet rs = getItem.executeQuery();
		if (rs.next()) {
			PreparedStatement updateItem = connect.prepareStatement(updateItemSQL);
			updateItem.setString(1, itemName);
			updateItem.setInt(2, price);
			updateItem.setInt(3, stockQuantity);
			updateItem.setLong(4, itemId);
			updateItem.executeUpdate();
		} else {
			PreparedStatement insertItem = connect.prepareStatement(insertItemSQL);
			insertItem.setLong(1, itemId);
			insertItem.setString(2, itemName);
			insertItem.setInt(3, price);
			insertItem.setInt(4, stockQuantity);
			insertItem.executeUpdate();
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void itemList() throws SQLException {
		String findAllItemsSQL = 
				"SELECT * FROM " + "ITEMS"+
				" WHERE 1 = 1";

		PreparedStatement findAllItems = connect.prepareStatement(findAllItemsSQL);
		ResultSet rs = findAllItems.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
	}

	// OrderController
	@ChoppedTransaction(microservice="m1")
	public void orderCreateForm() throws SQLException {
		String findAllMembersSQL = 
				"SELECT * FROM " + "MEMBER"+
				" WHERE 1 = 1";

		String findAllItemsSQL = 
				"SELECT * FROM " + "ITEMS"+
				" WHERE 1 = 1";

		PreparedStatement findAllMembers = connect.prepareStatement(findAllMembersSQL);
		ResultSet rs = findAllMembers.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}

		PreparedStatement findAllItems = connect.prepareStatement(findAllItemsSQL);
		rs = findAllItems.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void order(long memberId, long itemId, int count, long orderId, long deliveryId,
			long orderItemId, String orderDate) throws SQLException {
		String findOneMemberSQL = 
				"SELECT city, street, zipcode FROM " + "MEMBER"+
				" WHERE id = ?";

		String findOneItemSQL = 
				"SELECT price, stockQuantity FROM " + "ITEMS"+
				" WHERE id = ?";

		String insertDeliverySQL = 
				"INSERT INTO " + "DELIVERY" +
				" (id, orderId, city, street, zipcode, status) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";

		String insertOrderItemSQL = 
				"INSERT INTO " + "ORDER_ITEM" +
				" (id, itemId, orderId, deliveryId, price, count) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";

		String updateItemSQL = 
				"UPDATE " + "ITEMS" +
				"   SET stockQuantity = ?" +
				" WHERE id = ?";

		String insertOrderSQL = 
				"INSERT INTO " + "ORDERS" +
				" (id, memberId, deliveryId, orderDate, status) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		PreparedStatement findOneMember = connect.prepareStatement(findOneMemberSQL);
		findOneMember.setLong(1, memberId);
		ResultSet rs = findOneMember.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
		String memberCity = rs.getString("city");
		String memberStreet = rs.getString("street");
		String memberZipcode = rs.getString("zipcode");

		PreparedStatement findOneItem = connect.prepareStatement(findOneItemSQL);
		findOneItem.setLong(1, itemId);
		rs = findOneItem.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
		int itemPrice = rs.getInt("price");
		int itemStockQuantity = rs.getInt("stockQuantity");

		PreparedStatement insertDelivery = connect.prepareStatement(insertDeliverySQL);
		insertDelivery.setLong(1, deliveryId);
		insertDelivery.setLong(2, orderId);
		insertDelivery.setString(3, memberCity);
		insertDelivery.setString(4, memberStreet);
		insertDelivery.setString(5, memberZipcode);
		insertDelivery.setString(6, "Ready");
		insertDelivery.executeUpdate();

		PreparedStatement insertOrderItem = connect.prepareStatement(insertOrderItemSQL);
		insertOrderItem.setLong(1, orderItemId);
		insertOrderItem.setLong(2, itemId);
		insertOrderItem.setLong(3, orderId);
		insertOrderItem.setLong(4, deliveryId);
		insertOrderItem.setInt(5, itemPrice);
		insertOrderItem.setInt(6, count);
		insertOrderItem.executeUpdate();

		if (itemStockQuantity-count < 0) {
			System.out.println("need more stock");
			return;
		}

		PreparedStatement updateItem = connect.prepareStatement(updateItemSQL);
		updateItem.setInt(1, itemStockQuantity-count);
		updateItem.setLong(2, itemId);
		updateItem.executeUpdate();

		PreparedStatement insertOrder = connect.prepareStatement(insertOrderSQL);
		insertOrder.setLong(1, orderId);
		insertOrder.setLong(2, memberId);
		insertOrder.setLong(3, deliveryId);
		insertOrder.setString(4, orderDate);
		insertOrder.setString(5, "Order");
		insertOrder.executeUpdate();
	}

	@ChoppedTransaction(microservice="m1")
	public void processCancelBuy(long orderId) throws SQLException {
		String findOneOrderSQL = 
				"SELECT id, deliveryId FROM " + "ORDERS"+
				" WHERE id = ?";

		String findOneDeliverySQL = 
				"SELECT status FROM " + "DELIVERY"+
				" WHERE id = ?";

		String updateOrderSQL = 
				"UPDATE " + "ORDERS" +
				"   SET status = ?" +
				" WHERE id = ?";

		String findOrderItemByOrderIdSQL = 
				"SELECT itemId, count FROM " + "ORDER_ITEM"+
				" WHERE orderId = ?";

		String findItemSQL = 
				"SELECT stockQuantity FROM " + "ITEMS"+
				" WHERE id = ?";

		String updateItemSQL = 
				"UPDATE " + "ITEMS" +
				"   SET stockQuantity = ?" +
				" WHERE id = ?";

		PreparedStatement findOneOrder = connect.prepareStatement(findOneOrderSQL);
		findOneOrder.setLong(1, orderId);
		ResultSet rs = findOneOrder.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
		long memberId = rs.getLong("id");
		long deliveryId = rs.getLong("deliveryId");

		PreparedStatement findOneDelivery = connect.prepareStatement(findOneDeliverySQL);
		findOneDelivery.setLong(1, deliveryId);
		rs = findOneDelivery.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
		String deliveryStatus = rs.getString("status");

		if (deliveryStatus.equals("Comp")) {
			System.out.println("error");
			return;
		}

		PreparedStatement updateOrder = connect.prepareStatement(updateOrderSQL);
		updateOrder.setString(1, "Cancel");
		updateOrder.setLong(2, orderId);
		updateOrder.executeUpdate();

		PreparedStatement findOrderItemByOrderId = connect.prepareStatement(findOrderItemByOrderIdSQL);
		findOrderItemByOrderId.setLong(1, orderId);
		rs = findOrderItemByOrderId.executeQuery();
		while (rs.next()) {
			long orderItemItemid = rs.getLong("itemId");
			int orderItemCount = rs.getInt("count");
			
			PreparedStatement findItem = connect.prepareStatement(findItemSQL);
			findItem.setLong(1, orderItemItemid);
			ResultSet item = findItem.executeQuery();
			if (!item.next()) {
				System.out.println("empty");
			}
			int itemStockQuantity = item.getInt("stockQuantity");
			
			PreparedStatement updateItem = connect.prepareStatement(updateItemSQL);
			updateItem.setInt(1, itemStockQuantity+orderItemCount);
			updateItem.setLong(2, orderItemItemid);
			updateItem.executeUpdate();
		}
	}
}
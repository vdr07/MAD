package benchmarks.jpabook;

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

public class jpabook {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public jpabook(int id) {
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

	// MemberController
	public void memberCreate(int memberId, String memberName, String city,
			String street, String zipcode) throws SQLException {
		String getMemberByNameSQL = 
				"SELECT * FROM " + "MEMBER"+
				" WHERE name = ?";

		String insertMemberSQL = 
				"INSERT INTO " + "MEMBER" +
				" (id, name, rate, city, street, zipcode) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		PreparedStatement getMemberByName = connect.prepareStatement(getMemberByNameSQL);
		getMemberByName.setString(1, memberName);
		ResultSet rs = getMemberByName.executeQuery();
		if (rs.next()) {
			System.out.println("name already exists");
			return;
		}

		PreparedStatement insertMember = connect.prepareStatement(insertMemberSQL);
		insertMember.setInt(1, memberId);
		insertMember.setString(2, memberName);
		insertMember.setString(3, city);
		insertMember.setString(4, street);
		insertMember.setString(5, zipcode);
		insertMember.executeUpdate();
	}

	public void memberList() throws SQLException {
		String findAllMembersSQL = 
				"SELECT * FROM " + "MEMBER"+
				" WHERE 1 = 1";

		PreparedStatement findAllMembers = connect.prepareStatement(findAllMembersSQL);
		ResultSet rs = findAllMembers.executeQuery();
		rs.next();
	}

	// ItemController
	public void itemCreate(int itemId, String itemName, int price,
			int stockQuantity) throws SQLException {
		String insertItemSQL = 
				"INSERT INTO " + "ITEMS" +
				" (id, name, price, stockQuantity) " +
				" VALUES ( ?, ?, ?, ? )";

		PreparedStatement insertItem = connect.prepareStatement(insertItemSQL);
		insertItem.setInt(1, itemId);
		insertItem.setString(2, itemName);
		insertItem.setInt(3, price);
		insertItem.setInt(4, stockQuantity);
		insertItem.executeUpdate();
	}

	public void updateItemForm(int itemId) throws SQLException {
		String getItemSQL = 
				"SELECT * FROM " + "ITEMS"+
				" WHERE id = ?";

		PreparedStatement getItem = connect.prepareStatement(getItemSQL);
		getItem.setInt(1, itemId);
		ResultSet rs = getItem.executeQuery();
		rs.next();
	}

	public void updateItem(int itemId, String itemName, int price,
			int stockQuantity, int isNew) throws SQLException {

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

		if (isNew == 0) {
			PreparedStatement updateItem = connect.prepareStatement(updateItemSQL);
			updateItem.setString(1, itemName);
			updateItem.setInt(2, price);
			updateItem.setInt(3, stockQuantity);
			updateItem.setInt(4, itemId);
			updateItem.executeUpdate();
		} else {
			PreparedStatement insertItem = connect.prepareStatement(insertItemSQL);
			insertItem.setInt(1, itemId);
			insertItem.setString(2, itemName);
			insertItem.setInt(3, price);
			insertItem.setInt(4, stockQuantity);
			insertItem.executeUpdate();
		}
	}

	public void itemList() throws SQLException {
		String findAllItemsSQL = 
				"SELECT * FROM " + "ITEMS"+
				" WHERE 1 = 1";

		PreparedStatement findAllItems = connect.prepareStatement(findAllItemsSQL);
		ResultSet rs = findAllItems.executeQuery();
		rs.next();
	}

	// OrderController
	public void orderCreateForm() throws SQLException {
		String findAllMembersSQL = 
				"SELECT * FROM " + "MEMBER"+
				" WHERE 1 = 1";

		String findAllItemsSQL = 
				"SELECT * FROM " + "ITEMS"+
				" WHERE 1 = 1";

		PreparedStatement findAllMembers = connect.prepareStatement(findAllMembersSQL);
		ResultSet rs = findAllMembers.executeQuery();
		rs.next();

		PreparedStatement findAllItems = connect.prepareStatement(findAllItemsSQL);
		rs = findAllItems.executeQuery();
		rs.next();
	}

	public void order(int memberId, int itemId, int count, int orderId, int deliveryId,
			int orderItemId, String orderDate) throws SQLException {
		String findOneMemberSQL = 
				"SELECT * FROM " + "MEMBER"+
				" WHERE id = ?";

		String findOneItemSQL = 
				"SELECT * FROM " + "ITEMS"+
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
		findOneMember.setInt(1, memberId);
		ResultSet rs = findOneMember.executeQuery();
		rs.next();
		String memberCity = rs.getString("city");
		String memberStreet = rs.getString("street");
		String memberZipcode = rs.getString("zipcode");

		PreparedStatement findOneItem = connect.prepareStatement(findOneItemSQL);
		findOneItem.setInt(1, itemId);
		rs = findOneItem.executeQuery();
		rs.next();
		int itemPrice = rs.getInt("price");
		int itemStockQuantity = rs.getInt("stockQuantity");

		PreparedStatement insertDelivery = connect.prepareStatement(insertDeliverySQL);
		insertDelivery.setInt(1, deliveryId);
		insertDelivery.setInt(2, orderId);
		insertDelivery.setString(3, memberCity);
		insertDelivery.setString(4, memberStreet);
		insertDelivery.setString(5, memberZipcode);
		insertDelivery.setString(6, "Ready");
		insertDelivery.executeUpdate();

		PreparedStatement insertOrderItem = connect.prepareStatement(insertOrderItemSQL);
		insertOrderItem.setInt(1, orderItemId);
		insertOrderItem.setInt(2, itemId);
		insertOrderItem.setInt(3, orderId);
		insertOrderItem.setInt(4, deliveryId);
		insertOrderItem.setInt(5, itemPrice);
		insertOrderItem.setInt(6, count);
		insertOrderItem.executeUpdate();

		if (itemStockQuantity-count < 0) {
			System.out.println("need more stock");
			return;
		}

		PreparedStatement updateItem = connect.prepareStatement(updateItemSQL);
		updateItem.setInt(1, itemStockQuantity-count);
		updateItem.setInt(2, itemId);
		updateItem.executeUpdate();

		PreparedStatement insertOrder = connect.prepareStatement(insertOrderSQL);
		insertOrder.setInt(1, orderId);
		insertOrder.setInt(2, memberId);
		insertOrder.setInt(3, deliveryId);
		insertOrder.setString(4, orderDate);
		insertOrder.setString(5, "Order");
		insertOrder.executeUpdate();
	}

	public void orderList(String orderStatus, String memberName) throws SQLException {
		String findMemberByNameSQL = 
				"SELECT * FROM " + "MEMBER"+
				" WHERE name = ?";

		String findOrdersSQL = 
				"SELECT * FROM " + "ORDERS"+
				" WHERE status = ? AND memberId = ?";

		PreparedStatement findMemberByName = connect.prepareStatement(findMemberByNameSQL);
		findMemberByName.setString(1, memberName);
		ResultSet rs = findMemberByName.executeQuery();
		rs.next();
		int memberId = rs.getInt("id");

		PreparedStatement findOrders = connect.prepareStatement(findOrdersSQL);
		findOrders.setString(1, orderStatus);
		findOrders.setInt(2, memberId);
		rs = findOrders.executeQuery();
		rs.next();
	}

	public void processCancelBuy(int orderId) throws SQLException {
		String findOneOrderSQL = 
				"SELECT * FROM " + "ORDERS"+
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
		findOneOrder.setInt(1, orderId);
		ResultSet rs = findOneOrder.executeQuery();
		rs.next();
		int deliveryId = rs.getInt("deliveryId");

		PreparedStatement findOneDelivery = connect.prepareStatement(findOneDeliverySQL);
		findOneDelivery.setInt(1, deliveryId);
		rs = findOneDelivery.executeQuery();
		rs.next();
		String deliveryStatus = rs.getString("status");

		if (deliveryStatus.equals("Comp")) {
			System.out.println("error");
			return;
		}

		PreparedStatement updateOrder = connect.prepareStatement(updateOrderSQL);
		updateOrder.setString(1, "Cancel");
		updateOrder.setInt(2, orderId);
		updateOrder.executeUpdate();

		PreparedStatement findOrderItemByOrderId = connect.prepareStatement(findOrderItemByOrderIdSQL);
		findOrderItemByOrderId.setInt(1, orderId);
		rs = findOrderItemByOrderId.executeQuery();
		while (rs.next()) {
			int orderItemItemid = rs.getInt("itemId");
			int orderItemCount = rs.getInt("count");
			
			PreparedStatement findItem = connect.prepareStatement(findItemSQL);
			findItem.setInt(1, orderItemItemid);
			ResultSet item = findItem.executeQuery();
			item.next();
			int itemStockQuantity = item.getInt("stockQuantity");
			
			PreparedStatement updateItem = connect.prepareStatement(updateItemSQL);
			updateItem.setInt(1, itemStockQuantity+orderItemCount);
			updateItem.setInt(2, orderItemItemid);
			updateItem.executeUpdate();
		}
	}
}
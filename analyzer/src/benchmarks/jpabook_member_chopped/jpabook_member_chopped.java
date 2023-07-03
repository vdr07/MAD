package benchmarks.jpabook_member_chopped;

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

public class jpabook_member_chopped {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public jpabook_member_chopped(int id) {
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
	@ChoppedTransaction(microservice="m1")
	public void memberCreate(long memberId, String memberName, String city,
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
		insertMember.setLong(1, memberId);
		insertMember.setString(2, memberName);
		insertMember.setString(3, city);
		insertMember.setString(4, street);
		insertMember.setString(5, zipcode);
		insertMember.executeUpdate();
	}

	@ChoppedTransaction(microservice="m1")
	public void memberList() throws SQLException {
		String findAllMembersSQL = 
				"SELECT * FROM " + "MEMBER"+
				" WHERE 1 = 1";

		PreparedStatement findAllMembers = connect.prepareStatement(findAllMembersSQL);
		ResultSet rs = findAllMembers.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
		}
	}

	// OrderController
	@ChoppedTransaction(originalTransaction="orderCreateForm", microservice="m1")
	public void orderCreateForm1() throws SQLException {
		String findAllMembersSQL = 
				"SELECT * FROM " + "MEMBER"+
				" WHERE 1 = 1";

		PreparedStatement findAllMembers = connect.prepareStatement(findAllMembersSQL);
		ResultSet rs = findAllMembers.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
	}

	@ChoppedTransaction(originalTransaction="orderCreateForm", microservice="m2")
	public void orderCreateForm2() throws SQLException {
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

	@ChoppedTransaction(originalTransaction="order", microservice="m1")
	public void order1(long memberId) throws SQLException {
		String findOneMemberSQL = 
				"SELECT * FROM " + "MEMBER"+
				" WHERE id = ?";
	
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
	}

	@ChoppedTransaction(originalTransaction="order", microservice="m2")
	public void order2(long itemId) throws SQLException {
		String findOneItemSQL = 
				"SELECT * FROM " + "ITEMS"+
				" WHERE id = ?";
	
		PreparedStatement findOneItem = connect.prepareStatement(findOneItemSQL);
		findOneItem.setLong(1, itemId);
		ResultSet rs = findOneItem.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
		int itemPrice = rs.getInt("price");
		int itemStockQuantity = rs.getInt("stockQuantity");
	}

	@ChoppedTransaction(originalTransaction="order", microservice="m3")
	public void order3(long deliveryId, long orderId, String memberCity,
			String memberStreet, String memberZipcode) throws SQLException {
		String insertDeliverySQL = 
				"INSERT INTO " + "DELIVERY" +
				" (id, orderId, city, street, zipcode, status) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";
	
		PreparedStatement insertDelivery = connect.prepareStatement(insertDeliverySQL);
		insertDelivery.setLong(1, deliveryId);
		insertDelivery.setLong(2, orderId);
		insertDelivery.setString(3, memberCity);
		insertDelivery.setString(4, memberStreet);
		insertDelivery.setString(5, memberZipcode);
		insertDelivery.setString(6, "Ready");
		insertDelivery.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="order", microservice="m4")
	public void order4(long orderItemId, long itemId, long orderId, long deliveryId,
			int itemPrice, int count) throws SQLException {
		String insertOrderItemSQL = 
				"INSERT INTO " + "ORDER_ITEM" +
				" (id, itemId, orderId, deliveryId, price, count) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";
	
		PreparedStatement insertOrderItem = connect.prepareStatement(insertOrderItemSQL);
		insertOrderItem.setLong(1, orderItemId);
		insertOrderItem.setLong(2, itemId);
		insertOrderItem.setLong(3, orderId);
		insertOrderItem.setLong(4, deliveryId);
		insertOrderItem.setInt(5, itemPrice);
		insertOrderItem.setInt(6, count);
		insertOrderItem.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="order", microservice="m2")
	public void order5(int itemStockQuantity, int count, long itemId) throws SQLException {
		String updateItemSQL = 
				"UPDATE " + "ITEMS" +
				"   SET stockQuantity = ?" +
				" WHERE id = ?";
		
		PreparedStatement updateItem = connect.prepareStatement(updateItemSQL);
		updateItem.setInt(1, itemStockQuantity-count);
		updateItem.setLong(2, itemId);
		updateItem.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="order", microservice="m5")
	public void order6(long orderId, long memberId, long deliveryId,
			String orderDate) throws SQLException {
		String insertOrderSQL = 
				"INSERT INTO " + "ORDERS" +
				" (id, memberId, deliveryId, orderDate, status) " +
				" VALUES ( ?, ?, ?, ?, ? )";
		
		PreparedStatement insertOrder = connect.prepareStatement(insertOrderSQL);
		insertOrder.setLong(1, orderId);
		insertOrder.setLong(2, memberId);
		insertOrder.setLong(3, deliveryId);
		insertOrder.setString(4, orderDate);
		insertOrder.setString(5, "Order");
		insertOrder.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="orderList", microservice="m1")
	public void orderList1(String memberName) throws SQLException {
		String findMemberByNameSQL = 
				"SELECT * FROM " + "MEMBER"+
				" WHERE name = ?";
		
		PreparedStatement findMemberByName = connect.prepareStatement(findMemberByNameSQL);
		findMemberByName.setString(1, memberName);
		ResultSet rs = findMemberByName.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
		long memberId = rs.getLong("id");
	}

	@ChoppedTransaction(originalTransaction="orderList", microservice="m5")
	public void orderList2(String orderStatus, long memberId) throws SQLException {
		String findOrdersSQL = 
				"SELECT * FROM " + "ORDERS"+
				" WHERE status = ? AND memberId = ?";
		
		PreparedStatement findOrders = connect.prepareStatement(findOrdersSQL);
		findOrders.setString(1, orderStatus);
		findOrders.setLong(2, memberId);
		ResultSet rs = findOrders.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
	}
}
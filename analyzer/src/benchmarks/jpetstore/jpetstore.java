package benchmarks.jpetstore;

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

public class jpetstore {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public jpetstore(int id) {
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

	// Account
	public void newAccount(String userid, String  email, String firstname, String lastname,
			String status, String addr1, String addr2, String city, String state, String zip,
			String country, String phone, String langpref, String favcategory, int mylistopt,
			int banneropt, String password) throws SQLException {
		String insertAccountSQL = 
				"INSERT INTO " + "ACCOUNT" +
				" (userid, email, firstname, lastname, status, addr1, addr2, city, state, zip, country, phone) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

		String insertProfileSQL = 
				"INSERT INTO " + "PROFILE" +
				" (userid, langpref, favcategory, mylistopt, banneropt) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		String insertSignOnSQL = 
				"INSERT INTO " + "SIGNON" +
				" (username, password) " +
				" VALUES ( ?, ? )";
		
		String getAccountByUsernameSQL = 
				"SELECT email, firstname, lastname, status, addr1, addr2, city, state, zip, country, phone FROM " + "ACCOUNT"+
				" WHERE userid = ?";

		String getSignOnByUsernameSQL = 
				"SELECT username FROM " + "SIGNON"+
				" WHERE username = ?";

		String getProfileByUsernameSQL = 
				"SELECT langpref, favcategory, mylistopt, banneropt FROM " + "PROFILE"+
				" WHERE userid = ?";

		String getBannerByFavCategorySQL = 
				"SELECT bannername FROM " + "BANNERDATA"+
				" WHERE favcategory = ?";

		String getProductListByCategorySQL = 
				"SELECT * FROM " + "PRODUCT"+
				" WHERE category = ?";

		PreparedStatement insertAccount = connect.prepareStatement(insertAccountSQL);
		insertAccount.setString(1, userid);
		insertAccount.setString(2, email);
		insertAccount.setString(3, firstname);
		insertAccount.setString(4, lastname);
		insertAccount.setString(5, status);
		insertAccount.setString(6, addr1);
		insertAccount.setString(7, addr2);
		insertAccount.setString(8, city);
		insertAccount.setString(9, state);
		insertAccount.setString(10, zip);
		insertAccount.setString(11, country);
		insertAccount.setString(12, phone);
		insertAccount.executeUpdate();

		PreparedStatement insertProfile = connect.prepareStatement(insertProfileSQL);
		insertProfile.setString(1, userid);
		insertProfile.setString(2, langpref);
		insertProfile.setString(3, favcategory);
		insertProfile.setInt(4, mylistopt);
		insertProfile.setInt(5, banneropt);
		insertProfile.executeUpdate();

		PreparedStatement insertSignOn = connect.prepareStatement(insertSignOnSQL);
		insertSignOn.setString(1, userid);
		insertSignOn.setString(2, password);
		insertSignOn.executeUpdate();

		PreparedStatement getAccountByUsername = connect.prepareStatement(getAccountByUsernameSQL);
		getAccountByUsername.setString(1, userid);
		ResultSet rs = getAccountByUsername.executeQuery();
		rs.next();

		PreparedStatement getSignOnByUsername = connect.prepareStatement(getSignOnByUsernameSQL);
		getSignOnByUsername.setString(1, userid);
		ResultSet rs2 = getSignOnByUsername.executeQuery();
		rs2.next();

		PreparedStatement getProfileByUsername = connect.prepareStatement(getProfileByUsernameSQL);
		getProfileByUsername.setString(1, userid);
		ResultSet rs3 = getProfileByUsername.executeQuery();
		rs3.next();
		String readFavCategory = rs3.getString("favcategory");

		PreparedStatement getBannerByFavCategory = connect.prepareStatement(getBannerByFavCategorySQL);
		getBannerByFavCategory.setString(1, readFavCategory);
		ResultSet rs4 = getBannerByFavCategory.executeQuery();
		rs4.next();

		PreparedStatement getProductListByCategory = connect.prepareStatement(getProductListByCategorySQL);
		getProductListByCategory.setString(1, readFavCategory);
		ResultSet rs5 = getProductListByCategory.executeQuery();
		rs5.next();
	}


	public void editAccount(String userid, String  email, String firstname, String lastname,
			String status, String addr1, String addr2, String city, String state, String zip,
			String country, String phone, String langpref, String favcategory, int mylistopt,
			int banneropt, String password) throws SQLException {

		String updateAccountSQL = 
				"UPDATE " + "ACCOUNT" +
				" SET email=?, firstname=?, lastname=?, status=?, addr1=?, addr2=?, city=?, state=?, zip=?, country=?, phone=?" +
				" WHERE userid = ?";

		String updateProfileSQL = 
				"UPDATE " + "PROFILE" +
				" SET langpref=?, favcategory=?, mylistopt=?, banneropt=?" +
				" WHERE  userid = ?";

		String updateSignOnSQL = 
				"UPDATE " + "SIGNON" +
				" SET password=?" +
				" WHERE username = ?";
		
		String getAccountByUsernameSQL = 
				"SELECT email, firstname, lastname, status, addr1, addr2, city, state, zip, country, phone FROM " + "ACCOUNT"+
				" WHERE userid = ?";

		String getSignOnByUsernameSQL = 
				"SELECT username FROM " + "SIGNON"+
				" WHERE username = ?";

		String getProfileByUsernameSQL = 
				"SELECT langpref, favcategory, mylistopt, banneropt FROM " + "PROFILE"+
				" WHERE userid = ?";

		String getBannerByFavCategorySQL = 
				"SELECT bannername FROM " + "BANNERDATA"+
				" WHERE favcategory = ?";

		String getProductListByCategorySQL = 
				"SELECT * FROM " + "PRODUCT"+
				" WHERE category = ?";

		PreparedStatement updateAccount = connect.prepareStatement(updateAccountSQL);
		updateAccount.setString(1, email);
		updateAccount.setString(2, firstname);
		updateAccount.setString(3, lastname);
		updateAccount.setString(4, status);
		updateAccount.setString(5, addr1);
		updateAccount.setString(6, addr2);
		updateAccount.setString(7, city);
		updateAccount.setString(8, state);
		updateAccount.setString(9, zip);
		updateAccount.setString(10, country);
		updateAccount.setString(11, phone);
		updateAccount.setString(12, userid);
		updateAccount.executeUpdate();

		PreparedStatement updateProfile = connect.prepareStatement(updateProfileSQL);
		updateProfile.setString(1, langpref);
		updateProfile.setString(2, favcategory);
		updateProfile.setInt(3, mylistopt);
		updateProfile.setInt(4, banneropt);
		updateProfile.setString(5, userid);
		updateProfile.executeUpdate();

		if (password.length() > 0) {
			PreparedStatement updateSignOn = connect.prepareStatement(updateSignOnSQL);
			updateSignOn.setString(1, password);
			updateSignOn.setString(2, userid);
			updateSignOn.executeUpdate();
		}

		PreparedStatement getAccountByUsername = connect.prepareStatement(getAccountByUsernameSQL);
		getAccountByUsername.setString(1, userid);
		ResultSet rs = getAccountByUsername.executeQuery();
		rs.next();

		PreparedStatement getSignOnByUsername = connect.prepareStatement(getSignOnByUsernameSQL);
		getSignOnByUsername.setString(1, userid);
		ResultSet rs2 = getSignOnByUsername.executeQuery();
		rs2.next();

		PreparedStatement getProfileByUsername = connect.prepareStatement(getProfileByUsernameSQL);
		getProfileByUsername.setString(1, userid);
		ResultSet rs3 = getProfileByUsername.executeQuery();
		rs3.next();
		String readFavCategory = rs3.getString("favcategory");

		PreparedStatement getBannerByFavCategory = connect.prepareStatement(getBannerByFavCategorySQL);
		getBannerByFavCategory.setString(1, readFavCategory);
		ResultSet rs4 = getBannerByFavCategory.executeQuery();
		rs4.next();

		PreparedStatement getProductListByCategory = connect.prepareStatement(getProductListByCategorySQL);
		getProductListByCategory.setString(1, readFavCategory);
		ResultSet rs5 = getProductListByCategory.executeQuery();
		rs5.next();
	}

	public void signon(String userid, String password) throws SQLException {
		String getAccountByUsernameSQL = 
				"SELECT email, firstname, lastname, status, addr1, addr2, city, state, zip, country, phone FROM " + "ACCOUNT"+
				" WHERE userid = ?";

		String getSignOnByUsernameAndPasswordSQL = 
				"SELECT username FROM " + "SIGNON"+
				" WHERE username = ? and password = ?";

		String getProfileByUsernameSQL = 
				"SELECT langpref, favcategory, mylistopt, banneropt FROM " + "PROFILE"+
				" WHERE userid = ?";

		String getBannerByFavCategorySQL = 
				"SELECT bannername FROM " + "BANNERDATA"+
				" WHERE favcategory = ?";

		String getProductListByCategorySQL = 
				"SELECT * FROM " + "PRODUCT"+
				" WHERE category = ?";

		PreparedStatement getAccountByUsername = connect.prepareStatement(getAccountByUsernameSQL);
		getAccountByUsername.setString(1, userid);
		ResultSet rs = getAccountByUsername.executeQuery();

		if (rs.next()) {
			PreparedStatement getSignOnByUsernameAndPassword = connect.prepareStatement(getSignOnByUsernameAndPasswordSQL);
			getSignOnByUsernameAndPassword.setString(1, userid);
			getSignOnByUsernameAndPassword.setString(2, password);
			ResultSet rs2 = getSignOnByUsernameAndPassword.executeQuery();
			rs2.next();

			PreparedStatement getProfileByUsername = connect.prepareStatement(getProfileByUsernameSQL);
			getProfileByUsername.setString(1, userid);
			ResultSet rs3 = getProfileByUsername.executeQuery();
			rs3.next();
			String readFavCategory = rs3.getString("favcategory");

			PreparedStatement getBannerByFavCategory = connect.prepareStatement(getBannerByFavCategorySQL);
			getBannerByFavCategory.setString(1, readFavCategory);
			ResultSet rs4 = getBannerByFavCategory.executeQuery();
			rs4.next();
		
			PreparedStatement getProductListByCategory = connect.prepareStatement(getProductListByCategorySQL);
			getProductListByCategory.setString(1, readFavCategory);
			ResultSet rs5 = getProductListByCategory.executeQuery();
			rs5.next();
		}
	}

	// Cart
	public void addItemToCart(String itemid) throws SQLException {
		String isItemInStockSQL = 
				"SELECT qty FROM " + "INVENTORY"+
				" WHERE itemid = ?";

		String getItemByIdSQL = 
				"SELECT * FROM " + "ITEM"+
				" WHERE itemid = ?";

		String getProductByIdSQL = 
				"SELECT * FROM " + "PRODUCT"+
				" WHERE productid = ?";

		PreparedStatement isItemInStock = connect.prepareStatement(isItemInStockSQL);
		isItemInStock.setString(1, itemid);
		ResultSet rs = isItemInStock.executeQuery();
		rs.next();
		int readItemQty = rs.getInt("qty");

		PreparedStatement getItemById = connect.prepareStatement(getItemByIdSQL);
		getItemById.setString(1, itemid);
		ResultSet rs2 = getItemById.executeQuery();
		rs2.next();
		String readProductId = rs2.getString("productid");

		PreparedStatement getProductById = connect.prepareStatement(getProductByIdSQL);
		getProductById.setString(1, readProductId);
		ResultSet rs3 = getProductById.executeQuery();
		rs3.next();

		PreparedStatement isItemInStock2 = connect.prepareStatement(isItemInStockSQL);
		isItemInStock2.setString(1, itemid);
		ResultSet rs4 = isItemInStock2.executeQuery();
		rs4.next();
	}

	// Catalog
	public void viewCategory(String categoryId) throws SQLException {
		String getProductListByCategorySQL = 
				"SELECT * FROM " + "PRODUCT"+
				" WHERE category = ?";
		
		String getCategoryByIdSQL = 
				"SELECT * FROM " + "CATEGORY"+
				" WHERE catid = ?";

		PreparedStatement getProductListByCategory = connect.prepareStatement(getProductListByCategorySQL);
		getProductListByCategory.setString(1, categoryId);
		ResultSet rs = getProductListByCategory.executeQuery();
		rs.next();

		PreparedStatement getCategoryById = connect.prepareStatement(getCategoryByIdSQL);
		getCategoryById.setString(1, categoryId);
		ResultSet rs2 = getCategoryById.executeQuery();
		rs2.next();
	}

	public void viewProduct(String productId) throws SQLException {
		String getProductByIdSQL = 
				"SELECT * FROM " + "PRODUCT"+
				" WHERE productid = ?";

		String getItemListByProductIdSQL = 
				"SELECT * FROM " + "ITEM"+
				" WHERE productid = ?";

		PreparedStatement getProductById = connect.prepareStatement(getProductByIdSQL);
		getProductById.setString(1, productId);
		ResultSet rs = getProductById.executeQuery();
		rs.next();

		PreparedStatement getItemListByProductId = connect.prepareStatement(getItemListByProductIdSQL);
		getItemListByProductId.setString(1, productId);
		ResultSet rs2 = getItemListByProductId.executeQuery();
		rs2.next();

		PreparedStatement getProductById2 = connect.prepareStatement(getProductByIdSQL);
		getProductById2.setString(1, productId);
		ResultSet rs3 = getProductById2.executeQuery();
		rs3.next();
	}

	public void viewItem(String itemid) throws SQLException {
		String getItemByIdSQL = 
				"SELECT * FROM " + "ITEM"+
				" WHERE itemid = ?";

		String getProductByIdSQL = 
				"SELECT * FROM " + "PRODUCT"+
				" WHERE productid = ?";

		String isItemInStockSQL = 
				"SELECT qty FROM " + "INVENTORY"+
				" WHERE itemid = ?";

		PreparedStatement getItemById = connect.prepareStatement(getItemByIdSQL);
		getItemById.setString(1, itemid);
		ResultSet rs = getItemById.executeQuery();
		rs.next();
		String readProductId = rs.getString("productid");

		PreparedStatement getProductById = connect.prepareStatement(getProductByIdSQL);
		getProductById.setString(1, readProductId);
		ResultSet rs2 = getProductById.executeQuery();
		rs2.next();

		PreparedStatement isItemInStock = connect.prepareStatement(isItemInStockSQL);
		isItemInStock.setString(1, itemid);
		ResultSet rs3 = isItemInStock.executeQuery();
		rs3.next();

		PreparedStatement getProductById2 = connect.prepareStatement(getProductByIdSQL);
		getProductById2.setString(1, readProductId);
		ResultSet rs4 = getProductById2.executeQuery();
		rs4.next();
	}

	public void searchProducts(String keyword1, String keyword2, int both) throws SQLException {
		String getProductByNameSQL = 
				"SELECT * FROM " + "PRODUCT"+
				" WHERE name = ?";

		if (both == 1) {
			PreparedStatement getProductByName = connect.prepareStatement(getProductByNameSQL);
			getProductByName.setString(1, keyword1);
			ResultSet rs = getProductByName.executeQuery();
			rs.next();

			PreparedStatement getProductByName2 = connect.prepareStatement(getProductByNameSQL);
			getProductByName2.setString(1, keyword2);
			ResultSet rs2 = getProductByName2.executeQuery();
			rs2.next();
		} else {
			PreparedStatement getProductByName = connect.prepareStatement(getProductByNameSQL);
			getProductByName.setString(1, keyword1);
			ResultSet rs = getProductByName.executeQuery();
			rs.next();	
		}
	}

	// Order
	public void listOrders(String username) throws SQLException {
		String getOrdersByUserIdSQL = 
				"SELECT * FROM " + "ORDERS"+
				" WHERE userid = ?";

		String getOrderStatusByOrderIdSQL = 
				"SELECT status FROM " + "ORDERSTATUS"+
				" WHERE orderid = ?";

		PreparedStatement getOrdersByUserId = connect.prepareStatement(getOrdersByUserIdSQL);
		getOrdersByUserId.setString(1, username);
		ResultSet rs = getOrdersByUserId.executeQuery();
		while (rs.next()) {
			int readOrderid = rs.getInt("orderid");
			PreparedStatement getOrderStatusByOrderId = connect.prepareStatement(getOrderStatusByOrderIdSQL);
			getOrderStatusByOrderId.setInt(1, readOrderid);
			ResultSet rs2 = getOrderStatusByOrderId.executeQuery();
			rs2.next();
		}
	}

	public void newOrder(String itemid1, String itemid2, int increment1, int increment2, int both,
			String userid, String orderdate, String shipaddr1, String shipaddr2, String shipcity,
			String shipstate, String shipzip, String shipcountry, String billaddr1, String billaddr2,
			String billcity, String billstate, String billzip, String billcountry, String courier,
			int totalprice, String billtofirstname, String billtolastname, String shiptofirstname,
			String shiptolastname, String creditcard, String exprdate, String cardtype,
			String locale, int linenum, String timestamp, String status, int itemLinenum1,
			int itemLinenum2, int unitPrice1, int unitPrice2) throws SQLException {
		String getSequenceSQL = 
				"SELECT * FROM " + "SEQUENCE"+
				" WHERE name = ?";

		String updateSequenceSQL = 
				"UPDATE " + "SEQUENCE" +
				" SET nextid=?" +
				" WHERE name = ?";

		String isItemInStockSQL = 
				"SELECT qty FROM " + "INVENTORY"+
				" WHERE itemid = ?";

		String updateQuantitySQL = 
				"UPDATE " + "INVENTORY" +
				" SET qty=?" +
				" WHERE itemid = ?";

		String insertOrderSQL = 
				"INSERT INTO " + "ORDERS" +
				" (orderid, userid, orderdate, shipadr1, shipadr2, shipcity, shipstate, shipzip, shipcountry, billaddr1, billaddr2, billcity, billstate, billzip, billcountry, courier, totalprice, billtofirstname, billtolastname, shiptofirstname, shiptolastname, creditcard, exprdate, cardtype, locale) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

		String insertOrderStatusSQL = 
				"INSERT INTO " + "ORDERSTATUS" +
				" (orderid, linenum, timestamp, status) " +
				" VALUES ( ?, ?, ?, ? )";

		String insertLineItemSQL = 
				"INSERT INTO " + "LINEITEM" +
				" (orderid, linenum, itemid, quantity, unitprice) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		PreparedStatement getSequence = connect.prepareStatement(getSequenceSQL);
		getSequence.setString(1, "ordernum");
		ResultSet rs = getSequence.executeQuery();
		rs.next();
		int readNextId = rs.getInt("nextid");

		PreparedStatement updateSequence = connect.prepareStatement(updateSequenceSQL);
		updateSequence.setInt(1, readNextId+1);
		updateSequence.setString(2, "ordernum");
		updateSequence.executeUpdate();

		int newQuantity1;
		// newQuantity2 is either assigned the correct value for usage or will not be used
		int newQuantity2 = 0;
		if (both == 1) {
			PreparedStatement isItemInStock = connect.prepareStatement(isItemInStockSQL);
			isItemInStock.setString(1, itemid1);
			ResultSet rs2 = isItemInStock.executeQuery();
			rs2.next();
			int currentQuantity1 = rs2.getInt("qty");
			newQuantity1 = currentQuantity1 + increment1;

			PreparedStatement updateQuantity = connect.prepareStatement(updateQuantitySQL);
			updateQuantity.setInt(1, newQuantity1);
			updateQuantity.setString(2, itemid1);
			updateQuantity.executeUpdate();	

			PreparedStatement isItemInStock2 = connect.prepareStatement(isItemInStockSQL);
			isItemInStock2.setString(1, itemid2);
			ResultSet rs3 = isItemInStock2.executeQuery();
			rs3.next();
			int currentQuantity2 = rs3.getInt("qty");
			newQuantity2 = currentQuantity2 + increment2;

			PreparedStatement updateQuantity2 = connect.prepareStatement(updateQuantitySQL);
			updateQuantity2.setInt(1, newQuantity2);
			updateQuantity2.setString(2, itemid2);
			updateQuantity2.executeUpdate();	
		} else {
			PreparedStatement isItemInStock = connect.prepareStatement(isItemInStockSQL);
			isItemInStock.setString(1, itemid1);
			ResultSet rs2 = isItemInStock.executeQuery();
			rs2.next();
			int currentQuantity1 = rs2.getInt("qty");
			newQuantity1 = currentQuantity1 + increment1;

			PreparedStatement updateQuantity = connect.prepareStatement(updateQuantitySQL);
			updateQuantity.setInt(1, newQuantity1);
			updateQuantity.setString(2, itemid1);
			updateQuantity.executeUpdate();	
		}

		PreparedStatement insertOrder = connect.prepareStatement(insertOrderSQL);
		insertOrder.setInt(1, readNextId);
		insertOrder.setString(2, userid);
		insertOrder.setString(3, orderdate);
		insertOrder.setString(4, shipaddr1);
		insertOrder.setString(5, shipaddr2);
		insertOrder.setString(6, shipcity);
		insertOrder.setString(7, shipstate);
		insertOrder.setString(8, shipzip);
		insertOrder.setString(9, shipcountry);
		insertOrder.setString(10, billaddr1);
		insertOrder.setString(11, billaddr2);
		insertOrder.setString(12, billcity);
		insertOrder.setString(13, billstate);
		insertOrder.setString(14, billzip);
		insertOrder.setString(15, billcountry);
		insertOrder.setString(16, courier);
		insertOrder.setInt(17, totalprice);
		insertOrder.setString(18, billtofirstname);
		insertOrder.setString(19, billtolastname);
		insertOrder.setString(20, shiptofirstname);
		insertOrder.setString(21, shiptolastname);
		insertOrder.setString(22, creditcard);
		insertOrder.setString(23, exprdate);
		insertOrder.setString(24, cardtype);
		insertOrder.setString(25, locale);
		insertOrder.executeUpdate();

		PreparedStatement insertOrderStatus = connect.prepareStatement(insertOrderStatusSQL);
		insertOrderStatus.setInt(1, readNextId);
		insertOrderStatus.setInt(2, linenum);
		insertOrderStatus.setString(3, timestamp);
		insertOrderStatus.setString(4, status);
		insertOrderStatus.executeUpdate();

		if (both == 1) {
			PreparedStatement insertLineItem1 = connect.prepareStatement(insertLineItemSQL);
			insertLineItem1.setInt(1, readNextId);
			insertLineItem1.setInt(2, itemLinenum1);
			insertLineItem1.setString(3, itemid1);
			insertLineItem1.setInt(4, newQuantity1);
			insertLineItem1.setInt(5, unitPrice1);
			insertLineItem1.executeUpdate();

			PreparedStatement insertLineItem2 = connect.prepareStatement(insertLineItemSQL);
			insertLineItem2.setInt(1, readNextId);
			insertLineItem2.setInt(2, itemLinenum2);
			insertLineItem2.setString(3, itemid2);
			insertLineItem2.setInt(4, newQuantity2);
			insertLineItem2.setInt(5, unitPrice2);
			insertLineItem2.executeUpdate();
		} else {
			PreparedStatement insertLineItem1 = connect.prepareStatement(insertLineItemSQL);
			insertLineItem1.setInt(1, readNextId);
			insertLineItem1.setInt(2, itemLinenum1);
			insertLineItem1.setString(3, itemid1);
			insertLineItem1.setInt(4, newQuantity1);
			insertLineItem1.setInt(5, unitPrice1);
			insertLineItem1.executeUpdate();
		}
	}

	public void viewOrder(int orderid) throws SQLException {
		String getOrdersByOrderIdSQL = 
				"SELECT * FROM " + "ORDERS"+
				" WHERE orderid = ?";

		String getOrderStatusByOrderIdSQL = 
				"SELECT status FROM " + "ORDERSTATUS"+
				" WHERE orderid = ?";

		String getLineItemsByOrderIdSQL = 
				"SELECT * FROM " + "LINEITEM"+
				" WHERE orderid = ?";

		String getItemByIdSQL = 
				"SELECT * FROM " + "ITEM"+
				" WHERE itemid = ?";

		String getProductByIdSQL = 
				"SELECT * FROM " + "PRODUCT"+
				" WHERE productid = ?";

		String isItemInStockSQL = 
				"SELECT qty FROM " + "INVENTORY"+
				" WHERE itemid = ?";

		PreparedStatement getOrdersByOrderId = connect.prepareStatement(getOrdersByOrderIdSQL);
		getOrdersByOrderId.setInt(1, orderid);
		ResultSet rs = getOrdersByOrderId.executeQuery();
		rs.next();

		PreparedStatement getOrderStatusByOrderId = connect.prepareStatement(getOrderStatusByOrderIdSQL);
		getOrderStatusByOrderId.setInt(1, orderid);
		ResultSet rs2 = getOrderStatusByOrderId.executeQuery();
		rs2.next();

		PreparedStatement getLineItemsByOrderId = connect.prepareStatement(getLineItemsByOrderIdSQL);
		getLineItemsByOrderId.setInt(1, orderid);
		ResultSet rs3 = getLineItemsByOrderId.executeQuery();
		
		while (rs3.next()) {
			String readItemId = rs3.getString("itemid");

			PreparedStatement getItemById = connect.prepareStatement(getItemByIdSQL);
			getItemById.setString(1, readItemId);
			ResultSet rs4 = getItemById.executeQuery();
			rs4.next();
			String readProductId = rs4.getString("productid");

			PreparedStatement getProductById = connect.prepareStatement(getProductByIdSQL);
			getProductById.setString(1, readProductId);
			ResultSet rs5 = getProductById.executeQuery();
			rs5.next();

			PreparedStatement isItemInStock = connect.prepareStatement(isItemInStockSQL);
			isItemInStock.setString(1, readItemId);
			ResultSet rs6 = isItemInStock.executeQuery();
			rs6.next();
			
			PreparedStatement isItemInStock2 = connect.prepareStatement(isItemInStockSQL);
			isItemInStock2.setString(1, readItemId);
			ResultSet rs7 = isItemInStock2.executeQuery();
			rs7.next();
		}
	}
}
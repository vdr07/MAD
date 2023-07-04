package benchmarks.axon_trader_company;

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

public class axon_trader_company {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public axon_trader_company(int id) {
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

	@ChoppedTransaction(microservice="m1")
	public void buyForm(String loggedInUsername, String companyIdentifier) throws SQLException {
		String findByUsernameSQL = 
				"SELECT * FROM " + "USER_VIEW"+
				" WHERE username = ?";

		String findByUserIdentifierSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE userIdentifier = ?";

		String companyFindOneSQL = 
				"SELECT * FROM " + "COMPANY_VIEW"+
				" WHERE identifier = ?";

		PreparedStatement findByUsername = connect.prepareStatement(findByUsernameSQL);
		findByUsername.setString(1, loggedInUsername);
		ResultSet rs = findByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		String userIdentifier = rs.getString("identifier");

		PreparedStatement findByUserIdentifier = connect.prepareStatement(findByUserIdentifierSQL);
		findByUserIdentifier.setString(1, userIdentifier);
		ResultSet portfolioView = findByUserIdentifier.executeQuery();
		if (!portfolioView.next()) {
			System.out.println("Empty");
		}
		long amountOfMoney = portfolioView.getLong("amountOfMoney");
		long reservedAmountOfMoney = portfolioView.getLong("reservedAmountOfMoney");

		PreparedStatement companyFindOne = connect.prepareStatement(companyFindOneSQL);
		companyFindOne.setString(1, companyIdentifier);
		ResultSet companyView = companyFindOne.executeQuery();
		if (!companyView.next()) {
			System.out.println("Empty");
		}
		String companyName = companyView.getString("cname");
	}

	@ChoppedTransaction(microservice="m1")
	public void details(String companyIdentifier) throws SQLException {

		String companyFindOneSQL = 
				"SELECT * FROM " + "COMPANY_VIEW"+
				" WHERE identifier = ?";

		String obtainOrderBookForCompanySQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE companyIdentifier = ?";

		String findByOrderBookIdSQL = 
				"SELECT * FROM " + "TRADE_EXECUTED_VIEW"+
				" WHERE orderBookId = ?";

		String sellOrdersSQL = 
				"SELECT * FROM " + "ORDERENTRY_SELL"+
				" WHERE orderBookId = ?";
		
		String buyOrdersSQL = 
				"SELECT * FROM " + "ORDERENTRY_BUY"+
				" WHERE orderBookId = ?";
		
		String orderViewsSQL = 
				"SELECT * FROM " + "ORDER_VIEW"+
				" WHERE jpaId = ?";

		PreparedStatement companyFindOne = connect.prepareStatement(companyFindOneSQL);
		companyFindOne.setString(1, companyIdentifier);
		ResultSet companyView = companyFindOne.executeQuery();
		if (!companyView.next()) {
			System.out.println("Empty");
		}
		String companyId = companyView.getString("identifier");

		PreparedStatement obtainOrderBookForCompany = connect.prepareStatement(obtainOrderBookForCompanySQL);
		obtainOrderBookForCompany.setString(1, companyId);
		ResultSet bookEntry = obtainOrderBookForCompany.executeQuery();
		if (!bookEntry.next()) {
			System.out.println("Empty");
		}
		String bookEntryId = bookEntry.getString("identifier");

		PreparedStatement findByOrderBookId = connect.prepareStatement(findByOrderBookIdSQL);
		findByOrderBookId.setString(1, bookEntryId);
		ResultSet executedTrades = findByOrderBookId.executeQuery();
		if (!executedTrades.next()) {
			System.out.println("Empty");
		}

		PreparedStatement sellOrders = connect.prepareStatement(sellOrdersSQL);
		sellOrders.setString(1, bookEntryId);
		ResultSet sellOrdersRS = sellOrders.executeQuery();
		while (sellOrdersRS.next()) {
			long orderJpaId = sellOrdersRS.getLong("orderId");
			PreparedStatement orderViews = connect.prepareStatement(orderViewsSQL);
			orderViews.setLong(1, orderJpaId);
			ResultSet rs = orderViews.executeQuery();
			if (!rs.next()) {
				System.out.println("Empty");
			}
		}

		PreparedStatement buyOrders = connect.prepareStatement(buyOrdersSQL);
		buyOrders.setString(1, bookEntryId);
		ResultSet buyOrdersRS = buyOrders.executeQuery();
		while (buyOrdersRS.next()) {
			long orderJpaId = buyOrdersRS.getLong("orderId");
			PreparedStatement orderViews = connect.prepareStatement(orderViewsSQL);
			orderViews.setLong(1, orderJpaId);
			ResultSet rs = orderViews.executeQuery();
			if (!rs.next()) {
				System.out.println("Empty");
			}
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void sellForm(String loggedInUsername, String companyIdentifier) throws SQLException {
		String findByUsernameSQL = 
				"SELECT * FROM " + "USER_VIEW"+
				" WHERE username = ?";

		String findByUserIdentifierSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE userIdentifier = ?";

		String obtainOrderBookForCompanySQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE companyIdentifier = ?";

		String findItemInPossessionSQL = 
				"SELECT * FROM " + "PORTFOLIO_ITEM_POSSESSION"+
				" WHERE portfolioId = ? AND itemIdentifier = ?";

		String findItemReservedSQL = 
				"SELECT * FROM " + "PORTFOLIO_ITEM_RESERVED"+
				" WHERE portfolioId = ? AND itemIdentifier = ?";

		String obtainAmountOfItemsForSQL = 
				"SELECT * FROM " + "ITEM_ENTRY"+
				" WHERE identifier = ?";

		String companyFindOneSQL = 
				"SELECT * FROM " + "COMPANY_VIEW"+
				" WHERE identifier = ?";

		// addPortfolioItemInfoToModel
		PreparedStatement findByUsername = connect.prepareStatement(findByUsernameSQL);
		findByUsername.setString(1, loggedInUsername);
		ResultSet rs = findByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		String userIdentifier = rs.getString("identifier");

		PreparedStatement findByUserIdentifier = connect.prepareStatement(findByUserIdentifierSQL);
		findByUserIdentifier.setString(1, userIdentifier);
		ResultSet portfolioView = findByUserIdentifier.executeQuery();
		if (!portfolioView.next()) {
			System.out.println("Empty");
		}
		String portfolioId = portfolioView.getString("identifier");

		PreparedStatement obtainOrderBookForCompany = connect.prepareStatement(obtainOrderBookForCompanySQL);
		obtainOrderBookForCompany.setString(1, companyIdentifier);
		ResultSet bookEntry = obtainOrderBookForCompany.executeQuery();
		if (!bookEntry.next()) {
			System.out.println("Empty");
		}
		String bookEntryId = bookEntry.getString("identifier");

		PreparedStatement findItemInPossession = connect.prepareStatement(findItemInPossessionSQL);
		findItemInPossession.setString(1, portfolioId);
		findItemInPossession.setString(2, bookEntryId);
		ResultSet itemInPossession = findItemInPossession.executeQuery();
		if (!itemInPossession.next()) {
			System.out.println("Empty");
		}
		String itemInPossessionId = itemInPossession.getString("itemIdentifier");

		PreparedStatement obtainAmountOfItemsFor = connect.prepareStatement(obtainAmountOfItemsForSQL);
		obtainAmountOfItemsFor.setString(1, itemInPossessionId);
		ResultSet obtainAmountOfItems = obtainAmountOfItemsFor.executeQuery();
		if (!obtainAmountOfItems.next()) {
			System.out.println("Empty");
		}
		long itemInPossessionAmount = obtainAmountOfItems.getLong("amount");

		PreparedStatement findItemReserved = connect.prepareStatement(findItemReservedSQL);
		findItemReserved.setString(1, portfolioId);
		findItemReserved.setString(2, bookEntryId);
		ResultSet itemReserved = findItemReserved.executeQuery();
		if (!itemReserved.next()) {
			System.out.println("Empty");
		}
		String itemReservedId = itemReserved.getString("itemIdentifier");

		obtainAmountOfItemsFor = connect.prepareStatement(obtainAmountOfItemsForSQL);
		obtainAmountOfItemsFor.setString(1, itemReservedId);
		obtainAmountOfItems = obtainAmountOfItemsFor.executeQuery();
		if (!obtainAmountOfItems.next()) {
			System.out.println("Empty");
		}
		long itemReservedAmount = obtainAmountOfItems.getLong("amount");

		// prepareInitialOrder
		PreparedStatement companyFindOne = connect.prepareStatement(companyFindOneSQL);
		companyFindOne.setString(1, companyIdentifier);
		ResultSet companyView = companyFindOne.executeQuery();
		if (!companyView.next()) {
			System.out.println("Empty");
		}
		String companyName = companyView.getString("cname");
	}

	@ChoppedTransaction(microservice="m1")
	public void createCompany(String companyIdentifier, String cname, long cvalue, long amountOfShares) throws SQLException {
		String createCompanySQL = 
				"INSERT INTO " + "COMPANY_VIEW" +
				" (identifier, cname, cvalue, amountOfShares, tradeStarted) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		PreparedStatement createCompany = connect.prepareStatement(createCompanySQL);
		createCompany.setString(1, companyIdentifier);
		createCompany.setString(2, cname);
		createCompany.setLong(3, cvalue);
		createCompany.setLong(4, amountOfShares);
		createCompany.setLong(5, 1);
		createCompany.executeUpdate();
	}

	@ChoppedTransaction(microservice="m1")
	public void orderBookAddedToCompany(String companyIdentifier, String orderBookId) throws SQLException {
		String companyFindOneSQL = 
				"SELECT * FROM " + "COMPANY_VIEW"+
				" WHERE identifier = ?";
		
		String createOrderBookSQL = 
				"INSERT INTO " + "ORDER_BOOK_VIEW" +
				" (identifier, companyIdentifier, companyName) " +
				" VALUES ( ?, ?, ? )";

		PreparedStatement companyFindOne = connect.prepareStatement(companyFindOneSQL);
		companyFindOne.setString(1, companyIdentifier);
		ResultSet companyView = companyFindOne.executeQuery();
		if (!companyView.next()) {
			System.out.println("Empty");
		}
		String companyName = companyView.getString("cname");

		PreparedStatement createOrderBook = connect.prepareStatement(createOrderBookSQL);
		createOrderBook.setString(1, orderBookId);
		createOrderBook.setString(2, companyIdentifier);
		createOrderBook.setString(3, companyName);
		createOrderBook.executeUpdate();
	}
}
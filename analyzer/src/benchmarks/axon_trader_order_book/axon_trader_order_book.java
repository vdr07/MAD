package benchmarks.axon_trader_order_book;

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

public class axon_trader_order_book {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public axon_trader_order_book(int id) {
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

	// Company Controller
	@ChoppedTransaction(microservice="m1")
	public void buy(String companyIdentifier, String loggedInUsername, int bindingResult,
			long tradeCount, long itemPrice) throws SQLException {
		String obtainOrderBookForCompanySQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE companyIdentifier = ?";

		String findByUsernameSQL = 
				"SELECT * FROM " + "USER_VIEW"+
				" WHERE username = ?";

		String findByUserIdentifierSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE userIdentifier = ?";

		if (bindingResult == 1) {
			PreparedStatement obtainOrderBookForCompany = connect.prepareStatement(obtainOrderBookForCompanySQL);
			obtainOrderBookForCompany.setString(1, companyIdentifier);
			ResultSet bookEntry = obtainOrderBookForCompany.executeQuery();
			if (!bookEntry.next()) {
				System.out.println("Empty");
			}
			bookEntry.getString("identifier");

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

			long moneyToSpend = portfolioView.getLong("amountOfMoney") - portfolioView.getLong("reservedAmountOfMoney");			

			if (moneyToSpend < tradeCount * itemPrice) {
				System.out.println("Not enough cash to spend to buy the items for the price you want");
				return;
			}

			portfolioView.getString("identifier");

			return;
		}

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
	public void sell(String companyIdentifier, String loggedInUsername, int bindingResult,
			long tradeCount) throws SQLException {
		String obtainOrderBookForCompanySQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE companyIdentifier = ?";

		String findByUsernameSQL = 
				"SELECT * FROM " + "USER_VIEW"+
				" WHERE username = ?";

		String findByUserIdentifierSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE userIdentifier = ?";

		String findItemInPossessionSQL = 
				"SELECT * FROM " + "PORTFOLIO_ITEM_POSSESSION"+
				" WHERE portfolioId = ? AND itemIdentifier = ?";

		String findItemReservedSQL = 
				"SELECT * FROM " + "PORTFOLIO_ITEM_RESERVED"+
				" WHERE portfolioId = ? AND itemIdentifier = ?";

		String obtainAmountOfItemsForSQL = 
				"SELECT * FROM " + "ITEM_ENTRY"+
				" WHERE identifier = ?";

		if (bindingResult == 1) {
			PreparedStatement obtainOrderBookForCompany = connect.prepareStatement(obtainOrderBookForCompanySQL);
			obtainOrderBookForCompany.setString(1, companyIdentifier);
			ResultSet bookEntry = obtainOrderBookForCompany.executeQuery();
			if (!bookEntry.next()) {
				System.out.println("Empty");
			}
			String bookEntryId = bookEntry.getString("identifier");

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

			if ((itemInPossessionAmount - itemReservedAmount) < tradeCount) {
				System.out.println("Not enough items available to create sell order.");

				findItemInPossession = connect.prepareStatement(findItemInPossessionSQL);
				findItemInPossession.setString(1, portfolioId);
				findItemInPossession.setString(2, bookEntryId);
				itemInPossession = findItemInPossession.executeQuery();
				if (!itemInPossession.next()) {
					System.out.println("Empty");
				}
				itemInPossessionId = itemInPossession.getString("itemIdentifier");

				obtainAmountOfItemsFor = connect.prepareStatement(obtainAmountOfItemsForSQL);
				obtainAmountOfItemsFor.setString(1, itemInPossessionId);
				obtainAmountOfItems = obtainAmountOfItemsFor.executeQuery();
				if (!obtainAmountOfItems.next()) {
					System.out.println("Empty");
				}
				itemInPossessionAmount = obtainAmountOfItems.getLong("amount");

				findItemReserved = connect.prepareStatement(findItemReservedSQL);
				findItemReserved.setString(1, portfolioId);
				findItemReserved.setString(2, bookEntryId);
				itemReserved = findItemReserved.executeQuery();
				if (!itemReserved.next()) {
					System.out.println("Empty");
				}
				itemReservedId = itemReserved.getString("itemIdentifier");

				obtainAmountOfItemsFor = connect.prepareStatement(obtainAmountOfItemsForSQL);
				obtainAmountOfItemsFor.setString(1, itemReservedId);
				obtainAmountOfItems = obtainAmountOfItemsFor.executeQuery();
				if (!obtainAmountOfItems.next()) {
					System.out.println("Empty");
				}
				itemReservedAmount = obtainAmountOfItems.getLong("amount");
				return;
			}

			bookEntry.getString("identifier");
			portfolioView.getString("identifier");
		}

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

	// OrderBook Controller
	@ChoppedTransaction(microservice="m1")
	public void orderBookGetOrders(String identifier) throws SQLException {
		// findone
		String orderBookGetOrdersSQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE identifier = ?";

		PreparedStatement orderBookGetOrders = connect.prepareStatement(orderBookGetOrdersSQL);
		orderBookGetOrders.setString(1, identifier);
		ResultSet rs = orderBookGetOrders.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
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

	@ChoppedTransaction(microservice="m1")
	public void orderPlaced(String orderBookId, long jpaId, String identifier, long tradeCount, 
			long itemPrice, String userId, long itemsRemaining, String type) throws SQLException {
		String orderBookGetOrdersSQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE identifier = ?";

		String createOrderSQL = 
				"INSERT INTO " + "ORDER_VIEW" +
				" (jpaId, identifier, tradeCount, itemPrice, userId, itemsRemaining, otype) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ? )";

		String placeBuyOrderSQL = 
				"INSERT INTO " + "ORDERENTRY_BUY" +
				" (orderBookId, orderId) " +
				" VALUES ( ?, ? )";
		
		String placeSellOrderSQL = 
				"INSERT INTO " + "ORDERENTRY_SELL" +
				" (orderBookId, orderId) " +
				" VALUES ( ?, ? )";

		PreparedStatement orderBookGetOrders = connect.prepareStatement(orderBookGetOrdersSQL);
		orderBookGetOrders.setString(1, orderBookId);
		ResultSet rs = orderBookGetOrders.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		PreparedStatement createOrder = connect.prepareStatement(createOrderSQL);
		createOrder.setLong(1, jpaId);
		createOrder.setString(2, identifier);
		createOrder.setLong(3, tradeCount);
		createOrder.setLong(4, itemPrice);
		createOrder.setString(5, userId);
		createOrder.setLong(6, itemsRemaining);
		createOrder.setString(7, type);
		createOrder.executeUpdate();

		if (type.equals("Buy")) {
			PreparedStatement placeBuyOrder = connect.prepareStatement(placeBuyOrderSQL);
			placeBuyOrder.setString(1, orderBookId);
			placeBuyOrder.setLong(2, jpaId);
			placeBuyOrder.executeUpdate();
		} else if (type.equals("Sell")) {
			PreparedStatement placeSellOrder = connect.prepareStatement(placeSellOrderSQL);
			placeSellOrder.setString(1, orderBookId);
			placeSellOrder.setLong(2, jpaId);
			placeSellOrder.executeUpdate();
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void tradeExecuted(long buyOrderId, long sellOrderId, String orderBookId,
		long generatedId, long tradeCount, long itemPrice) throws SQLException {
		String orderBookGetOrdersSQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE identifier = ?";

		String tradeExecutedSQL = 
				"INSERT INTO " + "TRADE_EXECUTED_VIEW" +
				" (generatedId, tradeCount, tradePrice, companyName, orderBookId) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		String buyOrdersSQL = 
				"SELECT * FROM " + "ORDERENTRY_BUY"+
				" WHERE orderBookId = ?";

		String sellOrdersSQL = 
				"SELECT * FROM " + "ORDERENTRY_SELL"+
				" WHERE orderBookId = ?";

		String orderViewsSQL = 
				"SELECT * FROM " + "ORDER_VIEW"+
				" WHERE jpaId = ?";
		
		String orderViewsUpdateRemItemsSQL =
				"UPDATE " + "ORDER_VIEW" + 
				"   SET itemsRemaining = ?" +
				" WHERE jpaId = ? ";

		String deleteBuyOrderSQL = 
				"DELETE FROM " + "ORDERENTRY_BUY" +
				" WHERE orderBookId = ? " +
				"   AND orderId = ?";

		String deleteSellOrderSQL = 
				"DELETE FROM " + "ORDERENTRY_SELL" +
				" WHERE orderBookId = ? " +
				"   AND orderId = ?";

		PreparedStatement orderBookGetOrders = connect.prepareStatement(orderBookGetOrdersSQL);
		orderBookGetOrders.setString(1, orderBookId);
		ResultSet orderBookView = orderBookGetOrders.executeQuery();
		if (!orderBookView.next()) {
			System.out.println("Empty");
		}
		String companyName = orderBookView.getString("companyName");
		String orderBookIdentifier = orderBookView.getString("identifier");

		PreparedStatement tradeExecuted = connect.prepareStatement(tradeExecutedSQL);
		tradeExecuted.setLong(1, generatedId);
		tradeExecuted.setLong(2, tradeCount);
		tradeExecuted.setLong(3, itemPrice);
		tradeExecuted.setString(4, companyName);
		tradeExecuted.setString(5, orderBookIdentifier);
		tradeExecuted.executeUpdate();

		PreparedStatement buyOrders = connect.prepareStatement(buyOrdersSQL);
		buyOrders.setString(1, orderBookIdentifier);
		ResultSet buyOrdersRS = buyOrders.executeQuery();

		boolean foundBuyOrder = false;
		long buyOrderRemainingItems = 0;
		while (buyOrdersRS.next()) {
			long orderId = buyOrdersRS.getLong("orderId");
			if (orderId == buyOrderId) {
				PreparedStatement orderViews = connect.prepareStatement(orderViewsSQL);
				orderViews.setLong(1, orderId);
				ResultSet orderView = orderViews.executeQuery();
				if (!orderView.next()) {
					System.out.println("Empty");
				}
				long itemsRemaining = orderView.getLong("itemsRemaining");
				buyOrderRemainingItems = itemsRemaining - tradeCount;

				PreparedStatement orderViewsUpdateRemItems = connect.prepareStatement(orderViewsUpdateRemItemsSQL);
				orderViewsUpdateRemItems.setLong(1, buyOrderRemainingItems);
				orderViewsUpdateRemItems.setLong(2, orderId);
				orderViewsUpdateRemItems.executeUpdate();
                foundBuyOrder = true;
                break;
			}
		}

		if (foundBuyOrder && buyOrderRemainingItems == 0) {
			PreparedStatement deleteBuyOrder = connect.prepareStatement(deleteBuyOrderSQL);
			deleteBuyOrder.setString(1, orderBookIdentifier);
			deleteBuyOrder.setLong(2, buyOrderId);
			deleteBuyOrder.executeUpdate();
		}

		PreparedStatement sellOrders = connect.prepareStatement(sellOrdersSQL);
		sellOrders.setString(1, orderBookIdentifier);
		ResultSet sellOrdersRS = sellOrders.executeQuery();

		boolean foundSellOrder = false;
		long sellOrderRemainingItems = 0;
		while (sellOrdersRS.next()) {
			long orderId = sellOrdersRS.getLong("orderId");
			if (orderId == sellOrderId) {
				PreparedStatement orderViews = connect.prepareStatement(orderViewsSQL);
				orderViews.setLong(1, orderId);
				ResultSet orderView = orderViews.executeQuery();
				if (!orderView.next()) {
					System.out.println("Empty");
				}
				long itemsRemaining = orderView.getLong("itemsRemaining");
				sellOrderRemainingItems = itemsRemaining - tradeCount;

				PreparedStatement orderViewsUpdateRemItems = connect.prepareStatement(orderViewsUpdateRemItemsSQL);
				orderViewsUpdateRemItems.setLong(1, sellOrderRemainingItems);
				orderViewsUpdateRemItems.setLong(2, orderId);
				orderViewsUpdateRemItems.executeUpdate();
                foundSellOrder = true;
                break;
			}
		}

		if (foundSellOrder && sellOrderRemainingItems == 0) {
			PreparedStatement deleteSellOrder = connect.prepareStatement(deleteSellOrderSQL);
			deleteSellOrder.setString(1, orderBookIdentifier);
			deleteSellOrder.setLong(2, buyOrderId);
			deleteSellOrder.executeUpdate();
		}
	}

	// Admin Controller

	@ChoppedTransaction(microservice="m1")
	public void adminShowPortfolio(String identifier) throws SQLException {
		String adminShowPortfolioSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE identifier = ?";
		
		String orderBookViewFindAllSQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE 1 = 1";

		// findone
		PreparedStatement adminShowPortfolio = connect.prepareStatement(adminShowPortfolioSQL);
		adminShowPortfolio.setString(1, identifier);
		ResultSet rs = adminShowPortfolio.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		// findall orderbooks
		PreparedStatement orderBookViewFindAll = connect.prepareStatement(orderBookViewFindAllSQL);
		rs = orderBookViewFindAll.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void itemsAddedToPortfolio(String orderBookId, long itemId, String itemIdentifier, 
			long amountOfItemsAdded, String portfolioId) throws SQLException {
		String getOrderBookSQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE identifier = ?";

		String createItemEntrySQL = 
				"INSERT INTO " + "ITEM_ENTRY" +
				" (generatedId, identifier, companyIdentifier, companyName, amount) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		String portfolioFindOneSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE identifier = ?";

		String findItemInPossessionSQL = 
				"SELECT * FROM " + "PORTFOLIO_ITEM_POSSESSION"+
				" WHERE portfolioId = ? AND itemIdentifier = ?";

		String insertItemInPossessionSQL = 
				"INSERT INTO " + "PORTFOLIO_ITEM_POSSESSION" +
				" (portfolioId, itemIdentifier) " +
				" VALUES ( ?, ? )";
		
		String selectItemAmountSQL = 
				"SELECT amount FROM " + "ITEM_ENTRY"+
				" WHERE generatedId = ?";

		String updateItemAmountSQL = 
				"UPDATE " + "ITEM_ENTRY" + 
				"   SET amount = ? " +
				" WHERE generatedId = ?";

		PreparedStatement getOrderBook = connect.prepareStatement(getOrderBookSQL);
		getOrderBook.setString(1, orderBookId);
		ResultSet orderBookView = getOrderBook.executeQuery();
		if (!orderBookView.next()) {
			System.out.println("Empty");
		}
		String companyIdentifier = orderBookView.getString("companyIdentifier");
		String companyName = orderBookView.getString("companyName");

		PreparedStatement portfolioFindOne = connect.prepareStatement(portfolioFindOneSQL);
		portfolioFindOne.setString(1, portfolioId);
		ResultSet portfolioView = portfolioFindOne.executeQuery();
		if (!portfolioView.next()) {
			System.out.println("Empty");
		}

		PreparedStatement findItemInPossession = connect.prepareStatement(findItemInPossessionSQL);
		findItemInPossession.setString(1, portfolioId);
		findItemInPossession.setString(2, itemIdentifier);
		ResultSet itemInPossession = findItemInPossession.executeQuery();
		if (!itemInPossession.next()) {
			PreparedStatement createItemEntry = connect.prepareStatement(createItemEntrySQL);
			createItemEntry.setLong(1, itemId);
			createItemEntry.setString(2, itemIdentifier);
			createItemEntry.setString(3, companyIdentifier);
			createItemEntry.setString(4, companyName);
			createItemEntry.setLong(5, amountOfItemsAdded);
			createItemEntry.executeUpdate();
			
			PreparedStatement insertItemInPossession = connect.prepareStatement(insertItemInPossessionSQL);
			insertItemInPossession.setString(1, portfolioId);
			insertItemInPossession.setString(2, itemIdentifier);
			insertItemInPossession.executeUpdate();
		} else {
			PreparedStatement selectItemAmount = connect.prepareStatement(selectItemAmountSQL);
			selectItemAmount.setLong(1, itemId);
			ResultSet itemEntry = selectItemAmount.executeQuery();
			if (!itemEntry.next()) {
				System.out.println("Empty");
			}
			long currentAmount = itemEntry.getLong("amount");
			long newAmount = currentAmount + amountOfItemsAdded;
			
			PreparedStatement updateItemAmount = connect.prepareStatement(updateItemAmountSQL);
			updateItemAmount.setLong(1, newAmount);
			updateItemAmount.setLong(2, itemId);
			updateItemAmount.executeUpdate();
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void itemReservationCancelledForPortfolio(String orderBookId, long itemId, String itemIdentifier, 
			long amountOfItemsCancelled, String portfolioId) throws SQLException {
		String getOrderBookSQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE identifier = ?";

		String createItemEntrySQL = 
				"INSERT INTO " + "ITEM_ENTRY" +
				" (generatedId, identifier, companyIdentifier, companyName, amount) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		String portfolioFindOneSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE identifier = ?";

		String findItemReservedSQL = 
				"SELECT * FROM " + "PORTFOLIO_ITEM_RESERVED"+
				" WHERE portfolioId = ? AND itemIdentifier = ?";

		String removeItemReservedSQL = 
				"DELETE FROM " + "PORTFOLIO_ITEM_RESERVED" +
				" WHERE portfolioId = ? " +
				"   AND itemIdentifier = ?";

		String findItemInPossessionSQL = 
				"SELECT * FROM " + "PORTFOLIO_ITEM_POSSESSION"+
				" WHERE portfolioId = ? AND itemIdentifier = ?";

		String insertItemInPossessionSQL = 
				"INSERT INTO " + "PORTFOLIO_ITEM_POSSESSION" +
				" (portfolioId, itemIdentifier) " +
				" VALUES ( ?, ? )";
		
		String selectItemAmountSQL = 
				"SELECT amount FROM " + "ITEM_ENTRY"+
				" WHERE generatedId = ?";

		String updateItemAmountSQL = 
				"UPDATE " + "ITEM_ENTRY" + 
				"   SET amount = ? " +
				" WHERE generatedId = ?";

		PreparedStatement getOrderBook = connect.prepareStatement(getOrderBookSQL);
		getOrderBook.setString(1, orderBookId);
		ResultSet orderBookView = getOrderBook.executeQuery();
		if (!orderBookView.next()) {
			System.out.println("Empty");
		}
		String companyIdentifier = orderBookView.getString("companyIdentifier");
		String companyName = orderBookView.getString("companyName");

		PreparedStatement portfolioFindOne = connect.prepareStatement(portfolioFindOneSQL);
		portfolioFindOne.setString(1, portfolioId);
		ResultSet portfolioView = portfolioFindOne.executeQuery();
		if (!portfolioView.next()) {
			System.out.println("Empty");
		}

		PreparedStatement findItemReserved = connect.prepareStatement(findItemReservedSQL);
		findItemReserved.setString(1, portfolioId);
		findItemReserved.setString(2, itemIdentifier);
		ResultSet itemReserved = findItemReserved.executeQuery();
		if (itemReserved.next()) {
			PreparedStatement selectItemAmount = connect.prepareStatement(selectItemAmountSQL);
			selectItemAmount.setLong(1, itemId);
			ResultSet itemEntry = selectItemAmount.executeQuery();
			if (!itemEntry.next()) {
				System.out.println("Empty");
			}
			long currentAmount = itemEntry.getLong("amount");
			long newAmount = currentAmount - amountOfItemsCancelled;

			PreparedStatement updateItemAmount = connect.prepareStatement(updateItemAmountSQL);
			updateItemAmount.setLong(1, newAmount);
			updateItemAmount.setLong(2, itemId);
			updateItemAmount.executeUpdate();

			if (newAmount <= 0) {
				PreparedStatement removeItemReserved = connect.prepareStatement(removeItemReservedSQL);
				removeItemReserved.setString(1, portfolioId);
				removeItemReserved.setString(2, itemIdentifier);
				removeItemReserved.executeUpdate();
			}
		}

		PreparedStatement findItemInPossession = connect.prepareStatement(findItemInPossessionSQL);
		findItemInPossession.setString(1, portfolioId);
		findItemInPossession.setString(2, itemIdentifier);
		ResultSet itemInPossession = findItemInPossession.executeQuery();
		if (!itemInPossession.next()) {
			PreparedStatement createItemEntry = connect.prepareStatement(createItemEntrySQL);
			createItemEntry.setLong(1, itemId);
			createItemEntry.setString(2, itemIdentifier);
			createItemEntry.setString(3, companyIdentifier);
			createItemEntry.setString(4, companyName);
			createItemEntry.setLong(5, amountOfItemsCancelled);
			createItemEntry.executeUpdate();
			
			PreparedStatement insertItemInPossession = connect.prepareStatement(insertItemInPossessionSQL);
			insertItemInPossession.setString(1, portfolioId);
			insertItemInPossession.setString(2, itemIdentifier);
			insertItemInPossession.executeUpdate();
		} else {
			PreparedStatement selectItemAmount = connect.prepareStatement(selectItemAmountSQL);
			selectItemAmount.setLong(1, itemId);
			ResultSet itemEntry = selectItemAmount.executeQuery();
			if (!itemEntry.next()) {
				System.out.println("Empty");
			}
			long currentAmount = itemEntry.getLong("amount");
			long newAmount = currentAmount + amountOfItemsCancelled;
			
			PreparedStatement updateItemAmount = connect.prepareStatement(updateItemAmountSQL);
			updateItemAmount.setLong(1, newAmount);
			updateItemAmount.setLong(2, itemId);
			updateItemAmount.executeUpdate();
		}
	}


	@ChoppedTransaction(microservice="m1")
	public void itemReservationConfirmedForPortfolio(String orderBookId, long itemId, String itemIdentifier, 
			long amountOfConfirmedItems, String portfolioId) throws SQLException {
		String getOrderBookSQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE identifier = ?";

		String createItemEntrySQL = 
				"INSERT INTO " + "ITEM_ENTRY" +
				" (generatedId, identifier, companyIdentifier, companyName, amount) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		String portfolioFindOneSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE identifier = ?";

		String findItemReservedSQL = 
				"SELECT * FROM " + "PORTFOLIO_ITEM_RESERVED"+
				" WHERE portfolioId = ? AND itemIdentifier = ?";

		String removeItemReservedSQL = 
				"DELETE FROM " + "PORTFOLIO_ITEM_RESERVED" +
				" WHERE portfolioId = ? " +
				"   AND itemIdentifier = ?";

		String findItemInPossessionSQL = 
				"SELECT * FROM " + "PORTFOLIO_ITEM_POSSESSION"+
				" WHERE portfolioId = ? AND itemIdentifier = ?";

		String insertItemInPossessionSQL = 
				"INSERT INTO " + "PORTFOLIO_ITEM_POSSESSION" +
				" (portfolioId, itemIdentifier) " +
				" VALUES ( ?, ? )";

		String removeItemInPossesionSQL = 
				"DELETE FROM " + "PORTFOLIO_ITEM_POSSESSION" +
				" WHERE portfolioId = ? " +
				"   AND itemIdentifier = ?";
		
		String selectItemAmountSQL = 
				"SELECT amount FROM " + "ITEM_ENTRY"+
				" WHERE generatedId = ?";

		String updateItemAmountSQL = 
				"UPDATE " + "ITEM_ENTRY" + 
				"   SET amount = ? " +
				" WHERE generatedId = ?";

		PreparedStatement getOrderBook = connect.prepareStatement(getOrderBookSQL);
		getOrderBook.setString(1, orderBookId);
		ResultSet orderBookView = getOrderBook.executeQuery();
		if (!orderBookView.next()) {
			System.out.println("Empty");
		}
		String companyIdentifier = orderBookView.getString("companyIdentifier");
		String companyName = orderBookView.getString("companyName");

		PreparedStatement portfolioFindOne = connect.prepareStatement(portfolioFindOneSQL);
		portfolioFindOne.setString(1, portfolioId);
		ResultSet portfolioView = portfolioFindOne.executeQuery();
		if (!portfolioView.next()) {
			System.out.println("Empty");
		}

		PreparedStatement findItemReserved = connect.prepareStatement(findItemReservedSQL);
		findItemReserved.setString(1, portfolioId);
		findItemReserved.setString(2, itemIdentifier);
		ResultSet itemReserved = findItemReserved.executeQuery();
		if (itemReserved.next()) {
			PreparedStatement selectItemAmount = connect.prepareStatement(selectItemAmountSQL);
			selectItemAmount.setLong(1, itemId);
			ResultSet itemEntry = selectItemAmount.executeQuery();
			if (!itemEntry.next()) {
				System.out.println("Empty");
			}
			long currentAmount = itemEntry.getLong("amount");
			long newAmount = currentAmount - amountOfConfirmedItems;

			PreparedStatement updateItemAmount = connect.prepareStatement(updateItemAmountSQL);
			updateItemAmount.setLong(1, newAmount);
			updateItemAmount.setLong(2, itemId);
			updateItemAmount.executeUpdate();

			if (newAmount <= 0) {
				PreparedStatement removeItemReserved = connect.prepareStatement(removeItemReservedSQL);
				removeItemReserved.setString(1, portfolioId);
				removeItemReserved.setString(2, itemIdentifier);
				removeItemReserved.executeUpdate();
			}
		}

		PreparedStatement findItemInPossession = connect.prepareStatement(findItemInPossessionSQL);
		findItemInPossession.setString(1, portfolioId);
		findItemInPossession.setString(2, itemIdentifier);
		ResultSet itemInPossession = findItemInPossession.executeQuery();
		if (itemInPossession.next()) {
			PreparedStatement selectItemAmount = connect.prepareStatement(selectItemAmountSQL);
			selectItemAmount.setLong(1, itemId);
			ResultSet itemEntry = selectItemAmount.executeQuery();
			if (!itemEntry.next()) {
				System.out.println("Empty");
			}
			long currentAmount = itemEntry.getLong("amount");
			long newAmount = currentAmount - amountOfConfirmedItems;

			PreparedStatement updateItemAmount = connect.prepareStatement(updateItemAmountSQL);
			updateItemAmount.setLong(1, newAmount);
			updateItemAmount.setLong(2, itemId);
			updateItemAmount.executeUpdate();

			if (newAmount <= 0) {
				PreparedStatement removeItemInPossesion = connect.prepareStatement(removeItemInPossesionSQL);
				removeItemInPossesion.setString(1, portfolioId);
				removeItemInPossesion.setString(2, itemIdentifier);
				removeItemInPossesion.executeUpdate();
			}
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void itemsReserved(String orderBookId, long itemId, String itemIdentifier, 
			long amountOfItemsReserved, String portfolioId) throws SQLException {
		String getOrderBookSQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE identifier = ?";

		String createItemEntrySQL = 
				"INSERT INTO " + "ITEM_ENTRY" +
				" (generatedId, identifier, companyIdentifier, companyName, amount) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		String portfolioFindOneSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE identifier = ?";

		String findItemInReservedSQL = 
				"SELECT * FROM " + "PORTFOLIO_ITEM_RESERVED"+
				" WHERE portfolioId = ? AND itemIdentifier = ?";

		String insertItemInReservedSQL = 
				"INSERT INTO " + "PORTFOLIO_ITEM_RESERVED" +
				" (portfolioId, itemIdentifier) " +
				" VALUES ( ?, ? )";
		
		String selectItemAmountSQL = 
				"SELECT amount FROM " + "ITEM_ENTRY"+
				" WHERE generatedId = ?";

		String updateItemAmountSQL = 
				"UPDATE " + "ITEM_ENTRY" + 
				"   SET amount = ? " +
				" WHERE generatedId = ?";

		PreparedStatement getOrderBook = connect.prepareStatement(getOrderBookSQL);
		getOrderBook.setString(1, orderBookId);
		ResultSet orderBookView = getOrderBook.executeQuery();
		if (!orderBookView.next()) {
			System.out.println("Empty");
		}
		String companyIdentifier = orderBookView.getString("companyIdentifier");
		String companyName = orderBookView.getString("companyName");

		PreparedStatement portfolioFindOne = connect.prepareStatement(portfolioFindOneSQL);
		portfolioFindOne.setString(1, portfolioId);
		ResultSet portfolioView = portfolioFindOne.executeQuery();
		if (!portfolioView.next()) {
			System.out.println("Empty");
		}

		PreparedStatement findItemInReserved = connect.prepareStatement(findItemInReservedSQL);
		findItemInReserved.setString(1, portfolioId);
		findItemInReserved.setString(2, itemIdentifier);
		ResultSet itemReserved = findItemInReserved.executeQuery();
		if (!itemReserved.next()) {
			PreparedStatement createItemEntry = connect.prepareStatement(createItemEntrySQL);
			createItemEntry.setLong(1, itemId);
			createItemEntry.setString(2, itemIdentifier);
			createItemEntry.setString(3, companyIdentifier);
			createItemEntry.setString(4, companyName);
			createItemEntry.setLong(5, amountOfItemsReserved);
			createItemEntry.executeUpdate();
			
			PreparedStatement insertItemInReserved = connect.prepareStatement(insertItemInReservedSQL);
			insertItemInReserved.setString(1, portfolioId);
			insertItemInReserved.setString(2, itemIdentifier);
			insertItemInReserved.executeUpdate();
		} else {
			PreparedStatement selectItemAmount = connect.prepareStatement(selectItemAmountSQL);
			selectItemAmount.setLong(1, itemId);
			ResultSet itemEntry = selectItemAmount.executeQuery();
			if (!itemEntry.next()) {
				System.out.println("Empty");
			}
			long currentAmount = itemEntry.getLong("amount");
			long newAmount = currentAmount + amountOfItemsReserved;
			
			PreparedStatement updateItemAmount = connect.prepareStatement(updateItemAmountSQL);
			updateItemAmount.setLong(1, newAmount);
			updateItemAmount.setLong(2, itemId);
			updateItemAmount.executeUpdate();
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void transactionStarted(String orderBookId, String identifier, String portfolioId, 
			long amountOfItems, long amountOfExecutedItems, long pricePerItem, String type) throws SQLException {
		String getOrderBookSQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE identifier = ?";

		String insertTransactionEntrySQL = 
				"INSERT INTO " + "TRANSACTION_VIEW" +
				" (identifier, orderBookId, portfolioId, companyName, amountOfItems, amountOfExecutedItems, pricePerItem, transactionState, transactionType) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

		PreparedStatement getOrderBook = connect.prepareStatement(getOrderBookSQL);
		getOrderBook.setString(1, orderBookId);
		ResultSet orderBookView = getOrderBook.executeQuery();
		if (!orderBookView.next()) {
			System.out.println("Empty");
		}
		String companyName = orderBookView.getString("companyName");

		PreparedStatement insertTransactionEntry = connect.prepareStatement(insertTransactionEntrySQL);
		insertTransactionEntry.setString(1, identifier);
		insertTransactionEntry.setString(2, orderBookId);
		insertTransactionEntry.setString(3, portfolioId);
		insertTransactionEntry.setString(4, companyName);
		insertTransactionEntry.setLong(5, amountOfItems);
		insertTransactionEntry.setLong(6, amountOfExecutedItems);
		insertTransactionEntry.setLong(7, pricePerItem);
		insertTransactionEntry.setString(8, "Started");
		insertTransactionEntry.setString(9, type);
		insertTransactionEntry.executeUpdate();
	}
}
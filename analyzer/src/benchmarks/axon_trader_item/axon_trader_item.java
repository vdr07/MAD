package benchmarks.axon_trader_item;

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

public class axon_trader_item {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public axon_trader_item(int id) {
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
}
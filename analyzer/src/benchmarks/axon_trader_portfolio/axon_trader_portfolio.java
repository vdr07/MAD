package benchmarks.axon_trader_portfolio;

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

public class axon_trader_portfolio {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public axon_trader_portfolio(int id) {
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
	public void createPortfolio(String identifier, String userIdentifier) throws SQLException {
		String getUNameSQL = 
				"SELECT uname FROM " + "USER_VIEW"+
				" WHERE identifier = ?";

		String createPortfolioSQL = 
				"INSERT INTO " + "PORTFOLIO_VIEW" +
				" (identifier, userIdentifier, userName, amountOfMoney, reservedAmountOfMoney) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		PreparedStatement getUName = connect.prepareStatement(getUNameSQL);
		getUName.setString(1, userIdentifier);
		ResultSet rs = getUName.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		String userName = rs.getString("uname");

		PreparedStatement createPortfolio = connect.prepareStatement(createPortfolioSQL);
		createPortfolio.setString(1, identifier);
		createPortfolio.setString(2, userIdentifier);
		createPortfolio.setString(3, userName);
		createPortfolio.setLong(4, 0);
		createPortfolio.setLong(5, 0);
		createPortfolio.executeUpdate();
	}

	@ChoppedTransaction(microservice="m1")
	public void cashManaged(String portfolioId, long amount, String type) throws SQLException {
		String portfolioFindOneSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE identifier = ?";
		
		String portfolioUpdateAmountOfMoneySQL = 
				"UPDATE " + "PORTFOLIO_VIEW" + 
				"   SET amountOfMoney = ? " +
				" WHERE identifier = ?";

		PreparedStatement portfolioFindOne = connect.prepareStatement(portfolioFindOneSQL);
		portfolioFindOne.setString(1, portfolioId);
		ResultSet portfolioView = portfolioFindOne.executeQuery();
		if (!portfolioView.next()) {
			System.out.println("Empty");
		}
		long amountOfMoney = portfolioView.getLong("amountOfMoney");

		long newAmount;
		if (type.equals("deposit")) {
			newAmount = amountOfMoney + amount;
		} else {
			newAmount = amountOfMoney - amount;
		}

		PreparedStatement portfolioUpdateAmountOfMoney = connect.prepareStatement(portfolioUpdateAmountOfMoneySQL);
		portfolioUpdateAmountOfMoney.setLong(1, newAmount);
		portfolioUpdateAmountOfMoney.setString(2, portfolioId);
		portfolioUpdateAmountOfMoney.executeUpdate();
	}

	@ChoppedTransaction(microservice="m1")
	public void cashReservedManage(String portfolioId, long amount, String type) throws SQLException {
		String portfolioFindOneSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE identifier = ?";
		
		String portfolioUpdateReservedAmountOfMoneySQL = 
				"UPDATE " + "PORTFOLIO_VIEW" + 
				"   SET reservedAmountOfMoney = ? " +
				" WHERE identifier = ?";

		PreparedStatement portfolioFindOne = connect.prepareStatement(portfolioFindOneSQL);
		portfolioFindOne.setString(1, portfolioId);
		ResultSet portfolioView = portfolioFindOne.executeQuery();
		if (!portfolioView.next()) {
			System.out.println("Empty");
		}
		long reservedAmountOfMoney = portfolioView.getLong("reservedAmountOfMoney");
		
		long newReservedAmountOfMoney;
		if (type.equals("reserve")) {
			newReservedAmountOfMoney = reservedAmountOfMoney + amount;
		} else {
			newReservedAmountOfMoney = reservedAmountOfMoney - amount;
		}
		
		PreparedStatement portfolioUpdateReservedAmountOfMoney = connect.prepareStatement(portfolioUpdateReservedAmountOfMoneySQL);
		portfolioUpdateReservedAmountOfMoney.setLong(1, newReservedAmountOfMoney);
		portfolioUpdateReservedAmountOfMoney.setString(2, portfolioId);
		portfolioUpdateReservedAmountOfMoney.executeUpdate();
	}

	@ChoppedTransaction(microservice="m1")
	public void cashReservationConfirmed(String portfolioId, long amountOfMoneyConfirmed) throws SQLException {
		String portfolioFindOneSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE identifier = ?";
		
		String portfolioUpdateReservedAmountOfMoneySQL = 
				"UPDATE " + "PORTFOLIO_VIEW" + 
				"   SET reservedAmountOfMoney = ? " +
				" WHERE identifier = ?";

		String portfolioUpdateAmountOfMoneySQL = 
				"UPDATE " + "PORTFOLIO_VIEW" + 
				"   SET amountOfMoney = ? " +
				" WHERE identifier = ?";

		PreparedStatement portfolioFindOne = connect.prepareStatement(portfolioFindOneSQL);
		portfolioFindOne.setString(1, portfolioId);
		ResultSet portfolioView = portfolioFindOne.executeQuery();
		if (!portfolioView.next()) {
			System.out.println("Empty");
		}
		long reservedAmountOfMoney = portfolioView.getLong("reservedAmountOfMoney");
		long amountOfMoney = portfolioView.getLong("amountOfMoney");

		if (amountOfMoneyConfirmed < reservedAmountOfMoney) {
			PreparedStatement portfolioUpdateReservedAmountOfMoney = connect.prepareStatement(portfolioUpdateReservedAmountOfMoneySQL);
			portfolioUpdateReservedAmountOfMoney.setLong(1, reservedAmountOfMoney - amountOfMoneyConfirmed);
			portfolioUpdateReservedAmountOfMoney.setString(2, portfolioId);
			portfolioUpdateReservedAmountOfMoney.executeUpdate();
		} else {
			PreparedStatement portfolioUpdateReservedAmountOfMoney = connect.prepareStatement(portfolioUpdateReservedAmountOfMoneySQL);
			portfolioUpdateReservedAmountOfMoney.setLong(1, 0);
			portfolioUpdateReservedAmountOfMoney.setString(2, portfolioId);
			portfolioUpdateReservedAmountOfMoney.executeUpdate();
		}

		PreparedStatement portfolioUpdateAmountOfMoney = connect.prepareStatement(portfolioUpdateAmountOfMoneySQL);
		portfolioUpdateAmountOfMoney.setLong(1, amountOfMoney - amountOfMoneyConfirmed);
		portfolioUpdateAmountOfMoney.setString(2, portfolioId);
		portfolioUpdateAmountOfMoney.executeUpdate();
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

	// Dashboard Controller
	@ChoppedTransaction(microservice="m1")
	public void dashboardShow(String userIdentifier) throws SQLException {
		String portfolioFindByUserIdSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE userIdentifier = ?";
		
		String transactionFindByPortfolioIdSQL = 
				"SELECT * FROM " + "TRANSACTION_VIEW"+
				" WHERE portfolioId = ?";

		// portfolioFindByUserId
		PreparedStatement portfolioFindByUserId = connect.prepareStatement(portfolioFindByUserIdSQL);
		portfolioFindByUserId.setString(1, userIdentifier);
		ResultSet rs = portfolioFindByUserId.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		String portfolioId = rs.getString("identifier");

		// transactionFindByPortfolioId
		PreparedStatement transactionFindByPortfolioId = connect.prepareStatement(transactionFindByPortfolioIdSQL);
		transactionFindByPortfolioId.setString(1, portfolioId);
		rs = transactionFindByPortfolioId.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	// Rest Controller
	@ChoppedTransaction(microservice="m1")
	public void restObtainPortfolio(String identifier) throws SQLException {
		String restObtainPortfolioSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE identifier = ?";

		// restObtainPortfolio
		PreparedStatement restObtainPortfolio = connect.prepareStatement(restObtainPortfolioSQL);
		restObtainPortfolio.setString(1, identifier);
		ResultSet rs = restObtainPortfolio.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}
}
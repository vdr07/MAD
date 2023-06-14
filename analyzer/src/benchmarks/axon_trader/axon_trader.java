package benchmarks.axon_trader;

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

public class axon_trader {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public axon_trader(int id) {
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

	// User Controller
	@ChoppedTransaction(microservice="m1")
	public void showUsers() throws SQLException {
		String showUsersSQL = 
				"SELECT * FROM " + "USER_VIEW"+
				" WHERE identifier != 'blabla'";

		PreparedStatement showUsers = connect.prepareStatement(showUsersSQL);
		ResultSet rs = showUsers.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		return;
	}

	@ChoppedTransaction(microservice="m1")
	public void detail(String userIdentifier) throws SQLException {
		String detailSQL = 
				"SELECT * FROM " + "USER_VIEW"+
				" WHERE identifier = ?";

		PreparedStatement detail = connect.prepareStatement(detailSQL);
		detail.setString(1, userIdentifier);
		ResultSet rs = detail.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		return;
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

			bookEntry.getString("identifier");
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

		return;
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

		String companyName = companyView.getString("name");

		return;
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
		if (!sellOrdersRS.next()) {
			System.out.println("Empty");
		}
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
		if (!buyOrdersRS.next()) {
			System.out.println("Empty");
		}
		while (buyOrdersRS.next()) {
			long orderJpaId = buyOrdersRS.getLong("orderId");
			PreparedStatement orderViews = connect.prepareStatement(orderViewsSQL);
			orderViews.setLong(1, orderJpaId);
			ResultSet rs = orderViews.executeQuery();
			if (!rs.next()) {
				System.out.println("Empty");
			}
		}

		return;
	}

	@ChoppedTransaction(microservice="m1")
	public void companyGet() throws SQLException {
		String companyGetSQL = 
				"SELECT * FROM " + "COMPANY_VIEW"+
				" WHERE identifier != 'blabla'";

		PreparedStatement companyGet = connect.prepareStatement(companyGetSQL);
		ResultSet rs = companyGet.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		return;
	}

	@ChoppedTransaction(microservice="m1")
	public void sell(String companyIdentifier, String loggedInUsername, int bindingResult,
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

		String findItemInPossessionSQL = 
				"SELECT * FROM " + "PORTFOLIO_ITEM_POSSESSION"+
				" WHERE portfolioId = ? AND orderBookId = ?";

		String findItemReservedSQL = 
				"SELECT * FROM " + "PORTFOLIO_ITEM_RESERVED"+
				" WHERE portfolioId = ? AND orderBookId = ?";

		String obtainAmountOfItemsForSQL = 
				"SELECT * FROM " + "ITEM_ENTRY"+
				" WHERE generatedId = ?";

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
			long itemInPossessionId = itemInPossession.getLong("itemId");

			PreparedStatement obtainAmountOfItemsFor = connect.prepareStatement(obtainAmountOfItemsForSQL);
			obtainAmountOfItemsFor.setLong(1, itemInPossessionId);
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
			long itemReservedId = itemReserved.getLong("itemId");

			obtainAmountOfItemsFor = connect.prepareStatement(obtainAmountOfItemsForSQL);
			obtainAmountOfItemsFor.setLong(1, itemReservedId);
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
				itemInPossessionId = itemInPossession.getLong("itemId");

				obtainAmountOfItemsFor = connect.prepareStatement(obtainAmountOfItemsForSQL);
				obtainAmountOfItemsFor.setLong(1, itemInPossessionId);
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
				itemReservedId = itemReserved.getLong("itemId");

				obtainAmountOfItemsFor = connect.prepareStatement(obtainAmountOfItemsForSQL);
				obtainAmountOfItemsFor.setLong(1, itemReservedId);
				obtainAmountOfItems = obtainAmountOfItemsFor.executeQuery();
				if (!obtainAmountOfItems.next()) {
					System.out.println("Empty");
				}
				itemReservedAmount = obtainAmountOfItems.getLong("amount");
				return;
			}

			// bookEntry.getString("identifier");
			// portfolioView.getString("identifier");

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
		long itemInPossessionId = itemInPossession.getLong("itemId");

		PreparedStatement obtainAmountOfItemsFor = connect.prepareStatement(obtainAmountOfItemsForSQL);
		obtainAmountOfItemsFor.setLong(1, itemInPossessionId);
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
		long itemReservedId = itemReserved.getLong("itemId");

		obtainAmountOfItemsFor = connect.prepareStatement(obtainAmountOfItemsForSQL);
		obtainAmountOfItemsFor.setLong(1, itemReservedId);
		obtainAmountOfItems = obtainAmountOfItemsFor.executeQuery();
		if (!obtainAmountOfItems.next()) {
			System.out.println("Empty");
		}
		long itemReservedAmount = obtainAmountOfItems.getLong("amount");

		return;
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
				" WHERE portfolioId = ? AND orderBookId = ?";

		String findItemReservedSQL = 
				"SELECT * FROM " + "PORTFOLIO_ITEM_RESERVED"+
				" WHERE portfolioId = ? AND orderBookId = ?";

		String obtainAmountOfItemsForSQL = 
				"SELECT * FROM " + "ITEM_ENTRY"+
				" WHERE generatedId = ?";

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
		long itemInPossessionId = itemInPossession.getLong("itemId");

		PreparedStatement obtainAmountOfItemsFor = connect.prepareStatement(obtainAmountOfItemsForSQL);
		obtainAmountOfItemsFor.setLong(1, itemInPossessionId);
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
		long itemReservedId = itemReserved.getLong("itemId");

		obtainAmountOfItemsFor = connect.prepareStatement(obtainAmountOfItemsForSQL);
		obtainAmountOfItemsFor.setLong(1, itemReservedId);
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
		String companyName = companyView.getString("name");

		return;
	}

	// OrderBook Controller
	@ChoppedTransaction(microservice="m1")
	public void orderBookGet() throws SQLException {
		// findall
		String orderBookGetSQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE identifier != 'blabla'";

		PreparedStatement orderBookGet = connect.prepareStatement(orderBookGetSQL);
		ResultSet rs = orderBookGet.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		return;
	}

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

		return;
	}

	// Admin Controller
	@ChoppedTransaction(microservice="m1")
	public void adminShow() throws SQLException {
		// findall
		String adminShowSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE identifier != 'blabla'";

		PreparedStatement adminShow = connect.prepareStatement(adminShowSQL);
		ResultSet rs = adminShow.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		return;
	}

	@ChoppedTransaction(microservice="m1")
	public void adminShowPortfolio(String identifier) throws SQLException {
		String adminShowPortfolioSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE identifier = ?";
		
		String orderBookViewFindAllSQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE identifier != 'blabla'";

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

		return;
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

		return;
	}

	// Rest Controller
	@ChoppedTransaction(microservice="m1")
	public void restObtainOrderBooks() throws SQLException {
		String restObtainOrderBooksSQL = 
				"SELECT * FROM " + "ORDER_BOOK_VIEW"+
				" WHERE identifier != 'blabla'";

		// restObtainOrderBooks
		PreparedStatement restObtainOrderBooks = connect.prepareStatement(restObtainOrderBooksSQL);
		ResultSet rs = restObtainOrderBooks.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		return;
	}

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

		return;
	}

	@ChoppedTransaction(microservice="m1")
	public void restObtainPortfolios() throws SQLException {
		String restObtainPortfoliosSQL = 
				"SELECT * FROM " + "PORTFOLIO_VIEW"+
				" WHERE identifier != 'blabla'";

		// restObtainPortfolios
		PreparedStatement restObtainPortfolios = connect.prepareStatement(restObtainPortfoliosSQL);
		ResultSet rs = restObtainPortfolios.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		return;
	}
}
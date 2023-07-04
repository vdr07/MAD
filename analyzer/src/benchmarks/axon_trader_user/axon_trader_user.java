package benchmarks.axon_trader_user;

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

public class axon_trader_user {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public axon_trader_user(int id) {
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
	}

	@ChoppedTransaction(microservice="m1")
	public void createUser(String userIdentifier, String name, String uname, String password) throws SQLException {
		String createUserSQL = 
				"INSERT INTO " + "USER_VIEW" +
				" (identifier, uname, username, upassword) " +
				" VALUES ( ?, ?, ?, ? )";

		PreparedStatement createUser = connect.prepareStatement(createUserSQL);
		createUser.setString(1, userIdentifier);
		createUser.setString(2, name);
		createUser.setString(3, uname);
		createUser.setString(4, password);
		createUser.executeUpdate();
	}

	@ChoppedTransaction(microservice="m1")
	public void authenticateUser(String userIdentifier, String password) throws SQLException {
		String authenticateUserSQL = 
				"SELECT upassword FROM " + "USER_VIEW"+
				" WHERE identifier = ?";

		PreparedStatement authenticateUser = connect.prepareStatement(authenticateUserSQL);
		authenticateUser.setString(1, userIdentifier);
		ResultSet rs = authenticateUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		String userPassword = rs.getString("upassword");
		assert (userPassword.equals(password));
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
}
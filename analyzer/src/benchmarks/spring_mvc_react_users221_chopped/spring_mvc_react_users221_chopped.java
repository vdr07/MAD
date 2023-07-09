package benchmarks.spring_mvc_react_users22_chopped;

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

public class spring_mvc_react_users22_chopped {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public spring_mvc_react_users22_chopped(int id) {
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

	@ChoppedTransaction(originalTransaction="answerCreateQuestion", microservice="m2")
	public void answerCreateQuestion1(String username) throws SQLException {
		String userGetByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
		userGetByUsername.setString(1, username);
		ResultSet rs = userGetByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long userId = rs.getLong("id");
	}

	@ChoppedTransaction(originalTransaction="answerCreateQuestion", microservice="m3")
	public void answerCreateQuestion2(long questionId) throws SQLException {
		String questionGetByIdSQL = 
				"SELECT * FROM " + "QUESTIONS"+
				" WHERE id = ?";

		PreparedStatement questionGetById = connect.prepareStatement(questionGetByIdSQL);
		questionGetById.setLong(1, questionId);
		ResultSet rs = questionGetById.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long questionUserId = rs.getLong("userId");
	}

	@ChoppedTransaction(originalTransaction="answerCreateQuestion", microservice="m2")
	public void answerCreateQuestion3(long questionUserId, long userId, String username) throws SQLException {
		String userGetByIdSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE id = ?";

		PreparedStatement userGetById = connect.prepareStatement(userGetByIdSQL);
		userGetById.setLong(1, questionUserId);
		ResultSet rs = userGetById.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		String questionUsername = rs.getString("username");

		if (questionUsername.equals(username)) {
			userId = questionUserId;
		}
	}

	@ChoppedTransaction(originalTransaction="answerCreateQuestion", microservice="m1")
	public void answerCreateQuestion4(long answerId, String comment, String currentDate,
			long userId, long questionId) throws SQLException {
		String answerAddAnswerSQL = 
				"INSERT INTO " + "ANSWERS" +
				" (id, comment, createdAt, updatedAt, userId, questionId) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";

		PreparedStatement answerAddAnswer = connect.prepareStatement(answerAddAnswerSQL);
		answerAddAnswer.setLong(1, answerId);
		answerAddAnswer.setString(2, comment);
		answerAddAnswer.setString(3, currentDate);
		answerAddAnswer.setString(4, currentDate);
		answerAddAnswer.setLong(5, userId);
		answerAddAnswer.setLong(6, questionId);
		answerAddAnswer.executeUpdate();
	}

	// AuthorizationController
	/*@ChoppedTransaction(microservice="m2")
	public void authorizationLogin(String username, String password) throws SQLException {
		String userGetByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
		userGetByUsername.setString(1, username);
		ResultSet rs = userGetByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("User not found");
			return;
		}
		long userId = rs.getLong("id");
		String userPassword = rs.getString("password");

		if (!password.equals(userPassword)) {
			System.out.println("Wrong password");
			return;	
		}
	}

	@ChoppedTransaction(microservice="m2")
	public void authorizationRegister(long userId, String username, String password,
			String currentDate) throws SQLException {
		String userGetByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";
		
		String userAddUserSQL = 
				"INSERT INTO " + "USERS" +
				" (id, username, password, createdAt, status, popular) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";

		PreparedStatement userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
		userGetByUsername.setString(1, username);
		ResultSet rs = userGetByUsername.executeQuery();
		if (rs.next()) {
			System.out.println("User already exists");
			return;
		}

		PreparedStatement userAddUser = connect.prepareStatement(userAddUserSQL);
		userAddUser.setLong(1, userId);
		userAddUser.setString(2, username);
		userAddUser.setString(3, password);
		userAddUser.setString(4, currentDate);
		userAddUser.setString(5, "active");
		userAddUser.setInt(6, 0);
		userAddUser.executeUpdate();
	}

	// UserController
	@ChoppedTransaction(microservice="m2")
	public void userListAllUsers() throws SQLException {
		String userListAllUsersSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE 1 = 1";

		PreparedStatement userListAllUsers = connect.prepareStatement(userListAllUsersSQL);
		ResultSet rs = userListAllUsers.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(microservice="m2")
	public void userGetUserByName(String username) throws SQLException {
		String userGetUserByNameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement userGetUserByName = connect.prepareStatement(userGetUserByNameSQL);
		userGetUserByName.setString(1, username);
		ResultSet rs = userGetUserByName.executeQuery();
		if (!rs.next()) {
			System.out.println("User not found");
			return;
		}
	}

	@ChoppedTransaction(microservice="m2")
	public void userGetUser(long userId) throws SQLException {
		String userGetUserByIdSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE id = ?";

		PreparedStatement userGetUserById = connect.prepareStatement(userGetUserByIdSQL);
		userGetUserById.setLong(1, userId);
		ResultSet rs = userGetUserById.executeQuery();
		if (!rs.next()) {
			System.out.println("User not found");
			return;
		}
	}*/

	@ChoppedTransaction(microservice="m2")
	public void userCreateUser(long userId, String username, String password,
			String currentDate, String status, int popular) throws SQLException {
		String userGetByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";
		
		String userAddUserSQL = 
				"INSERT INTO " + "USERS" +
				" (id, username, password, createdAt, status, popular) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";

		PreparedStatement userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
		userGetByUsername.setString(1, username);
		ResultSet rs = userGetByUsername.executeQuery();
		if (rs.next()) {
			System.out.println("User already exists");
			return;
		}

		PreparedStatement userAddUser = connect.prepareStatement(userAddUserSQL);
		userAddUser.setLong(1, userId);
		userAddUser.setString(2, username);
		userAddUser.setString(3, password);
		userAddUser.setString(4, currentDate);
		userAddUser.setString(5, status);
		userAddUser.setInt(6, popular);
		userAddUser.executeUpdate();
	}

	@ChoppedTransaction(microservice="m2")
	public void userCreateQuestion(String username, String newPassword, String oldPassword) throws SQLException {
		String userGetByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String userUpdatePasswordSQL = 
				"UPDATE " + "USERS" + 
				"   SET password = ?" +
				" WHERE id = ? ";

		PreparedStatement userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
		userGetByUsername.setString(1, username);
		ResultSet rs = userGetByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long userId = rs.getLong("id");
		String userPassword = rs.getString("password");

		if (!userPassword.equals(oldPassword)) {
			System.out.println("wrong old password");
			return;
		}

		PreparedStatement userUpdatePassword = connect.prepareStatement(userUpdatePasswordSQL);
		userUpdatePassword.setString(1, newPassword);
		userUpdatePassword.setLong(2, userId);
		userUpdatePassword.executeUpdate();
	}

	@ChoppedTransaction(microservice="m2")
	public void userUpdateUser(long userId, String username, String password) throws SQLException {
		String userGetByIdSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE id = ?";

		String userUpdateUsernamePasswordSQL = 
				"UPDATE " + "USERS" + 
				"   SET username = ?," +
				"       password = ?" +
				" WHERE id = ? ";

		PreparedStatement userGetById = connect.prepareStatement(userGetByIdSQL);
		userGetById.setLong(1, userId);
		ResultSet rs = userGetById.executeQuery();
		if (!rs.next()) {
			System.out.println("User not found");
			return;
		}

		PreparedStatement userUpdateUsernamePassword = connect.prepareStatement(userUpdateUsernamePasswordSQL);
		userUpdateUsernamePassword.setString(1, username);
		userUpdateUsernamePassword.setString(2, password);
		userUpdateUsernamePassword.setLong(3, userId);
		userUpdateUsernamePassword.executeUpdate();
	}

	/*@ChoppedTransaction(microservice="m2")
	public void userDeleteUser(long userId) throws SQLException {
		String userGetByIdSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE id = ?";

		String userDeleteSQL = 
				"DELETE FROM " + "USERS"+
				" WHERE id = ?";

		PreparedStatement userGetById = connect.prepareStatement(userGetByIdSQL);
		userGetById.setLong(1, userId);
		ResultSet rs = userGetById.executeQuery();
		if (!rs.next()) {
			System.out.println("User not found");
			return;
		}

		PreparedStatement userDelete = connect.prepareStatement(userDeleteSQL);
		userDelete.setLong(1, userId);
		userDelete.executeUpdate();
	}

	// AjaxController
	@ChoppedTransaction(microservice="m2")
	public void ajaxGetSearchResultViaAjax(String username, String email) throws SQLException {
		String userGetByUsernameOrEmailSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ? AND username = ?";

		String userGetByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String userGetByEmailSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		if (!username.equals("") && !email.equals("")) {
			PreparedStatement userGetByUsernameOrEmail = connect.prepareStatement(userGetByUsernameOrEmailSQL);
			userGetByUsernameOrEmail.setString(1, username);
			userGetByUsernameOrEmail.setString(2, email);
			ResultSet rs = userGetByUsernameOrEmail.executeQuery();
			if (!rs.next()) {
				System.out.println("Empty");
			}
		} else if (!username.equals("")) {
			PreparedStatement userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
			userGetByUsername.setString(1, username);
			ResultSet rs = userGetByUsername.executeQuery();
			if (!rs.next()) {
				System.out.println("Empty");
			}
		} else if (!email.equals("")) {
			PreparedStatement userGetByEmail = connect.prepareStatement(userGetByEmailSQL);
			userGetByEmail.setString(1, email);
			ResultSet rs = userGetByEmail.executeQuery();
			if (!rs.next()) {
				System.out.println("Empty");
			}
		}
	}*/
}
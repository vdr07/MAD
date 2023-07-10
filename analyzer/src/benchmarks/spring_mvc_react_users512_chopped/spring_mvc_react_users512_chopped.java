package benchmarks.spring_mvc_react_users512_chopped;

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

public class spring_mvc_react_users512_chopped {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public spring_mvc_react_users512_chopped(int id) {
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

	// AuthorizationController
	@ChoppedTransaction(microservice="m2")
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

	/*@ChoppedTransaction(microservice="m2")
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

	/*@ChoppedTransaction(microservice="m2")
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

	@ChoppedTransaction(microservice="m2")
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
	}*/

	// VoteController
	@ChoppedTransaction(originalTransaction="voteCreateQuestion", microservice="m2")
	public void voteCreateQuestion1(String username) throws SQLException {
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
		String userUsername = rs.getString("username");
		int userPopular = rs.getInt("popular");
	}

	@ChoppedTransaction(originalTransaction="voteCreateQuestion", microservice="m3")
	public void voteCreateQuestion2(long questionId) throws SQLException {
		String questionGetByIdSQL = 
				"SELECT * FROM " + "QUESTIONS"+
				" WHERE id = ?";

		PreparedStatement questionGetById = connect.prepareStatement(questionGetByIdSQL);
		questionGetById.setLong(1, questionId);
		ResultSet rs = questionGetById.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(originalTransaction="voteCreateQuestion", microservice="m1")
	public void voteCreateQuestion3(long answerId) throws SQLException {
		String answerGetByIdSQL = 
				"SELECT * FROM " + "ANSWERS"+
				" WHERE id = ?";

		PreparedStatement answerGetById = connect.prepareStatement(answerGetByIdSQL);
		answerGetById.setLong(1, answerId);
		ResultSet rs = answerGetById.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}
	
	@ChoppedTransaction(originalTransaction="voteCreateQuestion", microservice="m2")
	public void voteCreateQuestion41(long questionUserId, String username, long userId, 
			String mark, int newPopular, int sawQuestion) throws SQLException {
		String userUsernameGetByIdSQL = 
				"SELECT username FROM " + "USERS"+
				" WHERE id = ?";

		String userUpdatePopularSQL = 
				"UPDATE " + "USERS" + 
				"   SET popular = ?" +
				" WHERE id = ? ";

		PreparedStatement userUsernameGetById = connect.prepareStatement(userUsernameGetByIdSQL);
		userUsernameGetById.setLong(1, questionUserId);
		ResultSet questionUser = userUsernameGetById.executeQuery();
		if (!questionUser.next()) {
			System.out.println("Empty");
		}
		String questionUsername = questionUser.getString("username");

		if (questionUsername.equals(username)) {
			userId = questionUserId;
		}

		if (mark.equals("DOWN")) newPopular -= 2;
		else newPopular += 5;

		sawQuestion = 1;

		if (sawQuestion == 1) {
			PreparedStatement userUpdatePopular = connect.prepareStatement(userUpdatePopularSQL);
			userUpdatePopular.setInt(1, newPopular);
			userUpdatePopular.setLong(2, userId);
			userUpdatePopular.executeUpdate();
		}
	}

	/*@ChoppedTransaction(originalTransaction="voteCreateQuestion", microservice="m3")
	public void voteCreateQuestion42(long answerQuestionId) throws SQLException {
		String questionUserIdGetByIdSQL = 
				"SELECT userId FROM " + "QUESTIONS"+
				" WHERE id = ?";

		PreparedStatement questionUserIdGetById = connect.prepareStatement(questionUserIdGetByIdSQL);
		questionUserIdGetById.setLong(1, answerQuestionId);
		ResultSet question = questionUserIdGetById.executeQuery();
		if (!question.next()) {
			System.out.println("Empty");
		}
		long questionUserId = question.getLong("userId");
	}

	@ChoppedTransaction(originalTransaction="voteCreateQuestion", microservice="m2")
	public void voteCreateQuestion52(long questionUserId, String username, long userId,
		String mark, int newPopular, int sawAnswer) throws SQLException {
		String userUsernameGetByIdSQL = 
				"SELECT username FROM " + "USERS"+
				" WHERE id = ?";

		String userUpdatePopularSQL = 
				"UPDATE " + "USERS" + 
				"   SET popular = ?" +
				" WHERE id = ? ";

		PreparedStatement userUsernameGetById = connect.prepareStatement(userUsernameGetByIdSQL);
		userUsernameGetById.setLong(1, questionUserId);
		ResultSet questionUser = userUsernameGetById.executeQuery();
		if (!questionUser.next()) {
			System.out.println("Empty");
		}
		String questionUsername = questionUser.getString("username");

		if (questionUsername.equals(username)) {
			userId = questionUserId;
		}

		if (mark.equals("DOWN")) newPopular -= 2;
		else newPopular += 10;

		sawAnswer = 1;
		
		if (sawAnswer == 1) {
			PreparedStatement userUpdatePopular = connect.prepareStatement(userUpdatePopularSQL);
			userUpdatePopular.setInt(1, newPopular);
			userUpdatePopular.setLong(2, userId);
			userUpdatePopular.executeUpdate();
		}
	}*/

	@ChoppedTransaction(originalTransaction="voteCreateQuestion", microservice="m5")
	public void voteCreateQuestion6(long voteId, long questionId, long answerId, long userId,
			String mark) throws SQLException {
		String voteAddVoteSQL = 
				"INSERT INTO " + "VOTES" +
				" (id, module, questionId, answerId, userId, mark) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";

		PreparedStatement voteAddVote = connect.prepareStatement(voteAddVoteSQL);
		voteAddVote.setLong(1, voteId);
		voteAddVote.setString(2, "");
		voteAddVote.setLong(3, questionId);
		voteAddVote.setLong(4, answerId);
		voteAddVote.setLong(5, userId);
		voteAddVote.setString(6, mark);
		voteAddVote.executeUpdate();
	}

	// AjaxController
	/*@ChoppedTransaction(microservice="m2")
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
package benchmarks.spring_mvc_react_users;

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

public class spring_mvc_react_users {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public spring_mvc_react_users(int id) {
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
	public void answerGetAnswersByUser(String username) throws SQLException {
		String userGetByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String answerGetByUserSQL = 
				"SELECT * FROM " + "ANSWERS"+
				" WHERE userId = ?";

		PreparedStatement userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
		userGetByUsername.setString(1, username);
		ResultSet rs = userGetByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long userId = rs.getLong("id");

		PreparedStatement answerGetByUser = connect.prepareStatement(answerGetByUserSQL);
		answerGetByUser.setLong(1, userId);
		rs = answerGetByUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void answerCreateQuestion(String username, long questionId, long answerId, 
			String comment, String currentDate) throws SQLException {
		String userGetByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String questionGetByIdSQL = 
				"SELECT * FROM " + "QUESTIONS"+
				" WHERE id = ?";

		String userGetByIdSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE id = ?";

		String answerAddAnswerSQL = 
				"INSERT INTO " + "ANSWERS" +
				" (id, comment, createdAt, updatedAt, userId, questionId) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";

		PreparedStatement userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
		userGetByUsername.setString(1, username);
		ResultSet rs = userGetByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long userId = rs.getLong("id");

		PreparedStatement questionGetById = connect.prepareStatement(questionGetByIdSQL);
		questionGetById.setLong(1, questionId);
		rs = questionGetById.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long questionUserId = rs.getLong("userId");

		PreparedStatement userGetById = connect.prepareStatement(userGetByIdSQL);
		userGetById.setLong(1, questionUserId);
		rs = userGetById.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		String questionUsername = rs.getString("username");

		if (questionUsername.equals(username)) {
			userId = questionUserId;
		}

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
	@ChoppedTransaction(microservice="m1")
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

	@ChoppedTransaction(microservice="m1")
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

	@ChoppedTransaction(microservice="m1")
	public void questionGetQuestionsByUser(String username) throws SQLException {
		String userGetByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String questionGetByUserSQL = 
				"SELECT * FROM " + "QUESTIONS"+
				" WHERE userId = ?";

		PreparedStatement userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
		userGetByUsername.setString(1, username);
		ResultSet rs = userGetByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long userId = rs.getLong("id");

		PreparedStatement questionGetByUser = connect.prepareStatement(questionGetByUserSQL);
		questionGetByUser.setLong(1, userId);
		rs = questionGetByUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void questionCreateQuestion(String username, String[] tagNames, long tagId, String currentDate,
			long questionId, String questionTitle, String questionAgo, String questionComment) throws SQLException {
		String userGetByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String tagGetByNameSQL = 
				"SELECT * FROM " + "TAGS"+
				" WHERE name = ?";

		String tagUpdatePopularSQL = 
				"UPDATE " + "TAGS" + 
				"   SET popular = ?" +
				" WHERE id = ? ";

		String tagAddTagSQL = 
				"INSERT INTO " + "TAGS" +
				" (id, name, description, popular, createdAt, userId) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";

		String questionAddQuestionSQL = 
				"INSERT INTO " + "QUESTIONS" +
				" (id, tile, ago, comment, userId, createdAt, updatedAt) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ? )";
		
		String questionTagAddQuestionTagSQL = 
				"INSERT INTO " + "QUESTION_TAG" +
				" (questionId, tagId) " +
				" VALUES ( ?, ? )";

		PreparedStatement userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
		userGetByUsername.setString(1, username);
		ResultSet rs = userGetByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
			return;
		}
		long userId = rs.getLong("id");

		for (String tagName : tagNames) {
			PreparedStatement tagGetByName = connect.prepareStatement(tagGetByNameSQL);
			tagGetByName.setString(1, tagName);
			rs = tagGetByName.executeQuery();
			if (rs.next()) {
				tagId = rs.getLong("id");
				int tagCurrentPopular = rs.getInt("popular");
				PreparedStatement tagUpdatePopular = connect.prepareStatement(tagUpdatePopularSQL);
				tagUpdatePopular.setInt(1, tagCurrentPopular + 1);
				tagUpdatePopular.setLong(2, tagId);
				tagUpdatePopular.executeUpdate();
			} else {
				PreparedStatement tagAddTag = connect.prepareStatement(tagAddTagSQL);
				tagAddTag.setLong(1, tagId);
				tagAddTag.setString(2, tagName);
				tagAddTag.setString(3, "");
				tagAddTag.setInt(4, 0);
				tagAddTag.setString(5, currentDate);
				tagAddTag.setLong(6, userId);
				tagAddTag.executeUpdate();
			}
		}

		PreparedStatement questionAddQuestion = connect.prepareStatement(questionAddQuestionSQL);
		questionAddQuestion.setLong(1, questionId);
		questionAddQuestion.setString(2, questionTitle);
		questionAddQuestion.setString(3, questionAgo);
		questionAddQuestion.setString(4, questionComment);
		questionAddQuestion.setLong(5, userId);
		questionAddQuestion.setString(6, currentDate);
		questionAddQuestion.setString(7, currentDate);
		questionAddQuestion.executeUpdate();

		for (String tagName : tagNames) {
			PreparedStatement tagGetByName = connect.prepareStatement(tagGetByNameSQL);
			tagGetByName.setString(1, tagName);
			rs = tagGetByName.executeQuery();
			if (!rs.next()) {
				System.out.println("No tag was found");
				return;
			}
			tagId = rs.getLong("id");

			PreparedStatement questionTagAddQuestionTag = connect.prepareStatement(questionTagAddQuestionTagSQL);
			questionTagAddQuestionTag.setLong(1, questionId);
			questionTagAddQuestionTag.setLong(2, tagId);
			questionTagAddQuestionTag.executeUpdate();
		}
	}

	// UserController
	@ChoppedTransaction(microservice="m1")
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

	@ChoppedTransaction(microservice="m1")
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

	@ChoppedTransaction(microservice="m1")
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
	}

	@ChoppedTransaction(microservice="m1")
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

	@ChoppedTransaction(microservice="m1")
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

	@ChoppedTransaction(microservice="m1")
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

	@ChoppedTransaction(microservice="m1")
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

	// VoteController
	@ChoppedTransaction(microservice="m1")
	public void voteCreateQuestion(String username, long questionId, long answerId, String mark,
			long voteId, String currentDate) throws SQLException {
		String userGetByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String questionGetByIdSQL = 
				"SELECT * FROM " + "QUESTIONS"+
				" WHERE id = ?";

		String userGetByIdSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE id = ?";

		String answerGetByIdSQL = 
				"SELECT * FROM " + "ANSWERS"+
				" WHERE id = ?";

		String userUpdatePopularSQL = 
				"UPDATE " + "USERS" + 
				"   SET popular = ?" +
				" WHERE id = ? ";

		String voteAddVoteSQL = 
				"INSERT INTO " + "VOTES" +
				" (id, module, questionId, answerId, userId, mark) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";

		PreparedStatement userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
		userGetByUsername.setString(1, username);
		ResultSet rs = userGetByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long userId = rs.getLong("id");
		int userPopular = rs.getInt("popular");

		int newPopular = userPopular;
		PreparedStatement questionGetById = connect.prepareStatement(questionGetByIdSQL);
		questionGetById.setLong(1, questionId);
		rs = questionGetById.executeQuery();
		int sawQuestion = 0;
		if (rs.next()) {
			userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
			userGetByUsername.setString(1, username);
			ResultSet user = userGetByUsername.executeQuery();
			if (!user.next()) {
				System.out.println("Empty");
			}
			userId = user.getLong("id");

			questionGetById = connect.prepareStatement(questionGetByIdSQL);
			questionGetById.setLong(1, questionId);
			ResultSet question = questionGetById.executeQuery();
			if (!question.next()) {
				System.out.println("Empty");
			}
			long questionUserId = question.getLong("userId");

			PreparedStatement userGetById = connect.prepareStatement(userGetByIdSQL);
			userGetById.setLong(1, questionUserId);
			ResultSet questionUser = userGetById.executeQuery();
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
		}

		PreparedStatement answerGetById = connect.prepareStatement(answerGetByIdSQL);
		answerGetById.setLong(1, answerId);
		rs = answerGetById.executeQuery();
		int sawAnswer = 0;
		if (rs.next() && sawQuestion == 0) {
			userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
			userGetByUsername.setString(1, username);
			ResultSet user = userGetByUsername.executeQuery();
			if (!user.next()) {
				System.out.println("Empty");
			}
			userId = user.getLong("id");

			answerGetById = connect.prepareStatement(answerGetByIdSQL);
			answerGetById.setLong(1, questionId);
			ResultSet answer = answerGetById.executeQuery();
			if (!answer.next()) {
				System.out.println("Empty");
			}
			long answerQuestionId = answer.getLong("questionId");

			questionGetById = connect.prepareStatement(questionGetByIdSQL);
			questionGetById.setLong(1, answerQuestionId);
			ResultSet question = questionGetById.executeQuery();
			if (!question.next()) {
				System.out.println("Empty");
			}
			long questionUserId = question.getLong("userId");

			PreparedStatement userGetById = connect.prepareStatement(userGetByIdSQL);
			userGetById.setLong(1, questionUserId);
			ResultSet questionUser = userGetById.executeQuery();
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
		}

		if (sawQuestion == 1 || sawAnswer == 1) {
			PreparedStatement userUpdatePopular = connect.prepareStatement(userUpdatePopularSQL);
			userUpdatePopular.setInt(1, newPopular);
			userUpdatePopular.setLong(2, userId);
			userUpdatePopular.executeUpdate();
		}

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
	@ChoppedTransaction(microservice="m1")
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
	}
}
package benchmarks.spring_mvc_react_chopped;

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

public class spring_mvc_react_chopped {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public spring_mvc_react_chopped(int id) {
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

	// AnswerController
	@ChoppedTransaction(microservice="m1")
	public void answerListAllAnswers() throws SQLException {
		String answerGetAllAnswersSQL = 
				"SELECT * FROM " + "ANSWERS"+
				" WHERE 1 = 1";

		PreparedStatement answerGetAllAnswers = connect.prepareStatement(answerGetAllAnswersSQL);
		ResultSet rs = answerGetAllAnswers.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void answerGetAnswer(long answerId) throws SQLException {
		String answerGetAnswerSQL = 
				"SELECT * FROM " + "ANSWERS"+
				" WHERE id = ?";

		PreparedStatement answerGetAnswer = connect.prepareStatement(answerGetAnswerSQL);
		answerGetAnswer.setLong(1, answerId);
		ResultSet rs = answerGetAnswer.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(originalTransaction="answerGetAnswersByUser", microservice="m2")
	public void answerGetAnswersByUser1(String username) throws SQLException {
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

	@ChoppedTransaction(originalTransaction="answerGetAnswersByUser", microservice="m1")
	public void answerGetAnswersByUser2(long userId) throws SQLException {
		String answerGetByUserSQL = 
				"SELECT * FROM " + "ANSWERS"+
				" WHERE userId = ?";

		PreparedStatement answerGetByUser = connect.prepareStatement(answerGetByUserSQL);
		answerGetByUser.setLong(1, userId);
		ResultSet rs = answerGetByUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
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

	// QuestionController
	@ChoppedTransaction(microservice="m3")
	public void questionListAllQuestions() throws SQLException {
		String questionGetAllQuestionsSQL = 
				"SELECT * FROM " + "QUESTIONS"+
				" WHERE 1 = 1";

		PreparedStatement questionGetAllQuestions = connect.prepareStatement(questionGetAllQuestionsSQL);
		ResultSet rs = questionGetAllQuestions.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(microservice="m3")
	public void questionGetQuestion(long questionId) throws SQLException {
		String questionGetQuestionSQL = 
				"SELECT * FROM " + "QUESTIONS"+
				" WHERE id = ?";

		PreparedStatement questionGetQuestion = connect.prepareStatement(questionGetQuestionSQL);
		questionGetQuestion.setLong(1, questionId);
		ResultSet rs = questionGetQuestion.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(originalTransaction="questionGetQuestionsByUser", microservice="m2")
	public void questionGetQuestionsByUser1(String username) throws SQLException {
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

	@ChoppedTransaction(originalTransaction="questionGetQuestionsByUser", microservice="m3")
	public void questionGetQuestionsByUser2(long userId) throws SQLException {
		String questionGetByUserSQL = 
				"SELECT * FROM " + "QUESTIONS"+
				" WHERE userId = ?";

		PreparedStatement questionGetByUser = connect.prepareStatement(questionGetByUserSQL);
		questionGetByUser.setLong(1, userId);
		ResultSet rs = questionGetByUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(originalTransaction="questionGetQuestionsByTag", microservice="m4")
	public void questionGetQuestionsByTag1(String tagName) throws SQLException {
		String tagGetByNameSQL = 
				"SELECT * FROM " + "TAGS"+
				" WHERE name = ?";

		PreparedStatement tagGetByName = connect.prepareStatement(tagGetByNameSQL);
		tagGetByName.setString(1, tagName);
		ResultSet rs = tagGetByName.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long tagId = rs.getLong("id");
	}
	
	@ChoppedTransaction(originalTransaction="questionGetQuestionsByTag", microservice="m3")
	public void questionGetQuestionsByTag2(long tagId) throws SQLException {
		String questionTagGetByTagSQL = 
				"SELECT questionId FROM " + "QUESTION_TAG"+
				" WHERE tagId = ?";

		String questionGetByIdSQL = 
				"SELECT * FROM " + "QUESTIONS"+
				" WHERE id = ?";

		PreparedStatement questionTagGetByTag = connect.prepareStatement(questionTagGetByTagSQL);
		questionTagGetByTag.setLong(1, tagId);
		ResultSet rs = questionTagGetByTag.executeQuery();
		while (rs.next()) {
			long questionId = rs.getLong("questionId");
			PreparedStatement questionGetById = connect.prepareStatement(questionGetByIdSQL);
			questionGetById.setLong(1, questionId);
			ResultSet question = questionGetById.executeQuery();
			if (!question.next()) {
				System.out.println("Empty");
			}
		}
	}

	@ChoppedTransaction(originalTransaction="questionCreateQuestion", microservice="m2")
	public void questionCreateQuestion1(String username) throws SQLException {
		String userGetByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
		userGetByUsername.setString(1, username);
		ResultSet rs = userGetByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
			return;
		}
		long userId = rs.getLong("id");
	}

	@ChoppedTransaction(originalTransaction="questionCreateQuestion", microservice="m4")
	public void questionCreateQuestion2(String[] tagNames, long tagId, String currentDate,
			long userId) throws SQLException {
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

		for (String tagName : tagNames) {
			PreparedStatement tagGetByName = connect.prepareStatement(tagGetByNameSQL);
			tagGetByName.setString(1, tagName);
			ResultSet rs = tagGetByName.executeQuery();
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
	}

	@ChoppedTransaction(originalTransaction="questionCreateQuestion", microservice="m3")
	public void questionCreateQuestion3(long questionId, String questionTitle, String questionAgo, 
			String questionComment, long userId, String currentDate, String[] tagNames) throws SQLException {
		String questionAddQuestionSQL = 
				"INSERT INTO " + "QUESTIONS" +
				" (id, tile, ago, comment, userId, createdAt, updatedAt) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ? )";

		PreparedStatement questionAddQuestion = connect.prepareStatement(questionAddQuestionSQL);
		questionAddQuestion.setLong(1, questionId);
		questionAddQuestion.setString(2, questionTitle);
		questionAddQuestion.setString(3, questionAgo);
		questionAddQuestion.setString(4, questionComment);
		questionAddQuestion.setLong(5, userId);
		questionAddQuestion.setString(6, currentDate);
		questionAddQuestion.setString(7, currentDate);
		questionAddQuestion.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="questionCreateQuestion", microservice="m4")
	public void questionCreateQuestion4(String[] tagNames) throws SQLException {
		String tagGetByNameSQL = 
				"SELECT * FROM " + "TAGS"+
				" WHERE name = ?";

		for (String tagName : tagNames) {
			PreparedStatement tagGetByName = connect.prepareStatement(tagGetByNameSQL);
			tagGetByName.setString(1, tagName);
			ResultSet rs = tagGetByName.executeQuery();
			if (!rs.next()) {
				System.out.println("No tag was found");
				return;
			}
			long tagId = rs.getLong("id");
		}
	}

	@ChoppedTransaction(originalTransaction="questionCreateQuestion", microservice="m3")
	public void questionCreateQuestion5(long questionId, long[] tagIds) throws SQLException {
		String questionTagAddQuestionTagSQL = 
				"INSERT INTO " + "QUESTION_TAG" +
				" (questionId, tagId) " +
				" VALUES ( ?, ? )";

		for (long tagId : tagIds) {
			PreparedStatement questionTagAddQuestionTag = connect.prepareStatement(questionTagAddQuestionTagSQL);
			questionTagAddQuestionTag.setLong(1, questionId);
			questionTagAddQuestionTag.setLong(2, tagId);
			questionTagAddQuestionTag.executeUpdate();
		}
	}

	// TagController
	@ChoppedTransaction(microservice="m4")
	public void tagListAllTags() throws SQLException {
		String tagGetAllTagsSQL = 
				"SELECT * FROM " + "TAGS"+
				" WHERE 1 = 1";

		PreparedStatement tagGetAllTags = connect.prepareStatement(tagGetAllTagsSQL);
		ResultSet rs = tagGetAllTags.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(microservice="m4")
	public void tagGetTag(long tagId) throws SQLException {
		String tagGetTagSQL = 
				"SELECT * FROM " + "TAGS"+
				" WHERE id = ?";

		PreparedStatement tagGetTag = connect.prepareStatement(tagGetTagSQL);
		tagGetTag.setLong(1, tagId);
		ResultSet rs = tagGetTag.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(microservice="m4")
	public void tagGetTagsByTerm(String term) throws SQLException {
		String tagGetTagsByTermSQL = 
				"SELECT * FROM " + "TAGS"+
				" WHERE name = ?";

		PreparedStatement tagGetTagsByTerm = connect.prepareStatement(tagGetTagsByTermSQL);
		tagGetTagsByTerm.setString(1, term);
		ResultSet rs = tagGetTagsByTerm.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
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
	}

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
	}

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
	}
}
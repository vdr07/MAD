package benchmarks.spring_mvc_react;

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

public class spring_mvc_react {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public spring_mvc_react(int id) {
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
	public void answerListAllAnswers() throws SQLException {
		String answerGetAllAnswersSQL = 
				"SELECT * FROM " + "ANSWERS"+
				" WHERE 1 = 1";

		PreparedStatement answerGetAllAnswers = connect.prepareStatement(answerGetAllAnswersSQL);
		ResultSet rs = answerGetAllAnswers.executeQuery();
		rs.next();
	}

	public void answerGetAnswer(int answerId) throws SQLException {
		String answerGetAnswerSQL = 
				"SELECT * FROM " + "ANSWERS"+
				" WHERE id = ?";

		PreparedStatement answerGetAnswer = connect.prepareStatement(answerGetAnswerSQL);
		answerGetAnswer.setInt(1, answerId);
		ResultSet rs = answerGetAnswer.executeQuery();
		rs.next();
	}

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
		rs.next();
		int userId = rs.getInt("id");

		PreparedStatement answerGetByUser = connect.prepareStatement(answerGetByUserSQL);
		answerGetByUser.setInt(1, userId);
		ResultSet answerRs = answerGetByUser.executeQuery();
		answerRs.next();
	}

	public void answerCreateQuestion(String username, int questionId, int answerId, 
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
		rs.next();
		int userId = rs.getInt("id");

		PreparedStatement questionGetById = connect.prepareStatement(questionGetByIdSQL);
		questionGetById.setInt(1, questionId);
		ResultSet questionRs = questionGetById.executeQuery();
		questionRs.next();
		int questionUserId = questionRs.getInt("userId");

		PreparedStatement userGetById = connect.prepareStatement(userGetByIdSQL);
		userGetById.setInt(1, questionUserId);
		ResultSet userRs = userGetById.executeQuery();
		userRs.next();
		String questionUsername = userRs.getString("username");

		if (questionUsername.equals(username)) {
			userId = questionUserId;
		}

		PreparedStatement answerAddAnswer = connect.prepareStatement(answerAddAnswerSQL);
		answerAddAnswer.setInt(1, answerId);
		answerAddAnswer.setString(2, comment);
		answerAddAnswer.setString(3, currentDate);
		answerAddAnswer.setString(4, currentDate);
		answerAddAnswer.setInt(5, userId);
		answerAddAnswer.setInt(6, questionId);
		answerAddAnswer.executeUpdate();
	}

	// AuthorizationController
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
		int userId = rs.getInt("id");
		String userPassword = rs.getString("password");

		if (!password.equals(userPassword)) {
			System.out.println("Wrong password");
			return;	
		}
	}

	public void authorizationRegister(int userId, String username, String password,
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
		userAddUser.setInt(1, userId);
		userAddUser.setString(2, username);
		userAddUser.setString(3, password);
		userAddUser.setString(4, currentDate);
		userAddUser.setString(5, "active");
		userAddUser.setInt(6, 0);
		userAddUser.executeUpdate();
	}

	// QuestionController
	public void questionListAllQuestions() throws SQLException {
		String questionGetAllQuestionsSQL = 
				"SELECT * FROM " + "QUESTIONS"+
				" WHERE 1 = 1";

		PreparedStatement questionGetAllQuestions = connect.prepareStatement(questionGetAllQuestionsSQL);
		ResultSet rs = questionGetAllQuestions.executeQuery();
		rs.next();
	}

	public void questionGetQuestion(int questionId) throws SQLException {
		String questionGetQuestionSQL = 
				"SELECT * FROM " + "QUESTIONS"+
				" WHERE id = ?";

		PreparedStatement questionGetQuestion = connect.prepareStatement(questionGetQuestionSQL);
		questionGetQuestion.setInt(1, questionId);
		ResultSet rs = questionGetQuestion.executeQuery();
		rs.next();
	}

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
		rs.next();
		int userId = rs.getInt("id");

		PreparedStatement questionGetByUser = connect.prepareStatement(questionGetByUserSQL);
		questionGetByUser.setInt(1, userId);
		ResultSet questionRs = questionGetByUser.executeQuery();
		questionRs.next();
	}

	public void questionGetQuestionsByTag(String tagName) throws SQLException {
		String tagGetByNameSQL = 
				"SELECT * FROM " + "TAGS"+
				" WHERE name = ?";

		String questionTagGetByTagSQL = 
				"SELECT questionId FROM " + "QUESTION_TAG"+
				" WHERE tagId = ?";

		String questionGetByIdSQL = 
				"SELECT * FROM " + "QUESTIONS"+
				" WHERE id = ?";

		PreparedStatement tagGetByName = connect.prepareStatement(tagGetByNameSQL);
		tagGetByName.setString(1, tagName);
		ResultSet rs = tagGetByName.executeQuery();
		rs.next();
		int tagId = rs.getInt("id");

		PreparedStatement questionTagGetByTag = connect.prepareStatement(questionTagGetByTagSQL);
		questionTagGetByTag.setInt(1, tagId);
		ResultSet questionIdRs = questionTagGetByTag.executeQuery();
		while (questionIdRs.next()) {
			int questionId = questionIdRs.getInt("questionId");
			PreparedStatement questionGetById = connect.prepareStatement(questionGetByIdSQL);
			questionGetById.setInt(1, questionId);
			ResultSet question = questionGetById.executeQuery();
			question.next();
		}
	}

	public void questionCreateQuestion(String username, String[] existingTagNames, String[] newTagNames, 
			int tagId, String currentDate, int questionId, String questionTitle, String questionAgo, 
			String questionComment) throws SQLException {
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
			System.out.println("No user");
			return;
		}
		int userId = rs.getInt("id");

		for (String tagName : existingTagNames) {
			PreparedStatement tagGetByName = connect.prepareStatement(tagGetByNameSQL);
			tagGetByName.setString(1, tagName);
			ResultSet tag = tagGetByName.executeQuery();
			tag.next();
			tagId = tag.getInt("id");
			
			int tagCurrentPopular = rs.getInt("popular");
			PreparedStatement tagUpdatePopular = connect.prepareStatement(tagUpdatePopularSQL);
			tagUpdatePopular.setInt(1, tagCurrentPopular + 1);
			tagUpdatePopular.setInt(2, tagId);
			tagUpdatePopular.executeUpdate();
		}

		for (String tagName : newTagNames) {
			PreparedStatement tagAddTag = connect.prepareStatement(tagAddTagSQL);
			tagAddTag.setInt(1, tagId);
			tagAddTag.setString(2, tagName);
			tagAddTag.setString(3, "");
			tagAddTag.setInt(4, 0);
			tagAddTag.setString(5, currentDate);
			tagAddTag.setInt(6, userId);
			tagAddTag.executeUpdate();
		}

		PreparedStatement questionAddQuestion = connect.prepareStatement(questionAddQuestionSQL);
		questionAddQuestion.setInt(1, questionId);
		questionAddQuestion.setString(2, questionTitle);
		questionAddQuestion.setString(3, questionAgo);
		questionAddQuestion.setString(4, questionComment);
		questionAddQuestion.setInt(5, userId);
		questionAddQuestion.setString(6, currentDate);
		questionAddQuestion.setString(7, currentDate);
		questionAddQuestion.executeUpdate();

		// Should iterate through both arrays, however that would duplicate the operations
		for (String tagName : newTagNames) {
			PreparedStatement tagGetByName = connect.prepareStatement(tagGetByNameSQL);
			tagGetByName.setString(1, tagName);
			ResultSet tagRs2 = tagGetByName.executeQuery();
			if (!tagRs2.next()) {
				System.out.println("No tag was found");
				return;
			}
			int tag2Id = tagRs2.getInt("id");

			PreparedStatement questionTagAddQuestionTag = connect.prepareStatement(questionTagAddQuestionTagSQL);
			questionTagAddQuestionTag.setInt(1, questionId);
			questionTagAddQuestionTag.setInt(2, tag2Id);
			questionTagAddQuestionTag.executeUpdate();
		}
	}

	// TagController
	public void tagListAllTags() throws SQLException {
		String tagGetAllTagsSQL = 
				"SELECT * FROM " + "TAGS"+
				" WHERE 1 = 1";

		PreparedStatement tagGetAllTags = connect.prepareStatement(tagGetAllTagsSQL);
		ResultSet rs = tagGetAllTags.executeQuery();
		rs.next();
	}

	public void tagGetTag(int tagId) throws SQLException {
		String tagGetTagSQL = 
				"SELECT * FROM " + "TAGS"+
				" WHERE id = ?";

		PreparedStatement tagGetTag = connect.prepareStatement(tagGetTagSQL);
		tagGetTag.setInt(1, tagId);
		ResultSet rs = tagGetTag.executeQuery();
		rs.next();
	}

	public void tagGetTagsByTerm(String term) throws SQLException {
		String tagGetTagsByTermSQL = 
				"SELECT * FROM " + "TAGS"+
				" WHERE name = ?";

		PreparedStatement tagGetTagsByTerm = connect.prepareStatement(tagGetTagsByTermSQL);
		tagGetTagsByTerm.setString(1, term);
		ResultSet rs = tagGetTagsByTerm.executeQuery();
		rs.next();
	}

	// UserController
	public void userListAllUsers() throws SQLException {
		String userListAllUsersSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE 1 = 1";

		PreparedStatement userListAllUsers = connect.prepareStatement(userListAllUsersSQL);
		ResultSet rs = userListAllUsers.executeQuery();
		rs.next();
	}

	public void userGetUserByName(String username) throws SQLException {
		String userGetUserByNameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement userGetUserByName = connect.prepareStatement(userGetUserByNameSQL);
		userGetUserByName.setString(1, username);
		ResultSet rs = userGetUserByName.executeQuery();
		rs.next();
	}

	public void userGetUser(int userId) throws SQLException {
		String userGetUserByIdSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE id = ?";

		PreparedStatement userGetUserById = connect.prepareStatement(userGetUserByIdSQL);
		userGetUserById.setInt(1, userId);
		ResultSet rs = userGetUserById.executeQuery();
		rs.next();
	}

	public void userCreateUser(int userId, String username, String password,
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
		userAddUser.setInt(1, userId);
		userAddUser.setString(2, username);
		userAddUser.setString(3, password);
		userAddUser.setString(4, currentDate);
		userAddUser.setString(5, status);
		userAddUser.setInt(6, popular);
		userAddUser.executeUpdate();
	}

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
		rs.next();
		int userId = rs.getInt("id");
		String userPassword = rs.getString("password");

		if (!userPassword.equals(oldPassword)) {
			System.out.println("wrong old password");
			return;
		}

		PreparedStatement userUpdatePassword = connect.prepareStatement(userUpdatePasswordSQL);
		userUpdatePassword.setString(1, newPassword);
		userUpdatePassword.setInt(2, userId);
		userUpdatePassword.executeUpdate();
	}

	public void userUpdateUser(int userId, String username, String password) throws SQLException {
		String userGetByIdSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE id = ?";

		String userUpdateUsernamePasswordSQL = 
				"UPDATE " + "USERS" + 
				"   SET username = ?," +
				"       password = ?" +
				" WHERE id = ? ";

		PreparedStatement userGetById = connect.prepareStatement(userGetByIdSQL);
		userGetById.setInt(1, userId);
		ResultSet rs = userGetById.executeQuery();
		if (!rs.next()) {
			System.out.println("User not found");
			return;
		}

		PreparedStatement userUpdateUsernamePassword = connect.prepareStatement(userUpdateUsernamePasswordSQL);
		userUpdateUsernamePassword.setString(1, username);
		userUpdateUsernamePassword.setString(2, password);
		userUpdateUsernamePassword.setInt(3, userId);
		userUpdateUsernamePassword.executeUpdate();
	}

	public void userDeleteUser(int userId) throws SQLException {
		String userGetByIdSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE id = ?";

		String userDeleteSQL = 
				"DELETE FROM " + "USERS"+
				" WHERE id = ?";

		PreparedStatement userGetById = connect.prepareStatement(userGetByIdSQL);
		userGetById.setInt(1, userId);
		ResultSet rs = userGetById.executeQuery();
		if (!rs.next()) {
			System.out.println("User not found");
			return;
		}

		PreparedStatement userDelete = connect.prepareStatement(userDeleteSQL);
		userDelete.setInt(1, userId);
		userDelete.executeUpdate();
	}

	// VoteController
	public void voteCreateQuestion(String username, int questionId, int answerId, String mark,
			int voteId, String currentDate) throws SQLException {
		String userGetByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String questionGetByIdSQL = 
				"SELECT * FROM " + "QUESTIONS"+
				" WHERE id = ?";

		String userGetByIdSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE id = ?";

		String userUsernameGetByIdSQL = 
				"SELECT username FROM " + "USERS"+
				" WHERE id = ?";

		String questionUserIdGetByIdSQL = 
				"SELECT userId FROM " + "QUESTIONS"+
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
		rs.next();
		int userId = rs.getInt("id");
		String userUsername = rs.getString("username");
		int userPopular = rs.getInt("popular");

		int newPopular = userPopular;
		PreparedStatement questionGetById = connect.prepareStatement(questionGetByIdSQL);
		questionGetById.setInt(1, questionId);
		ResultSet question = questionGetById.executeQuery();
		int questionUserId = 0;
		int sawQuestion = 0;
		if (question.next()) {
			sawQuestion = 1;
			questionUserId = question.getInt("userId");
		}

		PreparedStatement answerGetById = connect.prepareStatement(answerGetByIdSQL);
		answerGetById.setInt(1, answerId);
		ResultSet answer = answerGetById.executeQuery();
		int answerQuestionId = 0;
		int sawAnswer = 0;
		if (answer.next() && sawQuestion == 0) {
			sawAnswer = 1;
			answerQuestionId = answer.getInt("questionId");
		}

		if (sawQuestion == 1) {
			PreparedStatement userUsernameGetById = connect.prepareStatement(userUsernameGetByIdSQL);
			userUsernameGetById.setInt(1, questionUserId);
			ResultSet questionUser = userUsernameGetById.executeQuery();
			questionUser.next();
			String questionUsername = questionUser.getString("username");

			if (questionUsername.equals(userUsername)) {
				userId = questionUserId;
			}

			if (mark.equals("DOWN")) newPopular -= 2;
			else newPopular += 5;

		} else if (sawAnswer == 1 && sawQuestion == 0) {
			PreparedStatement questionUserIdGetById = connect.prepareStatement(questionUserIdGetByIdSQL);
			questionUserIdGetById.setInt(1, answerQuestionId);
			question = questionUserIdGetById.executeQuery();
			question.next();
			questionUserId = question.getInt("userId");

			PreparedStatement userUsernameGetById = connect.prepareStatement(userUsernameGetByIdSQL);
			userUsernameGetById.setInt(1, questionUserId);
			ResultSet questionUser = userUsernameGetById.executeQuery();
			questionUser.next();
			String questionUsername = questionUser.getString("username");

			if (questionUsername.equals(userUsername)) {
				userId = questionUserId;
			}

			if (mark.equals("DOWN")) newPopular -= 2;
			else newPopular += 10;
		}

		if (sawQuestion == 1 || sawAnswer == 1) {
			PreparedStatement userUpdatePopular = connect.prepareStatement(userUpdatePopularSQL);
			userUpdatePopular.setInt(1, newPopular);
			userUpdatePopular.setInt(2, userId);
			userUpdatePopular.executeUpdate();
		}

		PreparedStatement voteAddVote = connect.prepareStatement(voteAddVoteSQL);
		voteAddVote.setInt(1, voteId);
		voteAddVote.setString(2, "");
		voteAddVote.setInt(3, questionId);
		voteAddVote.setInt(4, answerId);
		voteAddVote.setInt(5, userId);
		voteAddVote.setString(6, mark);
		voteAddVote.executeUpdate();
	}

	// AjaxController
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
			rs.next();
		} else if (!username.equals("")) {
			PreparedStatement userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
			userGetByUsername.setString(1, username);
			ResultSet rs = userGetByUsername.executeQuery();
			rs.next();
		} else if (!email.equals("")) {
			PreparedStatement userGetByEmail = connect.prepareStatement(userGetByEmailSQL);
			userGetByEmail.setString(1, email);
			ResultSet rs = userGetByEmail.executeQuery();
			rs.next();
		}
	}
}
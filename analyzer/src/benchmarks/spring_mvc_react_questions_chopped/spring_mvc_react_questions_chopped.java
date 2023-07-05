package benchmarks.spring_mvc_react_questions_chopped;

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

public class spring_mvc_react_questions_chopped {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public spring_mvc_react_questions_chopped(int id) {
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

	// VoteController
	@ChoppedTransaction(originalTransaction="voteCreateQuestion1", microservice="m2")
	public void voteCreateQuestion11(String username) throws SQLException {
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
		int userPopular = rs.getInt("popular");
	}

	@ChoppedTransaction(originalTransaction="voteCreateQuestion2", microservice="m2")
	public void voteCreateQuestion21(String username) throws SQLException {
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
		int userPopular = rs.getInt("popular");
	}

	@ChoppedTransaction(originalTransaction="voteCreateQuestion1", microservice="m3")
	public void voteCreateQuestion12(long questionId) throws SQLException {
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
	
	@ChoppedTransaction(originalTransaction="voteCreateQuestion1", microservice="m2")
	public void voteCreateQuestion13(String username) throws SQLException {
		String userGetByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
		userGetByUsername.setString(1, username);
		ResultSet user = userGetByUsername.executeQuery();
		if (!user.next()) {
			System.out.println("Empty");
		}
		long userId = user.getLong("id");
	}

	@ChoppedTransaction(originalTransaction="voteCreateQuestion1", microservice="m3")
	public void voteCreateQuestion14(long questionId) throws SQLException {
		String questionGetByIdSQL = 
				"SELECT * FROM " + "QUESTIONS"+
				" WHERE id = ?";

		PreparedStatement questionGetById = connect.prepareStatement(questionGetByIdSQL);
		questionGetById.setLong(1, questionId);
		ResultSet question = questionGetById.executeQuery();
		if (!question.next()) {
			System.out.println("Empty");
		}
		long questionUserId = question.getLong("userId");
	}

	@ChoppedTransaction(originalTransaction="voteCreateQuestion1", microservice="m2")
	public void voteCreateQuestion15(long questionUserId, String username, long userId, 
			String mark, int newPopular, int sawQuestion) throws SQLException {
		String userGetByIdSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE id = ?";

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

	@ChoppedTransaction(originalTransaction="voteCreateQuestion2", microservice="m1")
	public void voteCreateQuestion22(long answerId) throws SQLException {
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

	@ChoppedTransaction(originalTransaction="voteCreateQuestion2", microservice="m2")
	public void voteCreateQuestion23(String username) throws SQLException {
		String userGetByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement userGetByUsername = connect.prepareStatement(userGetByUsernameSQL);
		userGetByUsername.setString(1, username);
		ResultSet user = userGetByUsername.executeQuery();
		if (!user.next()) {
			System.out.println("Empty");
		}
		long userId = user.getLong("id");
	}

	@ChoppedTransaction(originalTransaction="voteCreateQuestion2", microservice="m1")
	public void voteCreateQuestion24(long questionId) throws SQLException {
		String answerGetByIdSQL = 
				"SELECT * FROM " + "ANSWERS"+
				" WHERE id = ?";

		PreparedStatement answerGetById = connect.prepareStatement(answerGetByIdSQL);
		answerGetById.setLong(1, questionId);
		ResultSet answer = answerGetById.executeQuery();
		if (!answer.next()) {
			System.out.println("Empty");
		}
		long answerQuestionId = answer.getLong("questionId");
	}

	@ChoppedTransaction(originalTransaction="voteCreateQuestion2", microservice="m3")
	public void voteCreateQuestion25(long answerQuestionId) throws SQLException {
		String questionGetByIdSQL = 
				"SELECT * FROM " + "QUESTIONS"+
				" WHERE id = ?";

		PreparedStatement questionGetById = connect.prepareStatement(questionGetByIdSQL);
		questionGetById.setLong(1, answerQuestionId);
		ResultSet question = questionGetById.executeQuery();
		if (!question.next()) {
			System.out.println("Empty");
		}
		long questionUserId = question.getLong("userId");
	}

	@ChoppedTransaction(originalTransaction="voteCreateQuestion2", microservice="m2")
	public void voteCreateQuestion26(long questionUserId, String username, long userId,
		String mark, int newPopular, int sawAnswer) throws SQLException {
		String userGetByIdSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE id = ?";

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

	@ChoppedTransaction(originalTransaction="voteCreateQuestion1", microservice="m2")
	public void voteCreateQuestion16(int sawQuestion, int newPopular, long userId) throws SQLException {
		String userUpdatePopularSQL = 
				"UPDATE " + "USERS" + 
				"   SET popular = ?" +
				" WHERE id = ? ";

		if (sawQuestion == 1) {
			PreparedStatement userUpdatePopular = connect.prepareStatement(userUpdatePopularSQL);
			userUpdatePopular.setInt(1, newPopular);
			userUpdatePopular.setLong(2, userId);
			userUpdatePopular.executeUpdate();
		}
	}

	@ChoppedTransaction(originalTransaction="voteCreateQuestion2", microservice="m2")
	public void voteCreateQuestion27(int sawAnswer, int newPopular, long userId) throws SQLException {
		String userUpdatePopularSQL = 
				"UPDATE " + "USERS" + 
				"   SET popular = ?" +
				" WHERE id = ? ";

		if (sawAnswer == 1) {
			PreparedStatement userUpdatePopular = connect.prepareStatement(userUpdatePopularSQL);
			userUpdatePopular.setInt(1, newPopular);
			userUpdatePopular.setLong(2, userId);
			userUpdatePopular.executeUpdate();
		}
	}

	@ChoppedTransaction(originalTransaction="voteCreateQuestion1", microservice="m5")
	public void voteCreateQuestion17(long voteId, long questionId, long answerId, long userId,
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

	@ChoppedTransaction(originalTransaction="voteCreateQuestion2", microservice="m5")
	public void voteCreateQuestion28(long voteId, long questionId, long answerId, long userId,
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
}
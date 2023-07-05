package benchmarks.spring_mvc_react_questions;

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

public class spring_mvc_react_questions {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public spring_mvc_react_questions(int id) {
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

	// QuestionController
	@ChoppedTransaction(microservice="m1")
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

	@ChoppedTransaction(microservice="m1")
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
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long tagId = rs.getLong("id");

		PreparedStatement questionTagGetByTag = connect.prepareStatement(questionTagGetByTagSQL);
		questionTagGetByTag.setLong(1, tagId);
		rs = questionTagGetByTag.executeQuery();
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
}
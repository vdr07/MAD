package benchmarks.spring_mvc_react_answers;

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

public class spring_mvc_react_answers {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public spring_mvc_react_answers(int id) {
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
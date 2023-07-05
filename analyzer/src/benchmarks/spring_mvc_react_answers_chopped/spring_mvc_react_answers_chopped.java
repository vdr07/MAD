package benchmarks.spring_mvc_react_answers_chopped;

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

public class spring_mvc_react_answers_chopped {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public spring_mvc_react_answers_chopped(int id) {
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
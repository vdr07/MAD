package benchmarks.spring_mvc_react_tags_chopped;

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

public class spring_mvc_react_tags_chopped {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public spring_mvc_react_tags_chopped(int id) {
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
}
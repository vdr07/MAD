package benchmarks.java_spring_mvc_blog_comment;

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

public class java_spring_mvc_blog_comment {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public java_spring_mvc_blog_comment(int id) {
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

	// CommentController
	@ChoppedTransaction(microservice="m1")
	public void showComments(long postId) throws SQLException {
		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";
		
		String getTopLevelCommentsSQL = 
				"SELECT * FROM " + "COMMENTS"+
				" WHERE postId = ? AND pCommentId = 0";

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setLong(1, postId);
		ResultSet rs = getPost.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		PreparedStatement getTopLevelComments = connect.prepareStatement(getTopLevelCommentsSQL);
		getTopLevelComments.setLong(1, postId);
		rs = getTopLevelComments.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void addComment(long postId, int isAdmin, long commentId, String commentText,
			long currentDate, long currentUserId, long parentId) throws SQLException {
		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";
		
		String saveNewCommentSQL = 
				"INSERT INTO " + "COMMENTS" +
				" (id, commentText, originalDateTime, modifiedDateTime, userId, postId, deleted, pCommentId) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";

		String insertIntoChildrenCommentsSQL = 
				"INSERT INTO " + "CHILDREN_COMMENT" +
				" (pId, cId) " +
				" VALUES ( ?, ? )";

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setLong(1, postId);
		ResultSet rs = getPost.executeQuery();
		if (!rs.next()) {
			System.out.println("post not found");
			return;
		}

		if (rs.getInt("hide") == 1 && isAdmin != 1) {
			System.out.println("post not found");
			return;
		}

		PreparedStatement saveNewComment = connect.prepareStatement(saveNewCommentSQL);
		saveNewComment.setLong(1, commentId);
		saveNewComment.setString(2, commentText);
		saveNewComment.setLong(3, currentDate);
		saveNewComment.setLong(4, 0);
		saveNewComment.setLong(5, currentUserId);
		saveNewComment.setLong(6, postId);
		saveNewComment.setLong(7, 0);
		saveNewComment.setLong(8, parentId);
		saveNewComment.executeUpdate();
		
		// TODOOOOOOOOOOOOOOOOOO check their code

		if (parentId != 0) {
			PreparedStatement insertIntoChildrenComments = connect.prepareStatement(insertIntoChildrenCommentsSQL);
			insertIntoChildrenComments.setLong(1, parentId);
			insertIntoChildrenComments.setLong(2, commentId);
			insertIntoChildrenComments.executeUpdate();
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void deleteComment(long commentId, int isAdmin, String authName,
			long currentTime, long maxDeleteTime) throws SQLException {
		String getCommentSQL = 
				"SELECT userId FROM " + "COMMENTS"+
				" WHERE id = ?";

		String getUserByUsernameSQL = 
				"SELECT username FROM " + "USERS"+
				" WHERE username = ?";

		String getUserSQL = 
				"SELECT username FROM " + "USERS"+
				" WHERE id = ?";

		String deleteCommentSQL = 
				"UPDATE " + "COMMENTS" + 
				"   SET deleted = ?" +
				" WHERE id = ? ";

		PreparedStatement getComment = connect.prepareStatement(getCommentSQL);
		getComment.setLong(1, commentId);
		ResultSet rs = getComment.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		long userId = rs.getLong("userId");


		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		String currentUsername = rs.getString("username");

		PreparedStatement getUser = connect.prepareStatement(getUserSQL);
		getUser.setLong(1, userId);
		rs = getUser.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		String username = rs.getString("username");

		if (isAdmin != 1 && username != currentUsername) {
			System.out.println("forbidden");
			return;
		}

		if (isAdmin != 1 && currentTime >= maxDeleteTime) {
			System.out.println("delete time exceeded");
			return;
		}

		PreparedStatement deleteComment = connect.prepareStatement(deleteCommentSQL);
		deleteComment.setInt(1, 1);
		deleteComment.setLong(2, commentId);
		deleteComment.executeUpdate();
	}

	@ChoppedTransaction(microservice="m1")
	public void editComment(long commentId, int isAdmin, String authName,
			long currentTime, long maxEditTime, String newCommentText) throws SQLException {
		String getCommentSQL = 
				"SELECT userId FROM " + "COMMENTS"+
				" WHERE id = ?";

		String getUserByUsernameSQL = 
				"SELECT username FROM " + "USERS"+
				" WHERE username = ?";

		String getUserSQL = 
				"SELECT username FROM " + "USERS"+
				" WHERE id = ?";

		String updateCommentSQL = 
				"UPDATE " + "COMMENTS" + 
				"   SET commentText = ?," +
				"       modifiedDateTime = ? " +
				" WHERE id = ? ";

		PreparedStatement getComment = connect.prepareStatement(getCommentSQL);
		getComment.setLong(1, commentId);
		ResultSet rs = getComment.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		long userId = rs.getLong("userId");

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		String currentUsername = rs.getString("username");

		PreparedStatement getUser = connect.prepareStatement(getUserSQL);
		getUser.setLong(1, userId);
		rs = getUser.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		String username = rs.getString("username");

		if (isAdmin != 1 && username != currentUsername) {
			System.out.println("forbidden");
			return;
		}

		if (isAdmin != 1 && currentTime >= maxEditTime) {
			System.out.println("edit time exceeded");
			return;
		}

		PreparedStatement updateComment = connect.prepareStatement(updateCommentSQL);
		updateComment.setString(1, newCommentText);
		updateComment.setLong(2, currentTime);
		updateComment.setLong(3, commentId);
		updateComment.executeUpdate();
	}

	@ChoppedTransaction(microservice="m1")
	public void getCommentSource(long commentId) throws SQLException {
		String getCommentSQL = 
				"SELECT commentText FROM " + "COMMENTS"+
				" WHERE id = ?";

		PreparedStatement getComment = connect.prepareStatement(getCommentSQL);
		getComment.setLong(1, commentId);
		ResultSet rs = getComment.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		String commentText = rs.getString("commentText");
	}

	//merged
	@ChoppedTransaction(microservice="m1")
	public void commentVote(long commentId, int isAdmin, String authName, long ratingId,
			int like) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT id FROM " + "USERS"+
				" WHERE username = ?";

		String getCommentSQL = 
				"SELECT userId FROM " + "COMMENTS"+
				" WHERE id = ?";

		String getCommentRatingSQL = 
				"SELECT id FROM " + "COMMENT_RATING"+
				" WHERE userId = ? AND commentId = ?";

		String insertCommentRatingSQL = 
				"INSERT INTO " + "COMMENT_RATING" +
				" (id, userId, rate, commentId) " +
				" VALUES ( ?, ?, ?, ? )";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		long currentUserId = rs.getLong("id");

		PreparedStatement getComment = connect.prepareStatement(getCommentSQL);
		getComment.setLong(1, commentId);
		rs = getComment.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		long commentUserId = rs.getLong("userId");

		if (currentUserId == commentUserId) {
			System.out.println("cannot vote own comment");
			return;
		}

		PreparedStatement getCommentRating = connect.prepareStatement(getCommentRatingSQL);
		getCommentRating.setLong(1, currentUserId);
		getCommentRating.setLong(2, commentId);
		rs = getCommentRating.executeQuery();
		if (rs.next()) {
			System.out.println("already voted");
			return;
		}

		int rate;
		if (like == 1) rate = 1;
		else rate = -1;

		PreparedStatement insertCommentRating = connect.prepareStatement(insertCommentRatingSQL);
		insertCommentRating.setLong(1, ratingId);
		insertCommentRating.setLong(2, currentUserId);
		insertCommentRating.setLong(3, rate);
		insertCommentRating.setLong(4, commentId);
		insertCommentRating.executeUpdate();
	}
}
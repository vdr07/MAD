package benchmarks.java_spring_mvc_blog_comment_chopped;

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

public class java_spring_mvc_blog_comment_chopped {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public java_spring_mvc_blog_comment_chopped(int id) {
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
	@ChoppedTransaction(originalTransaction="showComments", microservice="m1")
	public void showComments1(long postId) throws SQLException {
		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setLong(1, postId);
		ResultSet rs = getPost.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(originalTransaction="showComments", microservice="m2")
	public void showComments2(long postId) throws SQLException {
		String getTopLevelCommentsSQL = 
				"SELECT * FROM " + "COMMENTS"+
				" WHERE postId = ? AND pCommentId = 0";
		
		PreparedStatement getTopLevelComments = connect.prepareStatement(getTopLevelCommentsSQL);
		getTopLevelComments.setLong(1, postId);
		ResultSet rs = getTopLevelComments.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(originalTransaction="addComment", microservice="m1")
	public void addComment1(long postId, int isAdmin, long commentId, String commentText,
			long currentDate, long currentUserId, long parentId) throws SQLException {
		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";

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
	}

	@ChoppedTransaction(originalTransaction="addComment", microservice="m2")
	public void addComment2(long postId, long commentId, String commentText,
			long currentDate, long currentUserId, long parentId) throws SQLException {
		String saveNewCommentSQL = 
				"INSERT INTO " + "COMMENTS" +
				" (id, commentText, originalDateTime, modifiedDateTime, userId, postId, deleted, pCommentId) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";

		String insertIntoChildrenCommentsSQL = 
				"INSERT INTO " + "CHILDREN_COMMENT" +
				" (pId, cId) " +
				" VALUES ( ?, ? )";

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
		
		// TODOOOOO CHECK THEIR CODE
		if (parentId != 0) {
			PreparedStatement insertIntoChildrenComments = connect.prepareStatement(insertIntoChildrenCommentsSQL);
			insertIntoChildrenComments.setLong(1, parentId);
			insertIntoChildrenComments.setLong(2, commentId);
			insertIntoChildrenComments.executeUpdate();
		}
	}

	@ChoppedTransaction(originalTransaction="deleteComment", microservice="m2")
	public void deleteComment1(long commentId) throws SQLException {
		String getCommentSQL = 
				"SELECT userId FROM " + "COMMENTS"+
				" WHERE id = ?";

		PreparedStatement getComment = connect.prepareStatement(getCommentSQL);
		getComment.setLong(1, commentId);
		ResultSet rs = getComment.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		long userId = rs.getLong("userId");
	}

	@ChoppedTransaction(originalTransaction="deleteComment", microservice="m3")
	public void deleteComment2(long commentId, long userId, int isAdmin, String authName,
			long currentTime, long maxDeleteTime) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT username FROM " + "USERS"+
				" WHERE username = ?";

		String getUserSQL = 
				"SELECT username FROM " + "USERS"+
				" WHERE id = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
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
	}

	@ChoppedTransaction(originalTransaction="deleteComment", microservice="m2")
	public void deleteComment3(long commentId) throws SQLException {
		String deleteCommentSQL = 
				"UPDATE " + "COMMENTS" + 
				"   SET deleted = ?" +
				" WHERE id = ? ";
		PreparedStatement deleteComment = connect.prepareStatement(deleteCommentSQL);
		deleteComment.setInt(1, 1);
		deleteComment.setLong(2, commentId);
		deleteComment.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="editComment", microservice="m2")
	public void editComment1(long commentId) throws SQLException {
		String getCommentSQL = 
				"SELECT userId FROM " + "COMMENTS"+
				" WHERE id = ?";

		PreparedStatement getComment = connect.prepareStatement(getCommentSQL);
		getComment.setLong(1, commentId);
		ResultSet rs = getComment.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		long userId = rs.getLong("userId");
	}

	@ChoppedTransaction(originalTransaction="editComment", microservice="m3")
	public void editComment2(long userId, int isAdmin, String authName,
			long currentTime, long maxEditTime) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT username FROM " + "USERS"+
				" WHERE username = ?";

		String getUserSQL = 
				"SELECT username FROM " + "USERS"+
				" WHERE id = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
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
	}

	@ChoppedTransaction(originalTransaction="editComment", microservice="m2")
	public void editComment3(long commentId, long currentTime, String newCommentText) throws SQLException {
		String updateCommentSQL = 
				"UPDATE " + "COMMENTS" + 
				"   SET commentText = ?," +
				"       modifiedDateTime = ? " +
				" WHERE id = ? ";

		PreparedStatement updateComment = connect.prepareStatement(updateCommentSQL);
		updateComment.setString(1, newCommentText);
		updateComment.setLong(2, currentTime);
		updateComment.setLong(3, commentId);
		updateComment.executeUpdate();
	}

	@ChoppedTransaction(microservice="m2")
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
	@ChoppedTransaction(originalTransaction="commentVote", microservice="m3")
	public void commentVote1(String authName) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT id FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		long currentUserId = rs.getLong("id");
	}

	@ChoppedTransaction(originalTransaction="commentVote", microservice="m2")
	public void commentVote2(long commentId, long currentUserId) throws SQLException {
		String getCommentSQL = 
				"SELECT userId FROM " + "COMMENTS"+
				" WHERE id = ?";

		PreparedStatement getComment = connect.prepareStatement(getCommentSQL);
		getComment.setLong(1, commentId);
		ResultSet rs = getComment.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		long commentUserId = rs.getLong("userId");

		if (currentUserId == commentUserId) {
			System.out.println("cannot vote own comment");
			return;
		}
	}

	@ChoppedTransaction(originalTransaction="commentVote", microservice="m4")
	public void commentVote3(long commentId, long currentUserId, long ratingId,
			int like) throws SQLException {
		String getCommentRatingSQL = 
				"SELECT id FROM " + "COMMENT_RATING"+
				" WHERE userId = ? AND commentId = ?";

		String insertCommentRatingSQL = 
				"INSERT INTO " + "COMMENT_RATING" +
				" (id, userId, rate, commentId) " +
				" VALUES ( ?, ?, ?, ? )";
		PreparedStatement getCommentRating = connect.prepareStatement(getCommentRatingSQL);
		getCommentRating.setLong(1, currentUserId);
		getCommentRating.setLong(2, commentId);
		ResultSet rs = getCommentRating.executeQuery();
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
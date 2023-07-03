package benchmarks.java_spring_mvc_blog_posts;

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

public class java_spring_mvc_blog_posts {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public java_spring_mvc_blog_posts(int id) {
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

	// PostsController
	@ChoppedTransaction(microservice="m1")
	public void showPostsList(int isAdmin, String authName) throws SQLException {
		String getAllPostsSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE 1 = 1";

		String getPublicPostsSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE hide = 0";

		String getUserByUsernameSQL = 
				"SELECT id FROM " + "USERS"+
				" WHERE username = ?";

		ResultSet rs;
		if (isAdmin == 1) {
			PreparedStatement getAllPosts = connect.prepareStatement(getAllPostsSQL);
			rs = getAllPosts.executeQuery();
			if (!rs.next()) {
				System.out.println("no posts");
			}
		} else {
			PreparedStatement getPublicPosts = connect.prepareStatement(getPublicPostsSQL);
			rs = getPublicPosts.executeQuery();
			if (!rs.next()) {
				System.out.println("no posts");
			}
		}

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("no current user");
			return;
		}
		long currentUserId = rs.getLong("id");
	}

	@ChoppedTransaction(microservice="m1")
	public void getPostsList() throws SQLException {
		String getPublicPostsSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE hide = 0";

		PreparedStatement getPublicPosts = connect.prepareStatement(getPublicPostsSQL);
		ResultSet rs = getPublicPosts.executeQuery();
		if (!rs.next()) {
			System.out.println("no posts");
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void getTopPostsList() throws SQLException {
		String getPublicPostsSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE hide = 0";

		String getPostRatingSQL = 
				"SELECT * FROM " + "POST_RATING"+
				" WHERE postId = ?";

		PreparedStatement getPublicPosts = connect.prepareStatement(getPublicPostsSQL);
		ResultSet rs = getPublicPosts.executeQuery();
		while (rs.next()) {
			long postId = rs.getLong("id");
			PreparedStatement getPostRating = connect.prepareStatement(getPostRatingSQL);
			getPostRating.setLong(1, postId);
			ResultSet postRating = getPostRating.executeQuery();
			if (!postRating.next()) {
				System.out.println("no rating");
			}
			int rating = postRating.getInt("rate");
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void searchByTag(int isAdmin, String authName, String[] tags) throws SQLException {
		String getTagSQL = 
				"SELECT id FROM " + "TAGS"+
				" WHERE tname = ?";

		String getPostByTagSQL = 
				"SELECT postId FROM " + "POST_TAG"+
				" WHERE tagId = ?";

		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";
		
		String getPostNotHiddenSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ? AND hide = 0";

		String getUserByUsernameSQL = 
				"SELECT id FROM " + "USERS"+
				" WHERE username = ?";

		ResultSet rs;
		for (String tagName : tags) {
			PreparedStatement getTag = connect.prepareStatement(getTagSQL);
			getTag.setString(1, tagName);
			rs = getTag.executeQuery();
			if (!rs.next()) {
				System.out.println("empty");
			}
			long tagId = rs.getLong("id");

			PreparedStatement getPostByTag = connect.prepareStatement(getPostByTagSQL);
			getPostByTag.setLong(1, tagId);
			rs = getPostByTag.executeQuery();
			while (rs.next()) {
				long postId = rs.getLong("postId");
				if (isAdmin == 1) {
					PreparedStatement getPost = connect.prepareStatement(getPostSQL);
					getPost.setLong(1, postId);
					ResultSet post = getPost.executeQuery();
					if (!post.next()) {
						System.out.println("empty");
					}
				} else {
					PreparedStatement getPostNotHidden = connect.prepareStatement(getPostNotHiddenSQL);
					getPostNotHidden.setLong(1, postId);
					ResultSet post = getPostNotHidden.executeQuery();
					if (!post.next()) {
						System.out.println("empty");
					}
				}
			}
		}

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("no current user");
			return;
		}
		long currentUserId = rs.getLong("id");
	}

	@ChoppedTransaction(microservice="m1")
	public void showPost(long postId, int isAdmin, String authName) throws SQLException {
		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";
		
		String getUserByUsernameSQL = 
				"SELECT id FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setLong(1, postId);
		ResultSet rs = getPost.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}

		if (rs.getInt("hide") == 1 && isAdmin != 1) {
			System.out.println("error");
			return;
		}

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("no current user");
			return;
		}
		long currentUserId = rs.getLong("id");
	}

	@ChoppedTransaction(microservice="m1")
	public void createPost(long postId, String title, String fullPostText, int cutInd,
			long currentTime, String[] tags) throws SQLException {
		String insertPostSQL = 
				"INSERT INTO " + "POSTS" +
				" (id, title, shortTextPart, fullPostText, originalDateTime, hide) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";
		
		String getTagSQL = 
				"SELECT * FROM " + "TAGS"+
				" WHERE tname = ?";

		String insertPostTagSQL = 
				"INSERT INTO " + "POST_TAG" +
				" (postId, tagId) " +
				" VALUES ( ?, ? )";

		String shortPostPart;
		if (cutInd > 0) shortPostPart = fullPostText.substring(0, cutInd);
		else shortPostPart = "";

		PreparedStatement insertPost = connect.prepareStatement(insertPostSQL);
		insertPost.setLong(1, postId);
		insertPost.setString(2, title);
		insertPost.setString(3, shortPostPart);
		insertPost.setString(4, fullPostText);
		insertPost.setLong(5, currentTime);
		insertPost.setInt(6, 0);
		insertPost.executeUpdate();

		for (String tagName : tags) {
			PreparedStatement getTag = connect.prepareStatement(getTagSQL);
			getTag.setString(1, tagName);
			ResultSet rs = getTag.executeQuery();
			if (!rs.next()) {
				System.out.println("empty");
			}
			long tagId = rs.getLong("id");

			PreparedStatement insertPostTag = connect.prepareStatement(insertPostTagSQL);
			insertPostTag.setLong(1, postId);
			insertPostTag.setLong(2, tagId);
			insertPostTag.executeUpdate();
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void showEditPostForm(long postId) throws SQLException {
		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";
		
		String getPostTagsSQL = 
				"SELECT * FROM " + "POST_TAG"+
				" WHERE postId = ?";

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setLong(1, postId);
		ResultSet rs = getPost.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
		long editPostId = rs.getLong("id");
		String text = rs.getString("fullPostText");
		String title = rs.getString("title");
		
		PreparedStatement getPostTags = connect.prepareStatement(getPostTagsSQL);
		getPostTags.setLong(1, postId);
		rs = getPostTags.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void updatePost(long postId, String title, String fullPostText, int cutInd,
			String[] newTags) throws SQLException {
		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";

		String deletePostTagsSQL = 
				"DELETE FROM " + "POST_TAG"+
				" WHERE postId = ?";
		
		String updatePostSQL = 
				"UPDATE " + "POSTS" +
				"   SET title = ?," +
				"       shortTextPart = ?," +
				"       fullPostText = ?" +
				" WHERE id = ?";

		String getTagSQL = 
				"SELECT * FROM " + "TAGS"+
				" WHERE tname = ?";

		String insertPostTagSQL = 
				"INSERT INTO " + "POST_TAG" +
				" (postId, tagId) " +
				" VALUES ( ?, ? )";

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setLong(1, postId);
		ResultSet rs = getPost.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}

		PreparedStatement deletePostTags = connect.prepareStatement(deletePostTagsSQL);
		deletePostTags.setLong(1, postId);
		deletePostTags.executeUpdate();

		String shortPostPart;
		if (cutInd > 0) shortPostPart = fullPostText.substring(0, cutInd);
		else shortPostPart = "";

		PreparedStatement updatePost = connect.prepareStatement(updatePostSQL);
		updatePost.setString(1, title);
		updatePost.setString(2, shortPostPart);
		updatePost.setString(3, fullPostText);
		updatePost.setLong(4, postId);
		updatePost.executeUpdate();

		for (String tagName : newTags) {
			PreparedStatement getTag = connect.prepareStatement(getTagSQL);
			getTag.setString(1, tagName);
			rs = getTag.executeQuery();
			if (!rs.next()) {
				System.out.println("empty");
			}
			long tagId = rs.getLong("id");

			PreparedStatement insertPostTag = connect.prepareStatement(insertPostTagSQL);
			insertPostTag.setLong(1, postId);
			insertPostTag.setLong(2, tagId);
			insertPostTag.executeUpdate();
		}
	}

	// merged
	@ChoppedTransaction(microservice="m1")
	public void setPostVisibility(long postId, int hide) throws SQLException {
		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";
		
		String setPostVisibilitySQL = 
				"UPDATE " + "POSTS" + 
				"   SET hide = ?" +
				" WHERE id = ? ";

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setLong(1, postId);
		ResultSet rs = getPost.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
		
		PreparedStatement setPostVisibility = connect.prepareStatement(setPostVisibilitySQL);
		if (hide == 1) setPostVisibility.setInt(1, 1);
		else setPostVisibility.setInt(1, 0);
		setPostVisibility.setLong(2, postId);
		setPostVisibility.executeUpdate();
	}

	@ChoppedTransaction(microservice="m1")
	public void deletePost(long postId) throws SQLException {
		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";

		String deletePostSQL = 
				"DELETE FROM " + "POSTS"+
				" WHERE id = ?";
		
		String deletePostTagsSQL = 
				"DELETE FROM " + "POST_TAG"+
				" WHERE postId = ?";

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setLong(1, postId);
		ResultSet rs = getPost.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}

		PreparedStatement deletePost = connect.prepareStatement(deletePostSQL);
		deletePost.setLong(1, postId);
		deletePost.executeUpdate();
		
		PreparedStatement deletePostTags = connect.prepareStatement(deletePostTagsSQL);
		deletePostTags.setLong(1, postId);
		deletePostTags.executeUpdate();
	}

	//merged
	@ChoppedTransaction(microservice="m1")
	public void postVote(long postId, int isAdmin, String authName, long ratingId,
			int like) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT id FROM " + "USERS"+
				" WHERE username = ?";

		String getPostSQL = 
				"SELECT id FROM " + "POSTS"+
				" WHERE id = ?";

		String getPostRatingSQL = 
				"SELECT id FROM " + "POST_RATING"+
				" WHERE userId = ? AND postId = ?";

		String insertPostRatingSQL = 
				"INSERT INTO " + "POST_RATING" +
				" (id, userId, rate, postId) " +
				" VALUES ( ?, ?, ?, ? )";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		long currentUserId = rs.getLong("id");

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setLong(1, postId);
		rs = getPost.executeQuery();
		if (!rs.next()) {
			System.out.println("post not found");
			return;
		}

		PreparedStatement getPostRating = connect.prepareStatement(getPostRatingSQL);
		getPostRating.setLong(1, currentUserId);
		getPostRating.setLong(2, postId);
		rs = getPostRating.executeQuery();
		if (rs.next()) {
			System.out.println("already voted");
			return;
		}

		int rate;
		if (like == 1) rate = 1;
		else rate = -1;

		PreparedStatement insertPostRating = connect.prepareStatement(insertPostRatingSQL);
		insertPostRating.setLong(1, ratingId);
		insertPostRating.setLong(2, currentUserId);
		insertPostRating.setLong(3, rate);
		insertPostRating.setLong(4, postId);
		insertPostRating.executeUpdate();
	}
}
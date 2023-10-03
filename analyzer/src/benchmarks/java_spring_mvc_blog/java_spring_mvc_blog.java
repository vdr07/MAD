package benchmarks.java_spring_mvc_blog;

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

public class java_spring_mvc_blog {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public java_spring_mvc_blog(int id) {
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
	public void showComments(int postId) throws SQLException {
		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";
		
		String getTopLevelCommentsSQL = 
				"SELECT * FROM " + "COMMENTS"+
				" WHERE postId = ? AND pCommentId = 0";

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setInt(1, postId);
		ResultSet rs = getPost.executeQuery();
		rs.next();

		PreparedStatement getTopLevelComments = connect.prepareStatement(getTopLevelCommentsSQL);
		getTopLevelComments.setInt(1, postId);
		ResultSet topLevelComments = getTopLevelComments.executeQuery();
		topLevelComments.next();
	}

	public void addComment(int postId, int commentId, String commentText,
			int currentDate, String authName, int parentId) throws SQLException {
		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";

		String getCurrentUserSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";
		
		String getUserRolesSQL = 
				"SELECT * FROM " + "USER_ROLE"+
				" WHERE userId = ?";

		String getRoleNameSQL = 
				"SELECT rname FROM " + "ROLES"+
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
		getPost.setInt(1, postId);
		ResultSet rs = getPost.executeQuery();
		if (!rs.next()) {
			System.out.println("post not found");
			return;
		}

		// isAdmin
		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setString(1, authName);
		ResultSet currentUser = getCurrentUser.executeQuery();
		if (!currentUser.next()) {
			System.out.println("user not found");
			return;
		}
		int currentUserId = currentUser.getInt("id");

		PreparedStatement getUserRoles = connect.prepareStatement(getUserRolesSQL);
		getUserRoles.setInt(1, currentUserId);
		ResultSet userRoles = getUserRoles.executeQuery();
		int isAdmin = 0;
		while (userRoles.next()) {
			int roleId = userRoles.getInt("roleId");

			PreparedStatement getRoleName = connect.prepareStatement(getRoleNameSQL);
			getRoleName.setInt(1, roleId);
			ResultSet roleName = getRoleName.executeQuery();
			if (!roleName.next()) {
				System.out.println("role not found");
				return;
			}
			String rName = roleName.getString("rname");

			if (rName.equals("ROLE_ADMIN")) {
				isAdmin = 1;
				break;
			}
		}
		//

		if (rs.getInt("hide") == 1 && isAdmin != 1) {
			System.out.println("post not found");
			return;
		}

		getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setString(1, authName);
		ResultSet currentUser2 = getCurrentUser.executeQuery();
		if (!currentUser2.next()) {
			System.out.println("user not found");
			return;
		}
		int currentUser2Id = currentUser2.getInt("id");

		PreparedStatement saveNewComment = connect.prepareStatement(saveNewCommentSQL);
		saveNewComment.setInt(1, commentId);
		saveNewComment.setString(2, commentText);
		saveNewComment.setInt(3, currentDate);
		saveNewComment.setInt(4, 0);
		saveNewComment.setInt(5, currentUser2Id);
		saveNewComment.setInt(6, postId);
		saveNewComment.setInt(7, 0);
		saveNewComment.setInt(8, parentId);
		saveNewComment.executeUpdate();

		if (parentId != 0) {
			PreparedStatement insertIntoChildrenComments = connect.prepareStatement(insertIntoChildrenCommentsSQL);
			insertIntoChildrenComments.setInt(1, parentId);
			insertIntoChildrenComments.setInt(2, commentId);
			insertIntoChildrenComments.executeUpdate();
		}
	}

	public void deleteComment(int commentId, String authName,
			int currentTime, int maxDeleteTime) throws SQLException {
		String getCommentSQL = 
				"SELECT * FROM " + "COMMENTS"+
				" WHERE id = ?";

		String getCurrentUserSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String getUserRolesSQL = 
				"SELECT * FROM " + "USER_ROLE"+
				" WHERE userId = ?";

		String getRoleNameSQL = 
				"SELECT rname FROM " + "ROLES"+
				" WHERE id = ?";

		String getUserSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE id = ?";

		String deleteCommentSQL = 
				"UPDATE " + "COMMENTS" + 
				"   SET deleted = ?" +
				" WHERE id = ? ";

		PreparedStatement getComment = connect.prepareStatement(getCommentSQL);
		getComment.setInt(1, commentId);
		ResultSet rs = getComment.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		int userId = rs.getInt("userId");

		// isAdmin
		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setString(1, authName);
		ResultSet currentUser = getCurrentUser.executeQuery();
		if (!currentUser.next()) {
			System.out.println("user not found");
			return;
		}
		int currentUserId = currentUser.getInt("id");

		PreparedStatement getUserRoles = connect.prepareStatement(getUserRolesSQL);
		getUserRoles.setInt(1, currentUserId);
		ResultSet userRoles = getUserRoles.executeQuery();
		int isAdmin = 0;
		while (userRoles.next()) {
			int roleId = userRoles.getInt("roleId");

			PreparedStatement getRoleName = connect.prepareStatement(getRoleNameSQL);
			getRoleName.setInt(1, roleId);
			ResultSet roleName = getRoleName.executeQuery();
			if (!roleName.next()) {
				System.out.println("role not found");
				return;
			}
			String rName = roleName.getString("rname");

			if (rName.equals("ROLE_ADMIN")) {
				isAdmin = 1;
				break;
			}
		}
		//

		getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setString(1, authName);
		ResultSet currentUser2 = getCurrentUser.executeQuery();
		if (!currentUser2.next()) {
			System.out.println("comment not found");
			return;
		}
		String currentUsername = currentUser2.getString("username");

		PreparedStatement getUser = connect.prepareStatement(getUserSQL);
		getUser.setInt(1, userId);
		ResultSet user = getUser.executeQuery();
		if (!user.next()) {
			System.out.println("user not found");
			return;
		}
		String username = user.getString("username");

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
		deleteComment.setInt(2, commentId);
		deleteComment.executeUpdate();
	}

	public void editComment(int commentId, String authName,
			int currentTime, int maxEditTime, String newCommentText) throws SQLException {
		String getCommentSQL = 
				"SELECT * FROM " + "COMMENTS"+
				" WHERE id = ?";

		String getCurrentUserSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String getUserRolesSQL = 
				"SELECT * FROM " + "USER_ROLE"+
				" WHERE userId = ?";

		String getRoleNameSQL = 
				"SELECT rname FROM " + "ROLES"+
				" WHERE id = ?";

		String getUserSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE id = ?";

		String updateCommentSQL = 
				"UPDATE " + "COMMENTS" + 
				"   SET commentText = ?," +
				"       modifiedDateTime = ? " +
				" WHERE id = ? ";

		PreparedStatement getComment = connect.prepareStatement(getCommentSQL);
		getComment.setInt(1, commentId);
		ResultSet rs = getComment.executeQuery();
		if (!rs.next()) {
			System.out.println("comment not found");
			return;
		}
		int userId = rs.getInt("userId");

		// isAdmin
		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setString(1, authName);
		ResultSet currentUser = getCurrentUser.executeQuery();
		if (!currentUser.next()) {
			System.out.println("user not found");
			return;
		}
		int currentUserId = currentUser.getInt("id");

		PreparedStatement getUserRoles = connect.prepareStatement(getUserRolesSQL);
		getUserRoles.setInt(1, currentUserId);
		ResultSet userRoles = getUserRoles.executeQuery();
		int isAdmin = 0;
		while (userRoles.next()) {
			int roleId = userRoles.getInt("roleId");

			PreparedStatement getRoleName = connect.prepareStatement(getRoleNameSQL);
			getRoleName.setInt(1, roleId);
			ResultSet roleName = getRoleName.executeQuery();
			if (!roleName.next()) {
				System.out.println("role not found");
				return;
			}
			String rName = roleName.getString("rname");

			if (rName.equals("ROLE_ADMIN")) {
				isAdmin = 1;
				break;
			}
		}
		//

		getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setString(1, authName);
		ResultSet currentUser2 = getCurrentUser.executeQuery();
		if (!currentUser2.next()) {
			System.out.println("comment not found");
			return;
		}
		String currentUsername = currentUser2.getString("username");

		PreparedStatement getUser = connect.prepareStatement(getUserSQL);
		getUser.setInt(1, userId);
		ResultSet user = getUser.executeQuery();
		if (!user.next()) {
			System.out.println("user not found");
			return;
		}
		String username = user.getString("username");

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
		updateComment.setInt(2, currentTime);
		updateComment.setInt(3, commentId);
		updateComment.executeUpdate();
	}

	public void getCommentSource(int commentId) throws SQLException {
		String getCommentSQL = 
				"SELECT * FROM " + "COMMENTS"+
				" WHERE id = ?";

		PreparedStatement getComment = connect.prepareStatement(getCommentSQL);
		getComment.setInt(1, commentId);
		ResultSet rs = getComment.executeQuery();
		rs.next();
		String commentText = rs.getString("commentText");
	}

	public void commentLike(int commentId, String authName, int ratingId) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String getCommentSQL = 
				"SELECT * FROM " + "COMMENTS"+
				" WHERE id = ?";

		String getCommentRatingSQL = 
				"SELECT * FROM " + "COMMENT_RATING"+
				" WHERE userId = ? AND commentId = ?";

		String insertCommentRatingSQL = 
				"INSERT INTO " + "COMMENT_RATING" +
				" (id, userId, rate, commentId) " +
				" VALUES ( ?, ?, ?, ? )";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("user not found");
			return;
		}
		int currentUserId = rs.getInt("id");

		PreparedStatement getComment = connect.prepareStatement(getCommentSQL);
		getComment.setInt(1, commentId);
		ResultSet comment = getComment.executeQuery();
		if (!comment.next()) {
			System.out.println("comment not found");
			return;
		}
		int commentUserId = comment.getInt("userId");

		if (currentUserId == commentUserId) {
			System.out.println("cannot vote own comment");
			return;
		}

		PreparedStatement getCommentRating = connect.prepareStatement(getCommentRatingSQL);
		getCommentRating.setInt(1, currentUserId);
		getCommentRating.setInt(2, commentId);
		ResultSet commentRating = getCommentRating.executeQuery();
		if (commentRating.next()) {
			System.out.println("already voted");
			return;
		}

		PreparedStatement insertCommentRating = connect.prepareStatement(insertCommentRatingSQL);
		insertCommentRating.setInt(1, ratingId);
		insertCommentRating.setInt(2, currentUserId);
		insertCommentRating.setInt(3, 1);
		insertCommentRating.setInt(4, commentId);
		insertCommentRating.executeUpdate();
	}

	public void commentDislike(int commentId, String authName, int ratingId) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String getCommentSQL = 
				"SELECT * FROM " + "COMMENTS"+
				" WHERE id = ?";

		String getCommentRatingSQL = 
				"SELECT * FROM " + "COMMENT_RATING"+
				" WHERE userId = ? AND commentId = ?";

		String insertCommentRatingSQL = 
				"INSERT INTO " + "COMMENT_RATING" +
				" (id, userId, rate, commentId) " +
				" VALUES ( ?, ?, ?, ? )";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("user not found");
			return;
		}
		int currentUserId = rs.getInt("id");

		PreparedStatement getComment = connect.prepareStatement(getCommentSQL);
		getComment.setInt(1, commentId);
		ResultSet comment = getComment.executeQuery();
		if (!comment.next()) {
			System.out.println("comment not found");
			return;
		}
		int commentUserId = comment.getInt("userId");

		if (currentUserId == commentUserId) {
			System.out.println("cannot vote own comment");
			return;
		}

		PreparedStatement getCommentRating = connect.prepareStatement(getCommentRatingSQL);
		getCommentRating.setInt(1, currentUserId);
		getCommentRating.setInt(2, commentId);
		ResultSet commentRating = getCommentRating.executeQuery();
		if (commentRating.next()) {
			System.out.println("already voted");
			return;
		}

		PreparedStatement insertCommentRating = connect.prepareStatement(insertCommentRatingSQL);
		insertCommentRating.setInt(1, ratingId);
		insertCommentRating.setInt(2, currentUserId);
		insertCommentRating.setInt(3, -1);
		insertCommentRating.setInt(4, commentId);
		insertCommentRating.executeUpdate();
	}

	// PostsController
	public void showPostsList(String authName) throws SQLException {
		String getCurrentUserSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String getUserRolesSQL = 
				"SELECT * FROM " + "USER_ROLE"+
				" WHERE userId = ?";

		String getRoleNameSQL = 
				"SELECT rname FROM " + "ROLES"+
				" WHERE id = ?";

		String getAllPostsSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE 1 = 1";

		String getPublicPostsSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE hide = 0";
		
		// isAdmin
		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setString(1, authName);
		ResultSet rs = getCurrentUser.executeQuery();
		if (!rs.next()) {
			System.out.println("user not found");
			return;
		}
		int currentUserId = rs.getInt("id");

		PreparedStatement getUserRoles = connect.prepareStatement(getUserRolesSQL);
		getUserRoles.setInt(1, currentUserId);
		ResultSet userRoles = getUserRoles.executeQuery();
		int isAdmin = 0;
		while (userRoles.next()) {
			int roleId = userRoles.getInt("roleId");

			PreparedStatement getRoleName = connect.prepareStatement(getRoleNameSQL);
			getRoleName.setInt(1, roleId);
			ResultSet roleName = getRoleName.executeQuery();
			if (!roleName.next()) {
				System.out.println("role not found");
				return;
			}
			String rName = roleName.getString("rname");

			if (rName.equals("ROLE_ADMIN")) {
				isAdmin = 1;
				break;
			}
		}
		//

		if (isAdmin == 1) {
			PreparedStatement getAllPosts = connect.prepareStatement(getAllPostsSQL);
			ResultSet allPosts = getAllPosts.executeQuery();
			allPosts.next();
		} else {
			PreparedStatement getPublicPosts = connect.prepareStatement(getPublicPostsSQL);
			ResultSet publicPosts = getPublicPosts.executeQuery();
			publicPosts.next();
		}

		getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setString(1, authName);
		ResultSet currentUser = getCurrentUser.executeQuery();
		currentUser.next();
		currentUserId = currentUser.getInt("id");
	}

	public void getPostsList() throws SQLException {
		String getPublicPostsSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE hide = 0";

		PreparedStatement getPublicPosts = connect.prepareStatement(getPublicPostsSQL);
		ResultSet rs = getPublicPosts.executeQuery();
		rs.next();
	}

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
			int postId = rs.getInt("id");
			PreparedStatement getPostRating = connect.prepareStatement(getPostRatingSQL);
			getPostRating.setInt(1, postId);
			ResultSet postRating = getPostRating.executeQuery();
			postRating.next();
			int rating = postRating.getInt("rate");
		}
	}

	public void searchByTag(String authName, String[] tags) throws SQLException {
		String getTagSQL = 
				"SELECT * FROM " + "TAGS"+
				" WHERE tname = ?";

		String getCurrentUserSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String getUserRolesSQL = 
				"SELECT * FROM " + "USER_ROLE"+
				" WHERE userId = ?";

		String getRoleNameSQL = 
				"SELECT rname FROM " + "ROLES"+
				" WHERE id = ?";

		String getPostByTagSQL = 
				"SELECT postId FROM " + "POST_TAG"+
				" WHERE tagId = ?";

		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";
		
		String getPostNotHiddenSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ? AND hide = 0";

		for (String tagName : tags) {
			PreparedStatement getTag = connect.prepareStatement(getTagSQL);
			getTag.setString(1, tagName);
			ResultSet rs = getTag.executeQuery();
			rs.next();
			int tagId = rs.getInt("id");

			// isAdmin
			PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
			getCurrentUser.setString(1, authName);
			ResultSet currentUser = getCurrentUser.executeQuery();
			if (!currentUser.next()) {
				System.out.println("user not found");
				return;
			}
			int currentUserId = currentUser.getInt("id");

			PreparedStatement getUserRoles = connect.prepareStatement(getUserRolesSQL);
			getUserRoles.setInt(1, currentUserId);
			ResultSet userRoles = getUserRoles.executeQuery();
			int isAdmin = 0;
			while (userRoles.next()) {
				int roleId = userRoles.getInt("roleId");

				PreparedStatement getRoleName = connect.prepareStatement(getRoleNameSQL);
				getRoleName.setInt(1, roleId);
				ResultSet roleName = getRoleName.executeQuery();
				if (!roleName.next()) {
					System.out.println("role not found");
					return;
				}
				String rName = roleName.getString("rname");

				if (rName.equals("ROLE_ADMIN")) {
					isAdmin = 1;
					break;
				}
			}
			//

			PreparedStatement getPostByTag = connect.prepareStatement(getPostByTagSQL);
			getPostByTag.setInt(1, tagId);
			ResultSet postByTag = getPostByTag.executeQuery();
			while (postByTag.next()) {
				int postId = postByTag.getInt("postId");
				if (isAdmin == 1) {
					PreparedStatement getPost = connect.prepareStatement(getPostSQL);
					getPost.setInt(1, postId);
					ResultSet post = getPost.executeQuery();
					post.next();
				} else {
					PreparedStatement getPostNotHidden = connect.prepareStatement(getPostNotHiddenSQL);
					getPostNotHidden.setInt(1, postId);
					ResultSet post = getPostNotHidden.executeQuery();
					post.next();
				}
			}
		}

		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setString(1, authName);
		ResultSet currentUser2 = getCurrentUser.executeQuery();
		currentUser2.next();
		int currentUserId = currentUser2.getInt("id");
	}

	public void showPost(int postId, String authName) throws SQLException {
		String getCurrentUserSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";
		
		String getUserRolesSQL = 
				"SELECT * FROM " + "USER_ROLE"+
				" WHERE userId = ?";

		String getRoleNameSQL = 
				"SELECT rname FROM " + "ROLES"+
				" WHERE id = ?";

		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setInt(1, postId);
		ResultSet rs = getPost.executeQuery();
		if (!rs.next()) {
			System.out.println("post not found");
			return;
		}

		// isAdmin
		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setString(1, authName);
		ResultSet currentUser = getCurrentUser.executeQuery();
		if (!currentUser.next()) {
			System.out.println("user not found");
			return;
		}
		int currentUserId = currentUser.getInt("id");

		PreparedStatement getUserRoles = connect.prepareStatement(getUserRolesSQL);
		getUserRoles.setInt(1, currentUserId);
		ResultSet userRoles = getUserRoles.executeQuery();
		int isAdmin = 0;
		while (userRoles.next()) {
			int roleId = userRoles.getInt("roleId");

			PreparedStatement getRoleName = connect.prepareStatement(getRoleNameSQL);
			getRoleName.setInt(1, roleId);
			ResultSet roleName = getRoleName.executeQuery();
			if (!roleName.next()) {
				System.out.println("role not found");
				return;
			}
			String rName = roleName.getString("rname");

			if (rName.equals("ROLE_ADMIN")) {
				isAdmin = 1;
				break;
			}
		}
		//

		if (rs.getInt("hide") == 1 && isAdmin != 1) {
			System.out.println("error");
			return;
		}

		getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setString(1, authName);
		ResultSet currentUser2 = getCurrentUser.executeQuery();
		currentUser2.next();
	}

	public void createPost(int postId, String title, String fullPostText, int cutInd,
			int currentTime, String[] tags) throws SQLException {
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
		insertPost.setInt(1, postId);
		insertPost.setString(2, title);
		insertPost.setString(3, shortPostPart);
		insertPost.setString(4, fullPostText);
		insertPost.setInt(5, currentTime);
		insertPost.setInt(6, 0);
		insertPost.executeUpdate();

		for (String tagName : tags) {
			PreparedStatement getTag = connect.prepareStatement(getTagSQL);
			getTag.setString(1, tagName);
			ResultSet rs = getTag.executeQuery();
			rs.next();
			int tagId = rs.getInt("id");

			PreparedStatement insertPostTag = connect.prepareStatement(insertPostTagSQL);
			insertPostTag.setInt(1, postId);
			insertPostTag.setInt(2, tagId);
			insertPostTag.executeUpdate();
		}
	}

	public void showEditPostForm(int postId) throws SQLException {
		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";
		
		String getPostTagsSQL = 
				"SELECT * FROM " + "POST_TAG"+
				" WHERE postId = ?";

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setInt(1, postId);
		ResultSet rs = getPost.executeQuery();
		rs.next();
		int editPostId = rs.getInt("id");
		String text = rs.getString("fullPostText");
		String title = rs.getString("title");
		
		PreparedStatement getPostTags = connect.prepareStatement(getPostTagsSQL);
		getPostTags.setInt(1, postId);
		ResultSet postTag = getPostTags.executeQuery();
		postTag.next();
	}

	public void updatePost(int postId, String title, String fullPostText, int cutInd,
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
		getPost.setInt(1, postId);
		ResultSet rs = getPost.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}

		PreparedStatement deletePostTags = connect.prepareStatement(deletePostTagsSQL);
		deletePostTags.setInt(1, postId);
		deletePostTags.executeUpdate();

		String shortPostPart;
		if (cutInd > 0) shortPostPart = fullPostText.substring(0, cutInd);
		else shortPostPart = "";

		PreparedStatement updatePost = connect.prepareStatement(updatePostSQL);
		updatePost.setString(1, title);
		updatePost.setString(2, shortPostPart);
		updatePost.setString(3, fullPostText);
		updatePost.setInt(4, postId);
		updatePost.executeUpdate();

		for (String tagName : newTags) {
			PreparedStatement getTag = connect.prepareStatement(getTagSQL);
			getTag.setString(1, tagName);
			ResultSet tag = getTag.executeQuery();
			tag.next();
			int tagId = tag.getInt("id");

			PreparedStatement insertPostTag = connect.prepareStatement(insertPostTagSQL);
			insertPostTag.setInt(1, postId);
			insertPostTag.setInt(2, tagId);
			insertPostTag.executeUpdate();
		}
	}

	public void hidePost(int postId) throws SQLException {
		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";
		
		String setPostVisibilitySQL = 
				"UPDATE " + "POSTS" + 
				"   SET hide = ?" +
				" WHERE id = ? ";

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setInt(1, postId);
		ResultSet rs = getPost.executeQuery();
		rs.next();
		
		PreparedStatement setPostVisibility = connect.prepareStatement(setPostVisibilitySQL);
		setPostVisibility.setInt(1, 1);
		setPostVisibility.setInt(2, postId);
		setPostVisibility.executeUpdate();
	}

	public void unhidePost(int postId) throws SQLException {
		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";
		
		String setPostVisibilitySQL = 
				"UPDATE " + "POSTS" + 
				"   SET hide = ?" +
				" WHERE id = ? ";

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setInt(1, postId);
		ResultSet rs = getPost.executeQuery();
		rs.next();
		
		PreparedStatement setPostVisibility = connect.prepareStatement(setPostVisibilitySQL);
		setPostVisibility.setInt(1, 0);
		setPostVisibility.setInt(2, postId);
		setPostVisibility.executeUpdate();
	}

	public void deletePost(int postId) throws SQLException {
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
		getPost.setInt(1, postId);
		ResultSet rs = getPost.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}

		PreparedStatement deletePost = connect.prepareStatement(deletePostSQL);
		deletePost.setInt(1, postId);
		deletePost.executeUpdate();
		
		PreparedStatement deletePostTags = connect.prepareStatement(deletePostTagsSQL);
		deletePostTags.setInt(1, postId);
		deletePostTags.executeUpdate();
	}

	public void postLike(int postId, String authName, int ratingId) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";

		String getPostRatingSQL = 
				"SELECT * FROM " + "POST_RATING"+
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
		int currentUserId = rs.getInt("id");

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setInt(1, postId);
		ResultSet post = getPost.executeQuery();
		if (!post.next()) {
			System.out.println("post not found");
			return;
		}

		PreparedStatement getPostRating = connect.prepareStatement(getPostRatingSQL);
		getPostRating.setInt(1, currentUserId);
		getPostRating.setInt(2, postId);
		ResultSet postRating = getPostRating.executeQuery();
		if (postRating.next()) {
			System.out.println("already voted");
			return;
		}

		PreparedStatement insertPostRating = connect.prepareStatement(insertPostRatingSQL);
		insertPostRating.setInt(1, ratingId);
		insertPostRating.setInt(2, currentUserId);
		insertPostRating.setInt(3, 1);
		insertPostRating.setInt(4, postId);
		insertPostRating.executeUpdate();
	}

	public void postDislike(int postId, String authName, int ratingId) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";

		String getPostRatingSQL = 
				"SELECT * FROM " + "POST_RATING"+
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
		int currentUserId = rs.getInt("id");

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setInt(1, postId);
		ResultSet post = getPost.executeQuery();
		if (!post.next()) {
			System.out.println("post not found");
			return;
		}

		PreparedStatement getPostRating = connect.prepareStatement(getPostRatingSQL);
		getPostRating.setInt(1, currentUserId);
		getPostRating.setInt(2, postId);
		ResultSet postRating = getPostRating.executeQuery();
		if (postRating.next()) {
			System.out.println("already voted");
			return;
		}

		PreparedStatement insertPostRating = connect.prepareStatement(insertPostRatingSQL);
		insertPostRating.setInt(1, ratingId);
		insertPostRating.setInt(2, currentUserId);
		insertPostRating.setInt(3, -1);
		insertPostRating.setInt(4, postId);
		insertPostRating.executeUpdate();
	}

	// UsersController
	public void registerUser(int userId, String username, String email, String password,
		int currentTime) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String getUserByEmailSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE email = ?";

		String insertUserSQL = 
				"INSERT INTO " + "USERS" +
				" (id, username, email, password, enabled, registrationDate, aboutText, websiteLink, smallAvatarLink, bigAvatarLink) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

		String getRoleByNameSQL = 
				"SELECT * FROM " + "ROLES"+
				" WHERE rname = ?";

		String insertUsersRolesSQL = 
				"INSERT INTO " + "USER_ROLE" +
				" (userId, roleId) " +
				" VALUES ( ?, ? )";

		String getUserRolesSQL = 
				"SELECT * FROM " + "USER_ROLE"+
				" WHERE userId = ?";

		String getRoleByIdSQL = 
				"SELECT * FROM " + "ROLES"+
				" WHERE id = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, username);
		ResultSet rs = getUserByUsername.executeQuery();
		if (rs.next()) {
			System.out.println("username already exists");
			return;
		}

		PreparedStatement getUserByEmail = connect.prepareStatement(getUserByEmailSQL);
		getUserByEmail.setString(1, email);
		ResultSet userByEmail = getUserByEmail.executeQuery();
		if (userByEmail.next()) {
			System.out.println("email already exists");
			return;
		}

		PreparedStatement insertUser = connect.prepareStatement(insertUserSQL);
		insertUser.setInt(1, userId);
		insertUser.setString(2, username);
		insertUser.setString(3, email);
		insertUser.setString(4, password);
		insertUser.setInt(5, 1);
		insertUser.setInt(6, currentTime);
		insertUser.setString(7, "");
		insertUser.setString(8, "");
		insertUser.setString(9, "");
		insertUser.setString(10, "");
		insertUser.executeUpdate();

		PreparedStatement getRoleByName = connect.prepareStatement(getRoleByNameSQL);
		getRoleByName.setString(1, "ROLE_USER");
		ResultSet role = getRoleByName.executeQuery();
		if (!role.next()) {
			System.out.println("empty");
			return;
		}
		int roleId = role.getInt("id");

		PreparedStatement insertUsersRoles = connect.prepareStatement(insertUsersRolesSQL);
		insertUsersRoles.setInt(1, userId);
		insertUsersRoles.setInt(2, roleId);
		insertUsersRoles.executeUpdate();
		
		getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, username);
		ResultSet userByUsername = getUserByUsername.executeQuery();
		if (!userByUsername.next()) {
			System.out.println("empty");
			return;
		}
		int registeredUserId = userByUsername.getInt("id");

		PreparedStatement getUserRoles = connect.prepareStatement(getUserRolesSQL);
		getUserRoles.setInt(1, registeredUserId);
		ResultSet userRoles = getUserRoles.executeQuery();
		while (userRoles.next()) {
			int roleUserId = userRoles.getInt("roleId");

			PreparedStatement getRoleById = connect.prepareStatement(getRoleByIdSQL);
			getRoleById.setInt(1, roleUserId);
			ResultSet role2 = getRoleById.executeQuery();
			role2.next();
		}		
	}

	public void checkEmail(String email) throws SQLException {
		String getUserByEmailSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE email = ?";

		PreparedStatement getUserByEmail = connect.prepareStatement(getUserByEmailSQL);
		getUserByEmail.setString(1, email);
		ResultSet rs = getUserByEmail.executeQuery();
		rs.next();
	}

	public void checkUsername(String username) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, username);
		ResultSet rs = getUserByUsername.executeQuery();
		rs.next();
	}

	public void showEditSettingsPage(String authName) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		rs.next();
	}

	public void changeEmail(String username, String newEmail, String currentPassword,
			String authName, int userId) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String getUserByEmailSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE email = ?";
		
		String updateEmailSQL = 
				"UPDATE " + "USERS" + 
				"   SET email = ?" +
				" WHERE id = ? ";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, username);
		ResultSet rs = getUserByUsername.executeQuery();
		if (rs.next()) {
			System.out.println("username already exists");
			return;
		}

		PreparedStatement getUserByEmail = connect.prepareStatement(getUserByEmailSQL);
		getUserByEmail.setString(1, newEmail);
		ResultSet userByEmail = getUserByEmail.executeQuery();
		if (userByEmail.next()) {
			System.out.println("email already exists");
			return;
		}

		getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet userByUsername = getUserByUsername.executeQuery();
		if (!userByUsername.next()) {
			System.out.println("empty");
			return;
		}
		String registeredPassword = userByUsername.getString("password");

		if (!currentPassword.equals(registeredPassword)) {
			System.out.println("wrong password");
			return;
		}

		PreparedStatement updateEmail = connect.prepareStatement(updateEmailSQL);
		updateEmail.setString(1, newEmail);
		updateEmail.setInt(2, userId);
		updateEmail.executeUpdate();
	}

	public void changePassword(String newPassword, String currentPassword,
			String authName, int userId) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";
		
		String updatePasswordSQL = 
				"UPDATE " + "USERS" + 
				"   SET password = ?" +
				" WHERE id = ? ";

		String updateEmailSQL = 
				"UPDATE " + "USERS" + 
				"   SET email = ?" +
				" WHERE id = ? ";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("user not found");
			return;
		}
		String registeredPassword = rs.getString("password");
		String registeredEmail = rs.getString("email");

		if (!currentPassword.equals(registeredPassword)) {
			System.out.println("wrong password");
			return;
		}

		PreparedStatement updatePassword = connect.prepareStatement(updatePasswordSQL);
		updatePassword.setString(1, newPassword);
		updatePassword.setInt(2, userId);
		updatePassword.executeUpdate();

		PreparedStatement updateEmail = connect.prepareStatement(updateEmailSQL);
		updateEmail.setString(1, registeredEmail);
		updateEmail.setInt(2, userId);
		updateEmail.executeUpdate();
	}

	public void showEditProfilePage(String authName) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		rs.next();
	}

	public void editProfile(int hasError, String authName, int userId,
			String aboutText, String websiteLink) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String updateBigAvatarLinkSQL = 
				"UPDATE " + "USERS" + 
				"   SET bigAvatarLink = ?" +
				" WHERE id = ? ";

		String updateUserProfileSQL = 
				"UPDATE " + "USERS" + 
				"   SET aboutText = ?," +
				"       websiteLink = ?" +
				" WHERE id = ? ";

		if (hasError == 1) {
			PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
			getUserByUsername.setString(1, authName);
			ResultSet rs = getUserByUsername.executeQuery();
			rs.next();
			String currentBigAvatarLink = rs.getString("bigAvatarLink");

			PreparedStatement updateBigAvatarLink = connect.prepareStatement(updateBigAvatarLinkSQL);
			updateBigAvatarLink.setString(1, currentBigAvatarLink);
			updateBigAvatarLink.setInt(2, userId);
			updateBigAvatarLink.executeUpdate();
		} else {
			PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
			getUserByUsername.setString(1, authName);
			ResultSet userByUsername = getUserByUsername.executeQuery();
			userByUsername.next();

			PreparedStatement updateUserProfile = connect.prepareStatement(updateUserProfileSQL);
			updateUserProfile.setString(1, aboutText);
			updateUserProfile.setString(2, websiteLink);
			updateUserProfile.setInt(3, userId);
			updateUserProfile.executeUpdate();
		}
	}

	public void uploadAvatar(String authName, String newBigAvatarLink,
			String newSmallAvatarLink) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String updateUserAvatarsSQL = 
				"UPDATE " + "USERS" + 
				"   SET bigAvatarLink = ?," +
				"       smallAvatarLink = ?" +
				" WHERE id = ? ";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("user not found");
			return;
		}
		int userId = rs.getInt("id");
		
		PreparedStatement updateUserAvatars = connect.prepareStatement(updateUserAvatarsSQL);
		updateUserAvatars.setString(1, newBigAvatarLink);
		updateUserAvatars.setString(2, newSmallAvatarLink);
		updateUserAvatars.setInt(3, userId);
		updateUserAvatars.executeUpdate();
	}
	
	public void removeAvatar(String authName) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		String updateUserAvatarsSQL = 
				"UPDATE " + "USERS" + 
				"   SET bigAvatarLink = ?," +
				"       smallAvatarLink = ?" +
				" WHERE id = ? ";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("user not found");
			return;
		}
		int userId = rs.getInt("id");
		
		PreparedStatement updateUserAvatars = connect.prepareStatement(updateUserAvatarsSQL);
		updateUserAvatars.setString(1, "");
		updateUserAvatars.setString(2, "");
		updateUserAvatars.setInt(3, userId);
		updateUserAvatars.executeUpdate();
	}

	public void showProfile(String username) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, username);
		ResultSet rs = getUserByUsername.executeQuery();
		rs.next();
	}
}
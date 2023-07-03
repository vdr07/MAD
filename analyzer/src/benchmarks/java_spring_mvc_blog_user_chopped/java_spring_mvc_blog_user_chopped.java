package benchmarks.java_spring_mvc_blog_user_chopped;

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

public class java_spring_mvc_blog_user_chopped {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public java_spring_mvc_blog_user_chopped(int id) {
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

	//merged
	@ChoppedTransaction(originalTransaction="commentVote", microservice="m3")
	public void commentVote1(String authName) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT username FROM " + "USERS"+
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

	// PostsController
	@ChoppedTransaction(originalTransaction="showPostsList", microservice="m1")
	public void showPostsList1(int isAdmin) throws SQLException {
		String getAllPostsSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE 1 = 1";

		String getPublicPostsSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE hide = 0";

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
	}

	@ChoppedTransaction(originalTransaction="showPostsList", microservice="m3")
	public void showPostsList2(String authName) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT id FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("no current user");
			return;
		}
		long currentUserId = rs.getLong("id");
	}

	@ChoppedTransaction(originalTransaction="searchByTag", microservice="m6")
	public void searchByTag1(String tagName) throws SQLException {
		String getTagSQL = 
				"SELECT id FROM " + "TAGS"+
				" WHERE tname = ?";
		PreparedStatement getTag = connect.prepareStatement(getTagSQL);
		getTag.setString(1, tagName);
		ResultSet rs = getTag.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
		}
		long tagId = rs.getLong("id");
	}

	@ChoppedTransaction(originalTransaction="searchByTag", microservice="m1")
	public void searchByTag2(int isAdmin, long tagId) throws SQLException {

		String getPostByTagSQL = 
				"SELECT postId FROM " + "POST_TAG"+
				" WHERE tagId = ?";

		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";
		
		String getPostNotHiddenSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ? AND hide = 0";
		
		PreparedStatement getPostByTag = connect.prepareStatement(getPostByTagSQL);
		getPostByTag.setLong(1, tagId);
		ResultSet rs = getPostByTag.executeQuery();
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

	@ChoppedTransaction(originalTransaction="searchByTag", microservice="m3")
	public void searchByTag3(String authName) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT id FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("no current user");
			return;
		}
		long currentUserId = rs.getLong("id");
	}

	@ChoppedTransaction(originalTransaction="showPost", microservice="m1")
	public void showPost1(long postId, int isAdmin) throws SQLException {
		String getPostSQL = 
				"SELECT * FROM " + "POSTS"+
				" WHERE id = ?";

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
	}

	@ChoppedTransaction(originalTransaction="showPost", microservice="m3")
	public void showPost2(String authName) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT id FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("no current user");
			return;
		}
		long currentUserId = rs.getLong("id");
	}

	//merged
	@ChoppedTransaction(originalTransaction="postVote", microservice="m3")
	public void postVote1(String authName) throws SQLException {
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

	@ChoppedTransaction(originalTransaction="postVote", microservice="m1")
	public void postVote2(long postId) throws SQLException {
		String getPostSQL = 
				"SELECT id FROM " + "POSTS"+
				" WHERE id = ?";

		PreparedStatement getPost = connect.prepareStatement(getPostSQL);
		getPost.setLong(1, postId);
		ResultSet rs = getPost.executeQuery();
		if (!rs.next()) {
			System.out.println("post not found");
			return;
		}
	}

	@ChoppedTransaction(originalTransaction="postVote", microservice="m5")
	public void postVote3(long postId, long currentUserId, String authName, long ratingId,
			int like) throws SQLException {
		String getPostRatingSQL = 
				"SELECT * FROM " + "POST_RATING"+
				" WHERE userId = ? AND postId = ?";

		String insertPostRatingSQL = 
				"INSERT INTO " + "POST_RATING" +
				" (id, userId, rate, postId) " +
				" VALUES ( ?, ?, ?, ? )";
		
		PreparedStatement getPostRating = connect.prepareStatement(getPostRatingSQL);
		getPostRating.setLong(1, currentUserId);
		getPostRating.setLong(2, postId);
		ResultSet rs = getPostRating.executeQuery();
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

	// Users Controller
	@ChoppedTransaction(originalTransaction="registerUser", microservice="m3")
	public void registerUser1(long userId, String username, String email, String password,
		long currentTime) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT id FROM " + "USERS"+
				" WHERE username = ?";

		String getUserByEmailSQL = 
				"SELECT id FROM " + "USERS"+
				" WHERE email = ?";

		String insertUserSQL = 
				"INSERT INTO " + "USERS" +
				" (id, username, email, password, enabled, registrationDate, aboutText, websiteLink, smallAvatarLink, bigAvatarLink) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, username);
		ResultSet rs = getUserByUsername.executeQuery();
		if (rs.next()) {
			System.out.println("username already exists");
			return;
		}

		PreparedStatement getUserByEmail = connect.prepareStatement(getUserByEmailSQL);
		getUserByEmail.setString(1, email);
		rs = getUserByEmail.executeQuery();
		if (rs.next()) {
			System.out.println("email already exists");
			return;
		}

		PreparedStatement insertUser = connect.prepareStatement(insertUserSQL);
		insertUser.setLong(1, userId);
		insertUser.setString(2, username);
		insertUser.setString(3, email);
		insertUser.setString(4, password);
		insertUser.setInt(5, 1);
		insertUser.setLong(6, currentTime);
		insertUser.setString(7, "");
		insertUser.setString(8, "");
		insertUser.setString(9, "");
		insertUser.setString(10, "");
		insertUser.executeUpdate();
	}
	
	@ChoppedTransaction(originalTransaction="registerUser", microservice="m7")
	public void registerUser2() throws SQLException {
		String getRoleByNameSQL = 
				"SELECT id FROM " + "ROLES"+
				" WHERE rname = ?";

		PreparedStatement getRoleByName = connect.prepareStatement(getRoleByNameSQL);
		getRoleByName.setString(1, "ROLE_USER");
		ResultSet rs = getRoleByName.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
		long roleId = rs.getLong("id");
	}

	@ChoppedTransaction(originalTransaction="registerUser", microservice="m3")
	public void registerUser3(long userId, long roleId, String username) throws SQLException {
		String insertUsersRolesSQL = 
				"INSERT INTO " + "USER_ROLE" +
				" (userId, roleId) " +
				" VALUES ( ?, ? )";

		String getUserByUsernameSQL = 
				"SELECT id FROM " + "USERS"+
				" WHERE username = ?";

		String getUserRolesSQL = 
				"SELECT roleId FROM " + "USER_ROLE"+
				" WHERE userId = ?";

		PreparedStatement insertUsersRoles = connect.prepareStatement(insertUsersRolesSQL);
		insertUsersRoles.setLong(1, userId);
		insertUsersRoles.setLong(2, roleId);
		insertUsersRoles.executeUpdate();
		
		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, username);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
		long registeredUserId = rs.getLong("id");

		PreparedStatement getUserRoles = connect.prepareStatement(getUserRolesSQL);
		getUserRoles.setLong(1, registeredUserId);
		rs = getUserRoles.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}		
	}

	// done
	@ChoppedTransaction(microservice="m3")
	public void checkEmail(String email) throws SQLException {
		String getUserByEmailSQL = 
				"SELECT id FROM " + "USERS"+
				" WHERE email = ?";

		PreparedStatement getUserByEmail = connect.prepareStatement(getUserByEmailSQL);
		getUserByEmail.setString(1, email);
		ResultSet rs = getUserByEmail.executeQuery();
		if (rs.next()) {
			System.out.println("email exists");
			return;
		}		
	}

	// done
	@ChoppedTransaction(microservice="m3")
	public void checkUsername(String username) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT id FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, username);
		ResultSet rs = getUserByUsername.executeQuery();
		if (rs.next()) {
			System.out.println("username exists");
			return;
		}	
	}

	// done
	/* showProfile
	@ChoppedTransaction(microservice="m3")
	public void showEditSettingsPage(String authName) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USER"+
				" WHERE username = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
	}*/

	// done
	@ChoppedTransaction(microservice="m3")
	public void changeEmail(String username, String newEmail, String currentPassword,
			String authName, long userId) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT password FROM " + "USERS"+
				" WHERE username = ?";

		String getUserByEmailSQL = 
				"SELECT id FROM " + "USERS"+
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
		rs = getUserByEmail.executeQuery();
		if (rs.next()) {
			System.out.println("email already exists");
			return;
		}

		getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
		String registeredPassword = rs.getString("password");

		if (!currentPassword.equals(registeredPassword)) {
			System.out.println("wrong password");
			return;
		}

		PreparedStatement updateEmail = connect.prepareStatement(updateEmailSQL);
		updateEmail.setString(1, newEmail);
		updateEmail.setLong(2, userId);
		updateEmail.executeUpdate();
	}

	// done
	@ChoppedTransaction(microservice="m3")
	public void changePassword(String newPassword, String currentPassword,
			String authName, long userId) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT password, email FROM " + "USERS"+
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
			System.out.println("empty");
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
		updatePassword.setLong(2, userId);
		updatePassword.executeUpdate();

		PreparedStatement updateEmail = connect.prepareStatement(updateEmailSQL);
		updateEmail.setString(1, registeredEmail);
		updateEmail.setLong(2, userId);
		updateEmail.executeUpdate();
	}

	// done
	/* showProfile
	@ChoppedTransaction(microservice="m3")
	public void showEditProfilePage(String authName) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USER"+
				" WHERE username = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
	}*/

	// done
	@ChoppedTransaction(microservice="m3")
	public void editProfile(int hasError, String authName, long userId,
			String aboutText, String websiteLink) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT bigAvatarLink FROM " + "USERS"+
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
			if (!rs.next()) {
				System.out.println("empty");
				return;
			}
			String currentBigAvatarLink = rs.getString("bigAvatarLink");

			PreparedStatement updateBigAvatarLink = connect.prepareStatement(updateBigAvatarLinkSQL);
			updateBigAvatarLink.setString(1, currentBigAvatarLink);
			updateBigAvatarLink.setLong(2, userId);
			updateBigAvatarLink.executeUpdate();
			return;
		}

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}

		PreparedStatement updateUserProfile = connect.prepareStatement(updateUserProfileSQL);
		updateUserProfile.setString(1, aboutText);
		updateUserProfile.setString(2, websiteLink);
		updateUserProfile.setLong(3, userId);
		updateUserProfile.executeUpdate();
	}

	// done
	@ChoppedTransaction(microservice="m3")
	public void uploadAvatar(String authName, String newBigAvatarLink,
			String newSmallAvatarLink) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT id FROM " + "USERS"+
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
			System.out.println("empty");
			return;
		}
		long userId = rs.getLong("id");
		
		PreparedStatement updateUserAvatars = connect.prepareStatement(updateUserAvatarsSQL);
		updateUserAvatars.setString(1, newBigAvatarLink);
		updateUserAvatars.setString(2, newSmallAvatarLink);
		updateUserAvatars.setLong(3, userId);
		updateUserAvatars.executeUpdate();
	}
	
	// done
	/* uploadAvatar
	@ChoppedTransaction(microservice="m3")
	public void removeAvatar(String authName) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USER"+
				" WHERE username = ?";

		String updateUserAvatarsSQL = 
				"UPDATE " + "USER" + 
				"   SET bigAvatarLink = ?," +
				"       smallAvatarLink = ?" +
				" WHERE id = ? ";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, authName);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
		long userId = rs.getLong("id");
		
		PreparedStatement updateUserAvatars = connect.prepareStatement(updateUserAvatarsSQL);
		updateUserAvatars.setString(1, "");
		updateUserAvatars.setString(2, "");
		updateUserAvatars.setLong(3, userId);
		updateUserAvatars.executeUpdate();
	}*/

	// done
	@ChoppedTransaction(microservice="m3")
	public void showProfile(String username) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USERS"+
				" WHERE username = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, username);
		ResultSet rs = getUserByUsername.executeQuery();
		if (!rs.next()) {
			System.out.println("empty");
			return;
		}
		long userId = rs.getLong("id");
	}
}
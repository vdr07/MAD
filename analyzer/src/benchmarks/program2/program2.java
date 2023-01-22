package benchmarks.program2;

import ar.DependsOn;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class program2 {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public program2(int id) {
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
	}

	//eventualmente evocar a addFriendship_c3 a partir da c1

	public void addFriendship_c1(int profile_id1, int profile_id2) throws SQLException {
		String stmtstring = "INSERT INTO FRIENDS_BY_PROFILE (profile_id, friend_id)" +
			" VALUES (?, ?);";
		PreparedStatement stmt = connect.prepareStatement(stmtstring);
		stmt.setInt(1, profile_id1);
		stmt.setInt(2, profile_id2);
		stmt.executeUpdate();

		String stmtstring2 = "INSERT INTO FRIENDS_BY_PROFILE (profile_id, friend_id)" +
			" VALUES (?, ?);";
		PreparedStatement stmt2 = connect.prepareStatement(stmtstring2);
		stmt2.setInt(1, profile_id2);
		stmt2.setInt(2, profile_id1);
		stmt2.executeUpdate();
	}

	public void removeFriendship_c1(int profile_id1, int profile_id2) throws SQLException {
		String stmtstring = "DELETE FROM FRIENDS_BY_PROFILE WHERE profile_id = ? AND friend_id = ?;";
		PreparedStatement stmt = connect.prepareStatement(stmtstring);
		stmt.setInt(1, profile_id1);
		stmt.setInt(2, profile_id2);
		stmt.executeUpdate();

		String stmtstring2 = "DELETE FROM FRIENDS_BY_PROFILE WHERE profile_id = ? AND friend_id = ?;";
		PreparedStatement stmt2 = connect.prepareStatement(stmtstring2);
		stmt2.setInt(1, profile_id2);
		stmt2.setInt(2, profile_id1);
		stmt2.executeUpdate();
	}

	public void listFriendships_c1(int profile_id) throws SQLException {
		String stmtString = "SELECT friend_id FROM FRIENDS_BY_PROFILE WHERE profile_id = ?;";
		PreparedStatement stmt = connect.prepareStatement(stmtString);
		stmt.setInt(1, profile_id);
		ResultSet rs = stmt.executeQuery();
		int readID;
		while (rs.next() != false) {
			readID = rs.getInt("FRIEND_ID");
			System.out.println("Friend with ID = " + readID);
		}
	}

	public void createPost_c1(int profile_id) throws SQLException {
		String stmtstring = "INSERT INTO POSTS_BY_PROFILE (profile_id, post_id) VALUES (?, ?, ?);";
		PreparedStatement stmt = connect.prepareStatement(stmtstring);
		stmt.setInt(1, profile_id);
		int postid = ThreadLocalRandom.current().nextInt(1, 99999);
		stmt.setInt(2, postid);
		stmt.setString(3, "olaola");
		stmt.executeUpdate();
	}

	@DependsOn(name="createPost_c1")
	public void createPost_c2(int profile_id, int post_id) throws SQLException {
		String stmtstring = "UPDATE POSTS_BY_PROFILE SET text = ? WHERE profile_id = ? AND post_id = ?;";
		PreparedStatement stmt = connect.prepareStatement(stmtstring);
		stmt.setString(1, "olaola");
		stmt.setInt(2, profile_id);
		stmt.setInt(3, post_id);
		stmt.executeUpdate();
	}

//	//c1 gets all posts that profile_id published, and triggers getPosts_c2 to read the text from those posts
//	//c1 verifies if it is possible to access the posts (if they are friends) and c2 reads the posts
//	public void getPosts_c1(int profile_id, int me) throws SQLException {
//		String stmtstring = "SELECT friend_id FROM FRIENDS_BY_PROFILE WHERE profile_id = ? AND friend_id = ?;";
//		PreparedStatement stmt = connect.prepareStatement(stmtstring);
//		stmt.setInt(1, profile_id);
//		stmt.setInt(2, me);
//		ResultSet rs = stmt.executeQuery();
//
//		if (rs.next()) {}
//		else System.out.println("You cannot access this profile's posts.");
//	}
//
//	@DependsOn(name="getPosts_c1")
//	public void getPosts_c2(int profile_id) throws SQLException {
//		String stmtstring = "SELECT text, post_id FROM POSTS_BY_PROFILE WHERE profile_id = ?;";
//		PreparedStatement stmt = connect.prepareStatement(stmtstring);
//		stmt.setInt(1, profile_id);
//		ResultSet rs = stmt.executeQuery();
//		String readText;
//		int readID;
//		while (rs.next() != false) {
//			readText = rs.getString("TEXT");
//			readID = rs.getInt("POST_ID");
//			System.out.println(readText + "with ID = " + readID);
//		}
//	}
}
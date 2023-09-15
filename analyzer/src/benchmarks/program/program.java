package benchmarks.program;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class program {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public program(int id) {
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

	public void addFriendship(int profile_id1, int profile_id2) throws SQLException {
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

	public void removeFriendship(int profile_id1, int profile_id2) throws SQLException {
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

	public void listFriendships(int profile_id) throws SQLException {
		String stmtString = "SELECT friend_id FROM FRIENDS_BY_PROFILE WHERE profile_id = ?;";
		PreparedStatement stmt = connect.prepareStatement(stmtString);
		stmt.setInt(1, profile_id);
		ResultSet rs = stmt.executeQuery();
		int readID;
		while (rs.next()) {
			readID = rs.getInt("FRIEND_ID");
			System.out.println("Friend with ID = " + readID);
		}
	}

	public void createPost(int profile_id) throws SQLException {
		String stmtstring = "INSERT INTO POSTS_BY_PROFILE (profile_id, post_id) VALUES (?, ?);";
		PreparedStatement stmt = connect.prepareStatement(stmtstring);
		stmt.setInt(1, profile_id);
		stmt.setInt(2, ThreadLocalRandom.current().nextInt(1, 99999));
		stmt.executeUpdate();
	}

	public void getPosts(int profile_id, int me) throws SQLException {
		//first we have to check if this person is my friend
		String stmtstring = "SELECT friend_id FROM FRIENDS_BY_PROFILE WHERE profile_id = ? AND friend_id = ?;";
		PreparedStatement stmt = connect.prepareStatement(stmtstring);
		stmt.setInt(1, me);
		stmt.setInt(2, profile_id);
		ResultSet rs = stmt.executeQuery();
		String stmtstring2;
		PreparedStatement stmt2;
		ResultSet rs2;
		int readID;
		if (rs.next()) { //if they are friends
			stmtstring2 = "SELECT post_id FROM POSTS_BY_PROFILE WHERE profile_id = ?;";
			stmt2 = connect.prepareStatement(stmtstring2);
			stmt2.setInt(1, profile_id);
			rs2 = stmt2.executeQuery();
			while (rs2.next()) {
				readID = rs2.getInt("POST_ID");
				System.out.println("post with ID = " + readID);
			}
		}
		else{
			System.out.println("You cannot access this profile's posts.");
		}
	}
}
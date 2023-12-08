package benchmarks.find_sport_mates;

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

public class find_sport_mates {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public find_sport_mates(int id) {
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

	// EventController
	public void showMainPage() throws SQLException {
		String getAllEventsSQL = 
				"SELECT * FROM " + "EVENT"+
				" WHERE 1 = 1";

		String getUsernameByIdSQL = 
				"SELECT username FROM " + "USER"+
				" WHERE userId = ?";

		PreparedStatement getAllEvents = connect.prepareStatement(getAllEventsSQL);
		ResultSet allEvents = getAllEvents.executeQuery();
		while (allEvents.next()) {
			int hostId = allEvents.getInt("hostId");
			PreparedStatement getUsernameById = connect.prepareStatement(getUsernameByIdSQL);
			getUsernameById.setInt(1, hostId);
			ResultSet user = getUsernameById.executeQuery();
			user.next();
		}
	}

	public void showUserEvents(int hostId) throws SQLException {
		String findUserEventsSQL = 
				"SELECT * FROM " + "EVENT"+
				" WHERE hostId = ?";

		PreparedStatement findUserEvents = connect.prepareStatement(findUserEventsSQL);
		findUserEvents.setInt(1, hostId);
		ResultSet rs = findUserEvents.executeQuery();
		rs.next();
	}

	public void addEvent(int userId, int eventId, String eventType, String eventTime, 
			String eventDate, String eventPlace) throws SQLException {
		String getUserByIdSQL = 
				"SELECT * FROM " + "USER"+
				" WHERE userId = ?";

		String insertEventSQL = 
				"INSERT INTO " + "EVENT" +
				" (eventId, hostId, eventType, eventTime, eventDate, eventPlace) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";
		
		String insertEventUserSQL = 
				"INSERT INTO " + "EVENT_USER" +
				" (eventId, userId) " +
				" VALUES ( ?, ? )";

		PreparedStatement getUserById = connect.prepareStatement(getUserByIdSQL);
		getUserById.setInt(1, userId);
		ResultSet user = getUserById.executeQuery();
		user.next();

		PreparedStatement insertEvent = connect.prepareStatement(insertEventSQL);
		insertEvent.setInt(1, eventId);
		insertEvent.setInt(2, userId);
		insertEvent.setString(3, eventType);
		insertEvent.setString(4, eventTime);
		insertEvent.setString(5, eventDate);
		insertEvent.setString(6, eventPlace);
		insertEvent.executeUpdate();

		PreparedStatement insertEventUser = connect.prepareStatement(insertEventUserSQL);
		insertEventUser.setInt(1, eventId);
		insertEventUser.setInt(2, userId);
		insertEventUser.executeUpdate();
	}

	public void removeEvent(int eventId) throws SQLException {
		String removeEventSQL = 
				"DELETE FROM " + "EVENT"+
				" WHERE eventId = ?";

		PreparedStatement removeEvent = connect.prepareStatement(removeEventSQL);
		removeEvent.setInt(1, eventId);
		removeEvent.executeUpdate();
	}

	public void joinEvent(int userId, int eventId) throws SQLException {
		String getUserByIdSQL = 
				"SELECT * FROM " + "USER"+
				" WHERE userId = ?";

		String getEventByIdSQL = 
				"SELECT * FROM " + "EVENT"+
				" WHERE eventId = ?";

		String getEventParticipantsSQL = 
				"SELECT * FROM " + "EVENT_USER"+
				" WHERE eventId = ?";
		
		String insertEventUserSQL = 
				"INSERT INTO " + "EVENT_USER" +
				" (eventId, userId) " +
				" VALUES ( ?, ? )";

		PreparedStatement getUserById = connect.prepareStatement(getUserByIdSQL);
		getUserById.setInt(1, userId);
		ResultSet user = getUserById.executeQuery();
		user.next();

		PreparedStatement getEventById = connect.prepareStatement(getEventByIdSQL);
		getEventById.setInt(1, eventId);
		ResultSet event = getEventById.executeQuery();
		event.next();

		PreparedStatement getEventParticipants = connect.prepareStatement(getEventParticipantsSQL);
		getEventParticipants.setInt(1, eventId);
		ResultSet participants = getEventParticipants.executeQuery();
		participants.next();

		PreparedStatement insertEventUser = connect.prepareStatement(insertEventUserSQL);
		insertEventUser.setInt(1, eventId);
		insertEventUser.setInt(2, userId);
		insertEventUser.executeUpdate();
	}

	public void searchEvent() throws SQLException {
		
		String getAllEventsSQL = 
				"SELECT * FROM " + "EVENT"+
				" WHERE 1 = 1";

		String getUsernameByIdSQL = 
				"SELECT username FROM " + "USER"+
				" WHERE userId = ?";

		PreparedStatement getAllEvents = connect.prepareStatement(getAllEventsSQL);
		ResultSet allEvents = getAllEvents.executeQuery();
		while (allEvents.next()) {
			int hostId = allEvents.getInt("hostId");
			PreparedStatement getUsernameById = connect.prepareStatement(getUsernameByIdSQL);
			getUsernameById.setInt(1, hostId);
			ResultSet user = getUsernameById.executeQuery();
			user.next();
		}
	}

	// UserController
	public void addUser(int userId, String username, String password, String role,
			String phone, String firstname, String lastname) throws SQLException {
		String insertUserSQL = 
				"INSERT INTO " + "USER" +
				" (userId, username, password, role, phone, firstname, lastname) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ? )";

		PreparedStatement insertUser = connect.prepareStatement(insertUserSQL);
		insertUser.setInt(1, userId);
		insertUser.setString(2, username);
		insertUser.setString(3, password);
		insertUser.setString(4, role);
		insertUser.setString(5, phone);
		insertUser.setString(6, firstname);
		insertUser.setString(7, lastname);
		insertUser.executeUpdate();
	}

	public void handleLoginRequest(String username, String password) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USER"+
				" WHERE username = ?";

		String getUserIdByUsernameSQL = 
				"SELECT id FROM " + "USER"+
				" WHERE username = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, username);
		ResultSet user = getUserByUsername.executeQuery();
		user.next();
		String userPassword = user.getString("password");
		if (password != userPassword) {
			return;
		}

		PreparedStatement getUserIdByUsername = connect.prepareStatement(getUserIdByUsernameSQL);
		getUserIdByUsername.setString(1, username);
		ResultSet userId = getUserIdByUsername.executeQuery();
		userId.next();
	}

	public void searchUser(String username) throws SQLException {
		String getUserByUsernameSQL = 
				"SELECT * FROM " + "USER"+
				" WHERE username = ?";

		PreparedStatement getUserByUsername = connect.prepareStatement(getUserByUsernameSQL);
		getUserByUsername.setString(1, username);
		ResultSet user = getUserByUsername.executeQuery();
		user.next();
	}
}
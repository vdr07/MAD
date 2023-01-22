package benchmarks.v_write_and_check;

import ar.DependsOn;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class v_write_and_check {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public v_write_and_check(int id) {
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

/*
	public void one_write() throws SQLException {
		String stmtstring = "UPDATE VALUES SET value = ?" + " WHERE var_name = ?";
		PreparedStatement stmt = connect.prepareStatement(stmtstring);
		stmt.setInt(1, 1);
		stmt.setString(2, "a");
		stmt.executeUpdate();
	}

	@DependsOn(name="one_write")
	public void two_reads() throws SQLException {
		PreparedStatement stmt2 = connect.prepareStatement("SELECT value FROM values WHERE var_name = ?");
		stmt2.setString(1, "a");
		ResultSet rs = stmt2.executeQuery();
		rs.next();
		int read_val = rs.getInt("VALUE");
		System.out.println(read_val);

		PreparedStatement stmt3 = connect.prepareStatement("SELECT value FROM values WHERE var_name = ?");
		stmt3.setString(1, "a");
		ResultSet rs2 = stmt3.executeQuery();
		rs2.next();
		int read_val2 = rs2.getInt("VALUE");
		System.out.println(read_val2);
		assert(read_val == read_val2);
	}

	public void another_write() throws SQLException {
		String stmtstring = "UPDATE VALUES SET value = ?" + " WHERE var_name = ?";
		PreparedStatement stmt = connect.prepareStatement(stmtstring);
		stmt.setInt(1, 404);
		stmt.setString(2, "a");
		stmt.executeUpdate();
	}
*/


	public void writeAndCheck() throws SQLException {
		String stmtstring = "UPDATE VALUES SET value = ?" + " WHERE var_name = ?";
		PreparedStatement stmt = connect.prepareStatement(stmtstring);
		stmt.setInt(1, 50);
		stmt.setString(2, "a");
		stmt.executeUpdate();
		
		PreparedStatement stmt3 = connect.prepareStatement("SELECT value FROM values WHERE var_name = ?");
		stmt3.setString(1, "a");
		ResultSet rs = stmt3.executeQuery();
		rs.next();
		int read_val = rs.getInt("VALUE");
		assert (read_val == 50);
		System.out.println(read_val);
	}

	public void write() throws SQLException {
		String stmtstring = "UPDATE VALUES SET value = ?" + " WHERE var_name = ?";
		PreparedStatement stmt = connect.prepareStatement(stmtstring);
		stmt.setInt(1, 1);
		stmt.setString(2, "a");
		stmt.executeUpdate();
	}
}
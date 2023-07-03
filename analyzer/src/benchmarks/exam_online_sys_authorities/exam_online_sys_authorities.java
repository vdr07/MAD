package benchmarks.exam_online_sys_authorities;

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

public class exam_online_sys_authorities {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public exam_online_sys_authorities(int id) {
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

	// SysUserController
	@ChoppedTransaction(microservice="m1")
	public void sysUserSave(long[] authIds, long userId, String sysModifyLogId, String name,
			String username, String password, int money, String nowDate) throws SQLException {
		String getAuthoritySQL = 
				"SELECT * FROM " + "sys_authorities"+
				" WHERE id = ?";

		String insertSysUserAuthoritySQL = 
				"INSERT INTO " + "sys_user_authority" +
				" (sysUserId, sysAuthorityId) " +
				" VALUES ( ?, ? )";

		String getCurrentUserSQL = 
				"SELECT * FROM " + "sys_users"+
				" WHERE id = ?";

		String getSysModifyLogSQL = 
				"SELECT * FROM " + "sys_modify_logs"+
				" WHERE id = ?";

		String insertSysModifyLogSQL = 
				"INSERT INTO " + "sys_modify_logs" +
				" (id, creator, createDate, modifier, modifiedDate) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		String updateSysModifyLogSQL = 
				"UPDATE " + "sys_modify_logs" +
				"   SET modifier = ?," +
				"       modifiedDate = ?" +
				" WHERE id = ?";
				
		String insertSysUserSQL = 
				"INSERT INTO " + "sys_users" +
				" (id, sysModifyLog, name, username, password, money) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";
		
		for (long authId : authIds) {
			PreparedStatement getAuthority = connect.prepareStatement(getAuthoritySQL);
			getAuthority.setLong(1, authId);
			ResultSet rs = getAuthority.executeQuery();
			if (!rs.next()) {
				System.out.println("Empty");
				break;
			}

			PreparedStatement insertSysUserAuthority = connect.prepareStatement(insertSysUserAuthoritySQL);
			insertSysUserAuthority.setLong(1, userId);
			insertSysUserAuthority.setLong(2, authId);
			insertSysUserAuthority.executeUpdate();
		}
	
		PreparedStatement insertSysUserAuthority = connect.prepareStatement(insertSysUserAuthoritySQL);
		insertSysUserAuthority.setLong(1, userId);
		insertSysUserAuthority.setLong(2, 0);
		insertSysUserAuthority.executeUpdate();

		// updateSysModifyLog
		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setLong(1, userId);
		ResultSet rs = getCurrentUser.executeQuery();
		if (!rs.next()) {
			return;
		}

		PreparedStatement getSysModifyLog = connect.prepareStatement(getSysModifyLogSQL);
		getSysModifyLog.setString(1, sysModifyLogId);
		rs = getSysModifyLog.executeQuery();
		if (!rs.next()) {
			PreparedStatement insertSysModifyLog = connect.prepareStatement(insertSysModifyLogSQL);
			insertSysModifyLog.setString(1, sysModifyLogId);
			insertSysModifyLog.setLong(2, userId);
			insertSysModifyLog.setString(3, nowDate);
			insertSysModifyLog.setLong(4, userId);
			insertSysModifyLog.setString(5, nowDate);			
			insertSysModifyLog.executeUpdate();
		} else {
			PreparedStatement updateSysModifyLog = connect.prepareStatement(updateSysModifyLogSQL);
			updateSysModifyLog.setLong(1, userId);
			updateSysModifyLog.setString(2, nowDate);
			updateSysModifyLog.setString(3, sysModifyLogId);
			updateSysModifyLog.executeUpdate();
		}
		//

		PreparedStatement insertSysUser = connect.prepareStatement(insertSysUserSQL);
		insertSysUser.setLong(1, userId);
		insertSysUser.setString(2, sysModifyLogId);
		insertSysUser.setString(3, name);
		insertSysUser.setString(4, username);
		insertSysUser.setString(5, password);
		insertSysUser.setInt(6, money);
		insertSysUser.executeUpdate();
	}

	// IndexController
	@ChoppedTransaction(microservice="m1")
	public void indexTest(long userId) throws SQLException {
		String getCurrentUserSQL = 
				"SELECT * FROM " + "sys_users"+
				" WHERE id = ?";

		String getUserAuthoritiesSQL = 
				"SELECT sysAuthorityId FROM " + "sys_user_authority"+
				" WHERE sysUserId = ?";

		String getAuthorityByIdSQL = 
				"SELECT authority FROM " + "sys_authorities"+
				" WHERE id = ?";

		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setLong(1, userId);
		ResultSet rs = getCurrentUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		PreparedStatement getUserAuthorities = connect.prepareStatement(getUserAuthoritiesSQL);
		getUserAuthorities.setLong(1, userId);
		rs = getUserAuthorities.executeQuery();
		while (rs.next()) {
			long sysAuthorityId = rs.getLong("sysAuthorityId");

			PreparedStatement getAuthorityById = connect.prepareStatement(getAuthorityByIdSQL);
			getAuthorityById.setLong(1, sysAuthorityId);
			ResultSet authority = getAuthorityById.executeQuery();
			if (authority.next()) {
				String authorityName = authority.getString("authority");
				if (authorityName.equals("ROLE_ADMIN")) {
					return;
				}
			}
		}
	}
}
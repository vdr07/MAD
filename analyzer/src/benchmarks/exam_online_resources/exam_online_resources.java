package benchmarks.exam_online_resources;

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

public class exam_online_resources {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public exam_online_resources(int id) {
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

	// ResourceCommentController
	@ChoppedTransaction(microservice="m1")
	public void resourceCommentSave(long userId, long resourceId, String sysModifyLogId, String nowDate,
			long resourceCommentId, String content, int good) throws SQLException {
		String getCurrentUserSQL = 
				"SELECT * FROM " + "sys_users"+
				" WHERE id = ?";

		String resourceFindByIdSQL = 
				"SELECT * FROM " + "resources"+
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
		
		String insertResourceCommentSQL = 
				"INSERT INTO " + "resource_comments" +
				" (id, sysModifyLog, user, content, good, resource) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";

		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setLong(1, userId);
		ResultSet rs = getCurrentUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		PreparedStatement resourceFindById = connect.prepareStatement(resourceFindByIdSQL);
		resourceFindById.setLong(1, resourceId);
		rs = resourceFindById.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		
		// updateSysModifyLog
		getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setLong(1, userId);
		rs = getCurrentUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
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

		PreparedStatement insertResourceComment = connect.prepareStatement(insertResourceCommentSQL);
		insertResourceComment.setLong(1, resourceCommentId);
		insertResourceComment.setString(2, sysModifyLogId);
		insertResourceComment.setLong(3, userId);
		insertResourceComment.setString(4, content);
		insertResourceComment.setInt(5, good);
		insertResourceComment.setLong(6, resourceId);
		insertResourceComment.executeUpdate();
	}

	// ResourceController
	// merged save & upload
	@ChoppedTransaction(microservice="m1")
	public void resourceSave(long userId, String sysModifyLogId, String nowDate, long resourceId,
			String name, String description, String fileName, long fileSize,
			String filePath, int price) throws SQLException {
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
		
		String insertResourceSQL = 
				"INSERT INTO " + "resources" +
				" (id, sysModifyLog, name, description, fileName, fileSize, filePath, price, downloadTimes) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

		// updateSysModifyLog
		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setLong(1, userId);
		ResultSet rs = getCurrentUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
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

		PreparedStatement insertResource = connect.prepareStatement(insertResourceSQL);
		insertResource.setLong(1, resourceId);
		insertResource.setString(2, sysModifyLogId);
		insertResource.setString(3, name);
		insertResource.setString(4, description);
		insertResource.setString(5, fileName);
		insertResource.setLong(6, fileSize);
		insertResource.setString(7, filePath);
		insertResource.setInt(8, price);
		insertResource.setInt(9, 0);
		insertResource.executeUpdate();
	}

	@ChoppedTransaction(microservice="m1")
	public void resourceDownload(long resourceId, long userId, long buyLogId,
			String buyLogSysModifyLog) throws SQLException {
		String resourceFindByIdSQL = 
				"SELECT * FROM " + "resources"+
				" WHERE id = ?";

		String getCurrentUserSQL = 
				"SELECT * FROM " + "sys_users"+
				" WHERE id = ?";

		String updateSysUserMoneySQL = 
				"UPDATE " + "sys_users" +
				"   SET money = ?" +
				" WHERE id = ?";

		String getSysModifyLogCreatorSQL = 
				"SELECT creator FROM " + "sys_modify_logs"+
				" WHERE id = ?";

		String updateResourceDownloadTimesSQL = 
				"UPDATE " + "resources" +
				"   SET downloadTimes = ?" +
				" WHERE id = ?";

		String insertBuyLogSQL = 
				"INSERT INTO " + "buy_logs" +
				" (id, sysModifyLog, resource, user, spending) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		PreparedStatement resourceFindById = connect.prepareStatement(resourceFindByIdSQL);
		resourceFindById.setLong(1, resourceId);
		ResultSet rs = resourceFindById.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		int resourcePrice = rs.getInt("price");
		String resourceFilePath = rs.getString("filePath");
		String resourceSysModifyLogId = rs.getString("sysModifyLog");
		int resourceDownloadTimes = rs.getInt("downloadTimes");

		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setLong(1, userId);
		rs = getCurrentUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		int nowUserMoney = rs.getInt("money");

		if (nowUserMoney < resourcePrice) {
			System.out.println("No money");
			return;
		}

		PreparedStatement updateSysUserMoney = connect.prepareStatement(updateSysUserMoneySQL);
		updateSysUserMoney.setInt(1, nowUserMoney - resourcePrice);
		updateSysUserMoney.setLong(2, userId);
		updateSysUserMoney.executeUpdate();

		PreparedStatement getSysModifyLogCreator = connect.prepareStatement(getSysModifyLogCreatorSQL);
		getSysModifyLogCreator.setString(1, resourceSysModifyLogId);
		rs = getSysModifyLogCreator.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long creatorId = rs.getLong("creator");

		getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setLong(1, creatorId);
		rs = getCurrentUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		int nowCreatorMoney = rs.getInt("money");

		updateSysUserMoney = connect.prepareStatement(updateSysUserMoneySQL);
		updateSysUserMoney.setInt(1, nowCreatorMoney + resourcePrice);
		updateSysUserMoney.setLong(2, creatorId);
		updateSysUserMoney.executeUpdate();

		PreparedStatement updateResourceDownloadTimes = connect.prepareStatement(updateResourceDownloadTimesSQL);
		updateResourceDownloadTimes.setInt(1, resourceDownloadTimes + 1);
		updateResourceDownloadTimes.setLong(2, resourceId);
		updateResourceDownloadTimes.executeUpdate();

		PreparedStatement insertBuyLog = connect.prepareStatement(insertBuyLogSQL);
		insertBuyLog.setLong(1, buyLogId);
		insertBuyLog.setString(2, buyLogSysModifyLog);
		insertBuyLog.setLong(3, resourceId);
		insertBuyLog.setLong(4, userId);
		insertBuyLog.setInt(5, resourcePrice);
		insertBuyLog.executeUpdate();
	}

	// ADDED all
	@ChoppedTransaction(microservice="m1")
	public void resourceAll() throws SQLException {
		String resourceFindallSQL = 
				"SELECT * FROM " + "resources"+
				" WHERE 1 = 1";

		PreparedStatement resourceFindall = connect.prepareStatement(resourceFindallSQL);
		ResultSet rs = resourceFindall.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	// merged adminSearch & search
	@ChoppedTransaction(microservice="m1")
	public void resourceSearch(String resourceName) throws SQLException {
		String resourceFindByNameSQL = 
				"SELECT * FROM " + "resources"+
				" WHERE name = ?";

		PreparedStatement resourceFindByName = connect.prepareStatement(resourceFindByNameSQL);
		resourceFindByName.setString(1, resourceName);
		ResultSet rs = resourceFindByName.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void resourceShow(long resourceId, long userId) throws SQLException {
		String resourceFindByIdSQL = 
				"SELECT * FROM " + "resources"+
				" WHERE id = ?";

		String getCurrentUserSQL = 
				"SELECT * FROM " + "sys_users"+
				" WHERE id = ?";

		String sysModifyLogsFindByIdSQL = 
				"SELECT * FROM " + "sys_modify_logs"+
				" WHERE id = ?";

		String buyLogsFindByResourceAndUserSQL = 
				"SELECT * FROM " + "buy_logs"+
				" WHERE resource = ? AND user = ?";

		PreparedStatement resourceFindById = connect.prepareStatement(resourceFindByIdSQL);
		resourceFindById.setLong(1, resourceId);
		ResultSet rs = resourceFindById.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		String sysModifyLogId = rs.getString("sysModifyLog");

		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setLong(1, userId);
		rs = getCurrentUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long nowUserId = rs.getLong("id");

		PreparedStatement sysModifyLogsFindById = connect.prepareStatement(sysModifyLogsFindByIdSQL);
		sysModifyLogsFindById.setString(1, sysModifyLogId);
		rs = sysModifyLogsFindById.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long creatorId = rs.getLong("creator");

		if (creatorId == nowUserId) return;

		PreparedStatement buyLogsFindByResourceAndUser = connect.prepareStatement(buyLogsFindByResourceAndUserSQL);
		buyLogsFindByResourceAndUser.setLong(1, resourceId);
		buyLogsFindByResourceAndUser.setLong(2, userId);
		rs = buyLogsFindByResourceAndUser.executeQuery();
		if (rs.next()) return;
	}
}
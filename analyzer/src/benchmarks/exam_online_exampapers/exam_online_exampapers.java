package benchmarks.exam_online_exampapers;

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

public class exam_online_exampapers {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public exam_online_exampapers(int id) {
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

	// ExamController
	@ChoppedTransaction(microservice="m1")
	public void examSave(long exampaperId, long examId, long userId, String nowDate,
			String sysModifyLogId, String name, String description, int time) throws SQLException {
		String getExampaperSQL = 
				"SELECT * FROM " + "exampapers"+
				" WHERE id = ?";

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
		
		String insertExamSQL = 
				"INSERT INTO " + "exams" +
				" (id, sysModifyLog, name, description, time, exampaper) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";
		
		PreparedStatement getExampaper = connect.prepareStatement(getExampaperSQL);
		getExampaper.setLong(1, exampaperId);
		ResultSet rs = getExampaper.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		// updateSysModifyLog
		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setLong(1, userId);
		rs = getCurrentUser.executeQuery();
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

		PreparedStatement insertExam = connect.prepareStatement(insertExamSQL);
		insertExam.setLong(1, userId);
		insertExam.setString(2, sysModifyLogId);
		insertExam.setString(3, name);
		insertExam.setString(4, description);
		insertExam.setInt(5, time);
		insertExam.setLong(6, exampaperId);
		insertExam.executeUpdate();
	}

	// ExamPaperController
	@ChoppedTransaction(microservice="m1")
	public void examPaperSave(String sysModifyLogId, long userId, String nowDate,
			long exampaperId, String name, String description, long[] questionsIds) throws SQLException {
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
		
		String insertExampaperSQL = 
				"INSERT INTO " + "exampapers" +
				" (id, sysModifyLog, name, description) " +
				" VALUES ( ?, ?, ?, ? )";

		String insertExampaperQuestionSQL = 
				"INSERT INTO " + "exampaper_question" +
				" (exampaperId, questionId) " +
				" VALUES ( ?, ? )";

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

		PreparedStatement insertExampaper = connect.prepareStatement(insertExampaperSQL);
		insertExampaper.setLong(1, exampaperId);
		insertExampaper.setString(2, sysModifyLogId);
		insertExampaper.setString(3, name);
		insertExampaper.setString(4, description);
		insertExampaper.executeUpdate();

		for (long questionId : questionsIds) {
			PreparedStatement insertExampaperQuestion = connect.prepareStatement(insertExampaperQuestionSQL);
			insertExampaperQuestion.setLong(1, exampaperId);
			insertExampaperQuestion.setLong(2, questionId);
			insertExampaperQuestion.executeUpdate();
		}
	}

	@ChoppedTransaction(microservice="m1")
	public void examPaperSearch(String examPaperName) throws SQLException {
		String examPaperFindByNameSQL = 
				"SELECT * FROM " + "exampapers"+
				" WHERE name = ?";

		PreparedStatement examPaperFindByName = connect.prepareStatement(examPaperFindByNameSQL);
		examPaperFindByName.setString(1, examPaperName);
		ResultSet rs = examPaperFindByName.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}
}
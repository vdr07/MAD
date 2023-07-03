package benchmarks.exam_online_exams;

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

public class exam_online_exams {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public exam_online_exams(int id) {
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

	@ChoppedTransaction(microservice="m1")
	public void examHandIn(long[] chooseLogsIds, long examId, long questionId, long[] choicesIds, 
			String[] sysModifyLogIds, long userId, String nowDate, long examresultId,
			String examresultSysModifyLogId) throws SQLException {
		String insertChooseLogChoiceSQL = 
				"INSERT INTO " + "choose_log_choice" +
				" (chooseLogId, choiceId) " +
				" VALUES ( ?, ? )";

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
		
		String getExamByIdSQL = 
				"SELECT * FROM " + "exams"+
				" WHERE id = ?";

		String getQuestionByIdSQL = 
				"SELECT * FROM " + "questions"+
				" WHERE id = ?";

		String getChoiceByIdSQL = 
				"SELECT * FROM " + "choices"+
				" WHERE id = ?";

		String getQuestionAnswersSQL = 
				"SELECT answer FROM " + "choices"+
				" WHERE question = ?";

		String getChoicesSQL = 
				"SELECT choiceId FROM " + "choose_log_choice"+
				" WHERE chooseLogId = ?";

		String getCurrentUserSQL = 
				"SELECT * FROM " + "sys_users"+
				" WHERE id = ?";

		String insertChooseLogSQL = 
				"INSERT INTO " + "choose_logs" +
				" (id, sysModifyLog, exam, question, correct, user, display) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ?)";

		String getChooseLogCorrectSQL = 
				"SELECT correct FROM " + "choose_logs"+
				" WHERE id = ?";

		String insertExamresultSQL = 
				"INSERT INTO " + "examresults" +
				" (id, sysModifyLog, exam, user, allCount, wrongCount, grade, rank) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)";

		for (long chooseLogsId : chooseLogsIds) {
			for (long choiceId : choicesIds) {
				PreparedStatement insertChooseLogChoice = connect.prepareStatement(insertChooseLogChoiceSQL);
				insertChooseLogChoice.setLong(1, chooseLogsId);
				insertChooseLogChoice.setLong(2, choiceId);
				insertChooseLogChoice.executeUpdate();
			}
		}
		
		// updateSysModifyLog
		for (String sysModifyLogId : sysModifyLogIds) {
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
		}
		//

		for (int i = 0; i < chooseLogsIds.length; i++) {
			long chooseLogsId = chooseLogsIds[i];
			String sysModifyLogId = sysModifyLogIds[i];

			PreparedStatement getExamById = connect.prepareStatement(getExamByIdSQL);
			getExamById.setLong(1, examId);
			ResultSet rs = getExamById.executeQuery();
			if (!rs.next()) {
				return;
			}

			PreparedStatement getQuestionById = connect.prepareStatement(getQuestionByIdSQL);
			getQuestionById.setLong(1, questionId);
			rs = getQuestionById.executeQuery();
			if (!rs.next()) {
				return;
			}

			for (long choiceId : choicesIds) {
				PreparedStatement getChoiceById = connect.prepareStatement(getChoiceByIdSQL);
				getChoiceById.setLong(1, choiceId);
				rs = getChoiceById.executeQuery();
				if (!rs.next()) {
					return;
				}
			}

			PreparedStatement getQuestionAnswers = connect.prepareStatement(getQuestionAnswersSQL);
			getQuestionAnswers.setLong(1, questionId);
			rs = getQuestionAnswers.executeQuery();
			if (!rs.next()) {
				return;
			}
			long answerId = rs.getLong("answer");

			PreparedStatement getChoices = connect.prepareStatement(getChoicesSQL);
			getChoices.setLong(1, chooseLogsId);
			rs = getChoices.executeQuery();
			if (!rs.next()) {
				return;
			}
			long choiceId = rs.getLong("choiceId");

			int correct;
			if (answerId == choiceId) correct = 1;
			else correct = 0;

			int display;
			if (correct == 1) display = 0;
			else display = 1;

			PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
			getCurrentUser.setLong(1, userId);
			rs = getCurrentUser.executeQuery();
			if (!rs.next()) {
				return;
			}

			PreparedStatement insertChooseLog = connect.prepareStatement(insertChooseLogSQL);
			insertChooseLog.setLong(1, chooseLogsId);
			insertChooseLog.setString(2, sysModifyLogId);
			insertChooseLog.setLong(3, examId);
			insertChooseLog.setLong(4, questionId);
			insertChooseLog.setInt(5, correct);
			insertChooseLog.setLong(6, userId);
			insertChooseLog.setInt(7, display);
			insertChooseLog.executeUpdate();
		}

		int count = chooseLogsIds.length;
		int rightCount = 0;
		for (long chooseLogsId : chooseLogsIds) {
			PreparedStatement getChooseLogCorrect = connect.prepareStatement(getChooseLogCorrectSQL);
			getChooseLogCorrect.setLong(1, chooseLogsId);
			ResultSet rs = getChooseLogCorrect.executeQuery();
			if (!rs.next()) {
				return;
			}
			int isCorrect = rs.getInt("correct");
			if (isCorrect == 1) rightCount++;
		}

		int grade = 100 * rightCount / count;

		PreparedStatement insertExamresult = connect.prepareStatement(insertExamresultSQL);
		insertExamresult.setLong(1, examresultId);
		insertExamresult.setString(2, examresultSysModifyLogId);
		insertExamresult.setLong(3, examId);
		insertExamresult.setLong(4, userId);
		insertExamresult.setInt(5, count);
		insertExamresult.setInt(6, count - rightCount);
		insertExamresult.setInt(7, grade);
		insertExamresult.setInt(8, 0);
		insertExamresult.executeUpdate();
	}

	// ADDED
	@ChoppedTransaction(microservice="m1")
	public void examShow() throws SQLException {
		String examFindallSQL = 
				"SELECT * FROM " + "exams"+
				" WHERE 1 = 1";

		PreparedStatement examFindall = connect.prepareStatement(examFindallSQL);
		ResultSet rs = examFindall.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	// merged adminSearch and search
	@ChoppedTransaction(microservice="m1")
	public void examSearch(String examName) throws SQLException {
		String examFindByNameSQL = 
				"SELECT * FROM " + "exams"+
				" WHERE name = ?";

		PreparedStatement examFindByName = connect.prepareStatement(examFindByNameSQL);
		examFindByName.setString(1, examName);
		ResultSet rs = examFindByName.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
	}

	// ExamResultController
	@ChoppedTransaction(microservice="m1")
	public void examResultSave(long examId, long userId, String sysModifyLogId, String nowDate,
			long examresultId, int allCount, int wrongCount, int grade,
			int rank) throws SQLException {
		String examFindByIdSQL = 
				"SELECT * FROM " + "exams"+
				" WHERE id = ?";
		
		String userFindByIdSQL = 
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
		
		String insertExamresultSQL = 
				"INSERT INTO " + "examresults" +
				" (id, sysModifyLog, exam, user, allCount, wrongCount, grade, rank) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";

		PreparedStatement examFindById = connect.prepareStatement(examFindByIdSQL);
		examFindById.setLong(1, examId);
		ResultSet rs = examFindById.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		PreparedStatement userFindById = connect.prepareStatement(userFindByIdSQL);
		userFindById.setLong(1, userId);
		rs = userFindById.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		
		// updateSysModifyLog
		userFindById = connect.prepareStatement(userFindByIdSQL);
		userFindById.setLong(1, userId);
		rs = userFindById.executeQuery();
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

		PreparedStatement insertExamresult = connect.prepareStatement(insertExamresultSQL);
		insertExamresult.setLong(1, examresultId);
		insertExamresult.setString(2, sysModifyLogId);
		insertExamresult.setLong(3, examId);
		insertExamresult.setLong(4, userId);
		insertExamresult.setInt(5, allCount);
		insertExamresult.setInt(6, wrongCount);
		insertExamresult.setInt(7, grade);
		insertExamresult.setInt(8, rank);
		insertExamresult.executeUpdate();
	}
}
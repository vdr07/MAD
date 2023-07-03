package benchmarks.exam_online_sys_users;

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

public class exam_online_sys_users {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public exam_online_sys_users(int id) {
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

	@ChoppedTransaction(microservice="m1")
	public void sysUserSearch(String name) throws SQLException {
		String getSysUserByNameSQL = 
				"SELECT * FROM " + "sys_users"+
				" WHERE name = ?";

		PreparedStatement getSysUserByName = connect.prepareStatement(getSysUserByNameSQL);
		getSysUserByName.setString(1, name);
		ResultSet rs = getSysUserByName.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
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

	@ChoppedTransaction(microservice="m1")
	public void examResultTxn(long examId, long userId) throws SQLException {
		String getCurrentUserIdSQL = 
				"SELECT id FROM " + "sys_users"+
				" WHERE id = ?";
		
		String chooseLogFindByExamIdAndUserIdSQL = 
				"SELECT * FROM " + "choose_logs"+
				" WHERE exam = ? AND user = ?";

		String examResultFindByExamIdAndUserIdSQL = 
				"SELECT * FROM " + "examresults"+
				" WHERE exam = ? AND user = ?";

		PreparedStatement getCurrentUserId = connect.prepareStatement(getCurrentUserIdSQL);
		getCurrentUserId.setLong(1, userId);
		ResultSet rs = getCurrentUserId.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
			return;
		}

		PreparedStatement chooseLogFindByExamIdAndUserId = connect.prepareStatement(chooseLogFindByExamIdAndUserIdSQL);
		chooseLogFindByExamIdAndUserId.setLong(1, examId);
		chooseLogFindByExamIdAndUserId.setLong(2, userId);
		rs = chooseLogFindByExamIdAndUserId.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
			return;
		}
	
		PreparedStatement examResultFindByExamIdAndUserId = connect.prepareStatement(examResultFindByExamIdAndUserIdSQL);
		examResultFindByExamIdAndUserId.setLong(1, examId);
		examResultFindByExamIdAndUserId.setLong(2, userId);
		rs = examResultFindByExamIdAndUserId.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}	
	}

	@ChoppedTransaction(microservice="m1")
	public void examShowWrong(long userId) throws SQLException {
		String getCurrentUserIdSQL = 
				"SELECT id FROM " + "sys_users"+
				" WHERE id = ?";

		String findWrongByUserIdSQL = 
				"SELECT * FROM " + "choose_logs"+
				" WHERE user = ? AND display = 1 AND correct = 0";

		PreparedStatement getCurrentUserId = connect.prepareStatement(getCurrentUserIdSQL);
		getCurrentUserId.setLong(1, userId);
		ResultSet rs = getCurrentUserId.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		PreparedStatement findWrongByUserId = connect.prepareStatement(findWrongByUserIdSQL);
		findWrongByUserId.setLong(1, userId);
		rs = findWrongByUserId.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
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

	@ChoppedTransaction(microservice="m1")
	public void examResultShow(long userId) throws SQLException {
		String getCurrentUserSQL = 
				"SELECT * FROM " + "sys_users"+
				" WHERE id = ?";

		String examResultFindByUserSQL = 
				"SELECT * FROM " + "examresults"+
				" WHERE user = ?";

		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setLong(1, userId);
		ResultSet rs = getCurrentUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		PreparedStatement examResultFindByUser = connect.prepareStatement(examResultFindByUserSQL);
		examResultFindByUser.setLong(1, userId);
		rs = examResultFindByUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
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

	// QuestionCommentController
	@ChoppedTransaction(microservice="m1")
	public void questionCommentSave(long userId, long questionId, String sysModifyLogId, String nowDate,
			long questionCommentId, String content, int good) throws SQLException {
		String getCurrentUserSQL = 
				"SELECT * FROM " + "sys_users"+
				" WHERE id = ?";

		String questionFindByIdSQL = 
				"SELECT * FROM " + "questions"+
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
		
		String insertQuestionCommentSQL = 
				"INSERT INTO " + "question_comments" +
				" (id, sysModifyLog, user, content, good, question) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";

		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setLong(1, userId);
		ResultSet rs = getCurrentUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		PreparedStatement questionFindById = connect.prepareStatement(questionFindByIdSQL);
		questionFindById.setLong(1, questionId);
		rs = questionFindById.executeQuery();
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

		PreparedStatement insertQuestionComment = connect.prepareStatement(insertQuestionCommentSQL);
		insertQuestionComment.setLong(1, questionCommentId);
		insertQuestionComment.setString(2, sysModifyLogId);
		insertQuestionComment.setLong(3, userId);
		insertQuestionComment.setString(4, content);
		insertQuestionComment.setInt(5, good);
		insertQuestionComment.setLong(6, questionId);
		insertQuestionComment.executeUpdate();
	}

	@ChoppedTransaction(microservice="m1")
	public void questionCommentThumbsUp(long questionCommentId, long userId) throws SQLException {
		String questionCommentFindByIdSQL = 
				"SELECT * FROM " + "question_comments"+
				" WHERE id = ?";

		String getCurrentUserSQL = 
				"SELECT * FROM " + "sys_users"+
				" WHERE id = ?";

		String updateQuestionCommentGoodSQL = 
				"UPDATE " + "question_comments" +
				"   SET good = ?" +
				" WHERE id = ?";

		String updateSysUserMoneySQL = 
				"UPDATE " + "sys_users" +
				"   SET money = ?" +
				" WHERE id = ?";

		PreparedStatement questionCommentFindById = connect.prepareStatement(questionCommentFindByIdSQL);
		questionCommentFindById.setLong(1, questionCommentId);
		ResultSet rs = questionCommentFindById.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long commentUser = rs.getLong("user");
		int commentGood = rs.getInt("good");

		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setLong(1, userId);
		rs = getCurrentUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long nowUserId = rs.getLong("id");
		int nowUserMoney = rs.getInt("money");
		
		if (nowUserId != commentUser) {
			PreparedStatement updateQuestionCommentGood = connect.prepareStatement(updateQuestionCommentGoodSQL);
			updateQuestionCommentGood.setInt(1, commentGood + 1);
			updateQuestionCommentGood.setLong(2, questionCommentId);
			updateQuestionCommentGood.executeUpdate();

			PreparedStatement updateSysUserMoney = connect.prepareStatement(updateSysUserMoneySQL);
			updateSysUserMoney.setInt(1, nowUserMoney + 1);
			updateSysUserMoney.setLong(2, nowUserId);
			updateSysUserMoney.executeUpdate();
		}
	}

	// QuestionController
	@ChoppedTransaction(microservice="m1")
	public void questionSave(long questionId, long userId, String sysModifyLogId, String nowDate,
			long[] deletedChoicesIds, long[] newChoicesIds, long[] exampapersIds,
			String type, String content) throws SQLException {
		String questionGetChoicesSQL = 
				"SELECT * FROM " + "choices"+
				" WHERE question = ?";

		String deleteChoiceSQL = 
				"DELETE FROM " + "choices"+
				" WHERE id = ?";

		String updateChoiceSQL = 
				"UPDATE " + "choices" +
				"   SET question = ?" +
				" WHERE id = ?";

		String insertExampaperQuestionSQL = 
				"INSERT INTO " + "exampaper_question" +
				" (exampaperId, questionId) " +
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
		
		String insertQuestionSQL = 
				"INSERT INTO " + "questions" +
				" (id, sysModifyLog, type, content) " +
				" VALUES ( ?, ?, ?, ?)";

		PreparedStatement questionGetChoices = connect.prepareStatement(questionGetChoicesSQL);
		questionGetChoices.setLong(1, questionId);
		ResultSet rs = questionGetChoices.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}

		for (long deletedChoicesId : deletedChoicesIds) {
			PreparedStatement deleteChoice = connect.prepareStatement(deleteChoiceSQL);
			deleteChoice.setLong(1, deletedChoicesId);
			deleteChoice.executeUpdate();
		}

		for (long newChoicesId : newChoicesIds) {
			PreparedStatement updateChoice = connect.prepareStatement(updateChoiceSQL);
			updateChoice.setLong(1, newChoicesId);
			updateChoice.executeUpdate();
		}

		for (long exampapersId : exampapersIds) {
			PreparedStatement insertExampaperQuestion = connect.prepareStatement(insertExampaperQuestionSQL);
			insertExampaperQuestion.setLong(1, exampapersId);
			insertExampaperQuestion.setLong(2, questionId);
			insertExampaperQuestion.executeUpdate();
		}
		
		// updateSysModifyLog
		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
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

		PreparedStatement insertQuestion = connect.prepareStatement(insertQuestionSQL);
		insertQuestion.setLong(1, questionId);
		insertQuestion.setString(2, sysModifyLogId);
		insertQuestion.setString(3, type);
		insertQuestion.setString(4, content);
		insertQuestion.executeUpdate();
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

	@ChoppedTransaction(microservice="m1")
	public void resourceCommentThumbsUp(long resourceCommentId, long userId) throws SQLException {
		String resourceCommentFindByIdSQL = 
				"SELECT * FROM " + "resource_comments"+
				" WHERE id = ?";

		String getCurrentUserSQL = 
				"SELECT * FROM " + "sys_users"+
				" WHERE id = ?";

		String updateResourceCommentGoodSQL = 
				"UPDATE " + "resource_comments" +
				"   SET good = ?" +
				" WHERE id = ?";

		String updateSysUserMoneySQL = 
				"UPDATE " + "sys_users" +
				"   SET money = ?" +
				" WHERE id = ?";

		PreparedStatement resourceCommentFindById = connect.prepareStatement(resourceCommentFindByIdSQL);
		resourceCommentFindById.setLong(1, resourceCommentId);
		ResultSet rs = resourceCommentFindById.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long commentUser = rs.getLong("user");
		int commentGood = rs.getInt("good");

		PreparedStatement getCurrentUser = connect.prepareStatement(getCurrentUserSQL);
		getCurrentUser.setLong(1, userId);
		rs = getCurrentUser.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		long nowUserId = rs.getLong("id");
		int nowUserMoney = rs.getInt("money");
		
		if (nowUserId != commentUser) {
			PreparedStatement updateResourceCommentGood = connect.prepareStatement(updateResourceCommentGoodSQL);
			updateResourceCommentGood.setInt(1, commentGood + 1);
			updateResourceCommentGood.setLong(2, resourceCommentId);
			updateResourceCommentGood.executeUpdate();

			PreparedStatement updateSysUserMoney = connect.prepareStatement(updateSysUserMoneySQL);
			updateSysUserMoney.setInt(1, nowUserMoney + 1);
			updateSysUserMoney.setLong(2, nowUserId);
			updateSysUserMoney.executeUpdate();
		}
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
package benchmarks.tpcc_delivery_chopped2;

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

public class tpcc_delivery_chopped2 {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public tpcc_delivery_chopped2(int id) {
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

	@ChoppedTransaction(originalTransaction="delivery", microservice="m1")
	public void new_order_ops1(int d_id, int w_id) throws SQLException {
		String delivGetOrderIdSQL = 
			"SELECT NO_O_ID FROM " + "NEW_ORDER" + 
			" WHERE NO_D_ID = ? " +
			"   AND NO_W_ID = ? " +
			" ORDER BY NO_O_ID ASC " +
			" LIMIT 1";
		PreparedStatement delivGetOrderId = connect.prepareStatement(delivGetOrderIdSQL);
	
		delivGetOrderId.setInt(1, d_id);
		delivGetOrderId.setInt(2, w_id);

		ResultSet rs = delivGetOrderId.executeQuery();

		if (!rs.next()) {
			System.out.println("Empty");
		}

		int no_o_id = rs.getInt("NO_O_ID");
		rs.close();
	}

	@ChoppedTransaction(originalTransaction="delivery", microservice="m2")
	public void new_order_ops2(int d_id, int w_id, int no_o_id) throws SQLException {	
		String delivDeleteNewOrderSQL = 
			"DELETE FROM " + "NEW_ORDER" +
			" WHERE NO_O_ID = ? " +
			"   AND NO_D_ID = ?" +
			"   AND NO_W_ID = ?";
		PreparedStatement delivDeleteNewOrder =  connect.prepareStatement(delivDeleteNewOrderSQL);
		delivDeleteNewOrder.setInt(1, no_o_id);
		delivDeleteNewOrder.setInt(2, d_id);
		delivDeleteNewOrder.setInt(3, w_id);

		int result = delivDeleteNewOrder.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="delivery", microservice="m3")
	public void o_order_ops1(int d_id, int w_id, int no_o_id) throws SQLException {
		String delivGetCustIdSQL = 
			"SELECT O_C_ID FROM " + "OORDER" + 
			" WHERE O_ID = ? " +
			"   AND O_D_ID = ? " +
			"   AND O_W_ID = ?";
		PreparedStatement delivGetCustId = connect.prepareStatement(delivGetCustIdSQL);
		delivGetCustId.setInt(1, no_o_id);
		delivGetCustId.setInt(2, d_id);
		delivGetCustId.setInt(3, w_id);
		ResultSet rs = delivGetCustId.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		int c_id = rs.getInt("O_C_ID");
		rs.close();
	}

	@ChoppedTransaction(originalTransaction="delivery", microservice="m4")
	public void o_order_ops2(int d_id, int w_id, int no_o_id) throws SQLException {	
		String delivUpdateCarrierIdSQL = 
			"UPDATE " + "OORDER" + 
			"   SET O_CARRIER_ID = ? " +
			" WHERE O_ID = ? " +
			"   AND O_D_ID = ?" +
			"   AND O_W_ID = ?";
		PreparedStatement delivUpdateCarrierId = connect.prepareStatement(delivUpdateCarrierIdSQL);
        int o_carrier_id = r.nextInt(10) + 1; // 1-10
		delivUpdateCarrierId.setInt(1, o_carrier_id);
		delivUpdateCarrierId.setInt(2, no_o_id);
		delivUpdateCarrierId.setInt(3, d_id);
		delivUpdateCarrierId.setInt(4, w_id);
		int result = delivUpdateCarrierId.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="delivery", microservice="m5")
	public void order_line_ops1(int d_id, int w_id, int no_o_id) throws SQLException {
		String delivUpdateDeliveryDateSQL = 
			"UPDATE " + "ORDER_LINE" +
			"   SET OL_DELIVERY_D = ? " +
			" WHERE OL_O_ID = ? " +
			"   AND OL_D_ID = ? " +
			"   AND OL_W_ID = ? ";
		PreparedStatement delivUpdateDeliveryDate = connect.prepareStatement(delivUpdateDeliveryDateSQL);
		delivUpdateDeliveryDate.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		delivUpdateDeliveryDate.setInt(2, no_o_id);
		delivUpdateDeliveryDate.setInt(3, d_id);
		delivUpdateDeliveryDate.setInt(4, w_id);
		int result = delivUpdateDeliveryDate.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="delivery", microservice="m6")
	public void order_line_ops2(int d_id, int w_id, int no_o_id) throws SQLException {
		String delivSumOrderAmountSQL = 
			"SELECT OL_AMOUNT FROM " + "ORDER_LINE" + 
			" WHERE OL_O_ID = ? " +
			"   AND OL_D_ID = ? " +
			"   AND OL_W_ID = ?";
		PreparedStatement delivSumOrderAmount = connect.prepareStatement(delivSumOrderAmountSQL);
		delivSumOrderAmount.setInt(1, no_o_id);
		delivSumOrderAmount.setInt(2, d_id);
		delivSumOrderAmount.setInt(3, w_id);
		ResultSet rs = delivSumOrderAmount.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		double ol_total = rs.getDouble("OL_AMOUNT");
		while(rs.next()) {
			ol_total += rs.getDouble("OL_AMOUNT");
		}
	}

	@ChoppedTransaction(originalTransaction="delivery", microservice="m7")
	public void customer_ops1(int d_id, int w_id, int c_id) throws SQLException {
		String delivGetCustBalDelivCntSQL = 
			"SELECT C_BALANCE, C_DELIVERY_CNT FROM " + "CUSTOMER" + 
			" WHERE C_W_ID = ? " +
			"   AND C_D_ID = ? " +
			"   AND C_ID = ?";
		PreparedStatement delivGetCustBalDelivCnt = connect.prepareStatement(delivGetCustBalDelivCntSQL);
		delivGetCustBalDelivCnt.setInt(1, w_id);
		delivGetCustBalDelivCnt.setInt(2, d_id);
		delivGetCustBalDelivCnt.setInt(3, c_id);
		ResultSet rs = delivGetCustBalDelivCnt.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		double c_balance = rs.getDouble("C_BALANCE");
		int c_delivery_cnt = rs.getInt("C_DELIVERY_CNT");
		rs.close();
	}

	@ChoppedTransaction(originalTransaction="delivery", microservice="m8")
	public void customer_ops2(int d_id, int w_id, int c_id, double ol_total, double c_balance, int c_delivery_cnt) throws SQLException {
		String delivUpdateCustBalDelivCntSQL = 
			"UPDATE " + "CUSTOMER" +
			"   SET C_BALANCE = ?," +
			"       C_DELIVERY_CNT = ? " +
			" WHERE C_W_ID = ? " +
			"   AND C_D_ID = ? " +
			"   AND C_ID = ? ";
		PreparedStatement delivUpdateCustBalDelivCnt = connect.prepareStatement(delivUpdateCustBalDelivCntSQL);

		delivUpdateCustBalDelivCnt.setDouble(1, c_balance + ol_total);
		delivUpdateCustBalDelivCnt.setInt(2, c_delivery_cnt + 1);
		delivUpdateCustBalDelivCnt.setInt(3, w_id);
		delivUpdateCustBalDelivCnt.setInt(4, d_id);
		delivUpdateCustBalDelivCnt.setInt(5, c_id);

		int result = delivUpdateCustBalDelivCnt.executeUpdate();
	}
}
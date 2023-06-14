package benchmarks.tpcc_chopped2;

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

public class tpcc_chopped2 {

	class Customer {
		public int c_id;
		public int c_d_id;
		public int c_w_id;
		public int c_payment_cnt;
		public int c_delivery_cnt;
		public Timestamp c_since;
		public double c_discount;
		public double c_credit_lim;
		public double c_balance;
		public double c_ytd_payment;
		public String c_credit;
		public String c_last;
		public String c_first;
		public String c_street_1;
		public String c_street_2;
		public String c_city;
		public String c_state;
		public String c_zip;
		public String c_phone;
		public String c_middle;
		public String c_data;
	}

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public tpcc_chopped2(int id) {
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

	//
	// Delivery
	//
	@ChoppedTransaction(originalTransaction="delivery", microservice="m1")
	public void delivery_new_order_ops(int d_id, int w_id, double ol_total) throws SQLException {
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
		rs = null;
		
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

		String delivGetCustIdSQL = 
			"SELECT O_C_ID FROM " + "OORDER" + 
			" WHERE O_ID = ? " +
			"   AND O_D_ID = ? " +
			"   AND O_W_ID = ?";
		PreparedStatement delivGetCustId = connect.prepareStatement(delivGetCustIdSQL);
		delivGetCustId.setInt(1, no_o_id);
		delivGetCustId.setInt(2, d_id);
		delivGetCustId.setInt(3, w_id);
		rs = delivGetCustId.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty");
		}
		int c_id = rs.getInt("O_C_ID");
		rs.close();
		
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
		result = delivUpdateCarrierId.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="delivery", microservice="m3")
	public void delivery_order_line_ops(int d_id, int w_id, int no_o_id) throws SQLException {
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

	@ChoppedTransaction(originalTransaction="delivery", microservice="m2")
	public void delivery_customer_ops(int d_id, int w_id, int c_id, double ol_total) throws SQLException {
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

	//
	// NewOrder
	//
	@ChoppedTransaction(originalTransaction="newOrder", microservice="m2")
	public void newOrder_customer_ops(int d_id, int w_id, int c_id, double ol_total) throws SQLException {
		String stmtGetCustSQL = 
			"SELECT C_DISCOUNT, C_LAST, C_CREDIT" +
			"  FROM " + "CUSTOMER" + 
			" WHERE C_W_ID = ? " + 
			"   AND C_D_ID = ? " +
			"   AND C_ID = ?";
		PreparedStatement stmtGetCust = connect.prepareStatement(stmtGetCustSQL);
		stmtGetCust.setInt(1, w_id);
		stmtGetCust.setInt(2, d_id);
		stmtGetCust.setInt(3, c_id);
		ResultSet rs = stmtGetCust.executeQuery();
		if (!rs.next())
			System.out.println("Empty!");
		double c_discount = rs.getDouble("C_DISCOUNT");
		String c_last = rs.getString("C_LAST");
		String c_credit = rs.getString("C_CREDIT");
		rs.close();
		rs = null;

		String stmtGetWhseSQL = 
			"SELECT W_TAX " + 
			"  FROM " + "WAREHOUSE" + 
			" WHERE W_ID = ?";
		PreparedStatement stmtGetWhse = connect.prepareStatement(stmtGetWhseSQL);
		stmtGetWhse.setInt(1, w_id);
		rs = stmtGetWhse.executeQuery();
		if (!rs.next())
			System.out.println("Empty!");
		double w_tax = rs.getDouble("W_TAX");
		rs.close();
		rs = null;

		String stmtGetDistSQL = 
			"SELECT D_NEXT_O_ID, D_TAX " +
			"  FROM " + "DISTRICT" +
			" WHERE D_W_ID = ? AND D_ID = ?";
		PreparedStatement stmtGetDist = connect.prepareStatement(stmtGetDistSQL);
		stmtGetDist.setInt(1, w_id);
		stmtGetDist.setInt(2, d_id);
		rs = stmtGetDist.executeQuery();
		if (!rs.next()) {
			System.out.println("Empty!");
		}
		int d_next_o_id = rs.getInt("D_NEXT_O_ID");
		double d_tax = rs.getDouble("D_TAX");
		rs.close();
		rs = null;

		String stmtUpdateDistSQL = 
			"UPDATE " + "DISTRICT" + 
			"   SET D_NEXT_O_ID = ? " +
			" WHERE D_W_ID = ? " +
			"   AND D_ID = ?";
		PreparedStatement stmtUpdateDist = connect.prepareStatement(stmtUpdateDistSQL);
		stmtUpdateDist.setInt(1, d_next_o_id + 1);
		stmtUpdateDist.setInt(2, w_id);
		stmtUpdateDist.setInt(3, d_id);
		int result = stmtUpdateDist.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="newOrder", microservice="m1")
	public void newOrder_o_order_ops(int o_id, int d_id, int w_id, int c_id,
			int o_ol_cnt, int o_all_local) throws SQLException {
		String stmtInsertOOrderSQL = 
			"INSERT INTO " + "OORDER" + 
			" (O_ID, O_D_ID, O_W_ID, O_C_ID, O_CARRIER_ID, O_ENTRY_D, O_OL_CNT, O_ALL_LOCAL)" + 
			" VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement stmtInsertOOrder = connect.prepareStatement(stmtInsertOOrderSQL);
		stmtInsertOOrder.setInt(1, o_id);
		stmtInsertOOrder.setInt(2, d_id);
		stmtInsertOOrder.setInt(3, w_id);
		stmtInsertOOrder.setInt(4, c_id);
		stmtInsertOOrder.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
		stmtInsertOOrder.setInt(7, o_ol_cnt);
		stmtInsertOOrder.setInt(8, o_all_local);
		stmtInsertOOrder.executeUpdate();

		String stmtInsertNewOrderSQL = 
			"INSERT INTO " + "NEW_ORDER" +
			" (NO_O_ID, NO_D_ID, NO_W_ID) " +
			" VALUES ( ?, ?, ?)";
		PreparedStatement stmtInsertNewOrder = connect.prepareStatement(stmtInsertNewOrderSQL);
		stmtInsertNewOrder.setInt(1, o_id);
		stmtInsertNewOrder.setInt(2, d_id);
		stmtInsertNewOrder.setInt(3, w_id);
		stmtInsertNewOrder.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="newOrder", microservice="m3")
	public void newOrder_item_ops(int ol_i_id, int ol_number, int o_ol_cnt,
			int ol_supply_w_id, int w_id, int ol_quantity, int d_id, int o_id,
			double ol_amount) throws SQLException {

		String stmtGetItemSQL = 
			"SELECT I_PRICE, I_NAME , I_DATA " +
			"  FROM " + "ITEM" + 
			" WHERE I_ID = ?";
		PreparedStatement stmtGetItem = connect.prepareStatement(stmtGetItemSQL);
		stmtGetItem.setInt(1, ol_i_id);
		ResultSet rs = stmtGetItem.executeQuery();
		if (!rs.next()) {
			assert ol_number == o_ol_cnt;
			assert ol_i_id == -12345;
			rs.close();
		}
		double i_price = rs.getDouble("I_PRICE");
		String i_name = rs.getString("I_NAME");
		String i_data = rs.getString("I_DATA");
		rs.close();
		rs = null;

		String stmtGetStockSQL = 
			"SELECT S_QUANTITY, S_YTD, S_ORDER_CNT, S_REMOTE_CNT, S_DATA, S_DIST_01, S_DIST_02, S_DIST_03, S_DIST_04, S_DIST_05, S_DIST_06, S_DIST_07, S_DIST_08, S_DIST_09, S_DIST_10 " +
			"  FROM " + "STOCK" + 
			" WHERE S_I_ID = ? " +
			"   AND S_W_ID = ?";
		PreparedStatement stmtGetStock = connect.prepareStatement(stmtGetStockSQL);
		String s_dist_01, s_dist_02, s_dist_03, s_dist_04, s_dist_05;
		String s_dist_06, s_dist_07, s_dist_08, s_dist_09, s_dist_10, ol_dist_info = null;
		stmtGetStock.setInt(1, ol_i_id);
		stmtGetStock.setInt(2, ol_supply_w_id);
		rs = stmtGetStock.executeQuery();
		if (!rs.next())
			System.out.println("Empty!");
		int s_quantity = rs.getInt("S_QUANTITY");
		double s_ytd = rs.getDouble("S_YTD");
		int s_order_cnt = rs.getInt("S_ORDER_CNT");
		int s_remote_cnt = rs.getInt("S_REMOTE_CNT");
		String s_data = rs.getString("S_DATA");
		s_dist_01 = rs.getString("S_DIST_01");
		s_dist_02 = rs.getString("S_DIST_02");
		s_dist_03 = rs.getString("S_DIST_03");
		s_dist_04 = rs.getString("S_DIST_04");
		s_dist_05 = rs.getString("S_DIST_05");
		s_dist_06 = rs.getString("S_DIST_06");
		s_dist_07 = rs.getString("S_DIST_07");
		s_dist_08 = rs.getString("S_DIST_08");
		s_dist_09 = rs.getString("S_DIST_09");
		s_dist_10 = rs.getString("S_DIST_10");
		rs.close();
		rs = null;

		String stmtUpdateStockSQL = 
			"UPDATE " + "STOCK" + 
			"   SET S_QUANTITY = ?, " +
			"       S_YTD = ?, " + 
			"       S_ORDER_CNT = ?, " +
			"       S_REMOTE_CNT = ? " +
			" WHERE S_I_ID = ? " +
			"   AND S_W_ID = ?";
		PreparedStatement stmtUpdateStock = connect.prepareStatement(stmtUpdateStockSQL);
		if (s_quantity - ol_quantity >= 10) {
			s_quantity -= ol_quantity;
		} else {
			s_quantity += -ol_quantity + 91;
		}

		int s_remote_cnt_increment;
		if (ol_supply_w_id == w_id) {
			s_remote_cnt_increment = 0;
		} else {
			s_remote_cnt_increment = 1;
		}
		stmtUpdateStock.setInt(1, s_quantity);
		stmtUpdateStock.setDouble(2, s_ytd + ol_quantity);
		stmtUpdateStock.setInt(3, s_order_cnt + 1);
		stmtUpdateStock.setInt(4, s_remote_cnt + s_remote_cnt_increment);
		stmtUpdateStock.setInt(5, ol_i_id);
		stmtUpdateStock.setInt(6, ol_supply_w_id);
		stmtUpdateStock.executeUpdate();
		
		if(d_id == 1)
			ol_dist_info = s_dist_01;
		else if(d_id == 2)
			ol_dist_info = s_dist_02;
		else if(d_id == 3)
			ol_dist_info = s_dist_03;
		else if(d_id == 4)
			ol_dist_info = s_dist_04;
		else if(d_id == 5)
			ol_dist_info = s_dist_05;
		else if(d_id == 6)
			ol_dist_info = s_dist_06;
		else if(d_id == 7)
			ol_dist_info = s_dist_07;
		else if(d_id == 8)
			ol_dist_info = s_dist_08;
		else if(d_id == 9)
			ol_dist_info = s_dist_09;
		else if(d_id == 10)
			ol_dist_info = s_dist_10;

		String stmtInsertOrderLineSQL = 
				"INSERT INTO " + "ORDER_LINE" + 
				" (OL_O_ID, OL_D_ID, OL_W_ID, OL_NUMBER, OL_I_ID, OL_DELIVERY_D, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DIST_INFO) " +
				" VALUES (?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement stmtInsertOrderLine = connect.prepareStatement(stmtInsertOrderLineSQL);
		stmtInsertOrderLine.setInt(1, o_id);
		stmtInsertOrderLine.setInt(2, d_id);
		stmtInsertOrderLine.setInt(3, w_id);
		stmtInsertOrderLine.setInt(4, ol_number);
		stmtInsertOrderLine.setInt(5, ol_i_id);
		stmtInsertOrderLine.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
		stmtInsertOrderLine.setInt(7, ol_supply_w_id);
		stmtInsertOrderLine.setInt(8, ol_quantity);
		stmtInsertOrderLine.setDouble(9, ol_amount);
		stmtInsertOrderLine.setString(10, ol_dist_info);
		stmtInsertOrderLine.executeUpdate();
	}

	//
	// OrderStatus
	//
	@ChoppedTransaction(originalTransaction="orderStatus", microservice="m2")
	public void orderStatus_customer_ops(int d_id, int w_id, int c_id, int y) throws SQLException {
		String customerByNameSQL = 
			"SELECT C_FIRST, C_MIDDLE, C_ID, C_STREET_1, C_STREET_2, C_CITY, " + 
			"       C_STATE, C_ZIP, C_PHONE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, " +
			"       C_BALANCE, C_YTD_PAYMENT, C_PAYMENT_CNT, C_SINCE " +
			"  FROM " + "CUSTOMER" + 
			" WHERE C_W_ID = ? " +
			"   AND C_D_ID = ? " +
			"   AND C_LAST = ? ";

		String payGetCustSQL = 
			"SELECT C_FIRST, C_MIDDLE, C_LAST, C_STREET_1, C_STREET_2, " + 
			"       C_CITY, C_STATE, C_ZIP, C_PHONE, C_CREDIT, C_CREDIT_LIM, " + 
			"       C_DISCOUNT, C_BALANCE, C_YTD_PAYMENT, C_PAYMENT_CNT, C_SINCE " +
			"  FROM " + "CUSTOMER" + 
			" WHERE C_W_ID = ? " +
			"   AND C_D_ID = ? " +
			"   AND C_ID = ?";

		PreparedStatement customerByName = connect.prepareStatement(customerByNameSQL);
		PreparedStatement payGetCust = connect.prepareStatement(payGetCustSQL);

		boolean c_by_name = false;
		String c_last = null;
		if (y <= 60) {
            c_by_name = true;
			String[] nameTokens = { "BAR", "OUGHT", "ABLE", "PRI",
			"PRES", "ESE", "ANTI", "CALLY", "ATION", "EING" };
            int num = r.nextInt(1000);
			c_last = nameTokens[num / 100] + nameTokens[(num / 10) % 10] + nameTokens[num % 10];
        } else {
            c_by_name = false;
            c_id = r.nextInt(3000) + 1;
        }

        int o_id = -1, o_carrier_id = -1;
        ArrayList<String> orderLines = new ArrayList<String>();

        Customer c;
        if (c_by_name) {
            assert c_id <= 0;
			ArrayList<Customer> customers = new ArrayList<Customer>();

			customerByName.setInt(1, w_id);
			customerByName.setInt(2, d_id);
			customerByName.setString(3, c_last);
			ResultSet rs = customerByName.executeQuery();

			while (rs.next()) {
				c = new Customer();
				c.c_first = rs.getString("c_first");
				c.c_middle = rs.getString("c_middle");
				c.c_street_1 = rs.getString("c_street_1");
				c.c_street_2 = rs.getString("c_street_2");
				c.c_city = rs.getString("c_city");
				c.c_state = rs.getString("c_state");
				c.c_zip = rs.getString("c_zip");
				c.c_phone = rs.getString("c_phone");
				c.c_credit = rs.getString("c_credit");
				c.c_credit_lim = rs.getDouble("c_credit_lim");
				c.c_discount = rs.getDouble("c_discount");
				c.c_balance = rs.getDouble("c_balance");
				c.c_ytd_payment = rs.getDouble("c_ytd_payment");
				c.c_payment_cnt = rs.getInt("c_payment_cnt");
				c.c_since = rs.getTimestamp("c_since");

				c.c_id = rs.getInt("C_ID");
				c.c_last = c_last;
				customers.add(c);
			}
			rs.close();

			if (customers.size() == 0) {
				System.out.println("No customers!");
			}

			// TPC-C 2.5.2.2: Position n / 2 rounded up to the next integer, but
			// that counts starting from 1.
			int index = customers.size() / 2;
			if (customers.size() % 2 == 0) {
				index -= 1;
			}
			c = customers.get(index);
        } else {
            assert c_last == null;
            payGetCust.setInt(1, w_id);
			payGetCust.setInt(2, d_id);
			payGetCust.setInt(3, c_id);
			ResultSet rs = payGetCust.executeQuery();
			if (!rs.next()) {
				System.out.println("Empty!");
			}

			c = new Customer();
			c.c_first = rs.getString("c_first");
			c.c_middle = rs.getString("c_middle");
			c.c_street_1 = rs.getString("c_street_1");
			c.c_street_2 = rs.getString("c_street_2");
			c.c_city = rs.getString("c_city");
			c.c_state = rs.getString("c_state");
			c.c_zip = rs.getString("c_zip");
			c.c_phone = rs.getString("c_phone");
			c.c_credit = rs.getString("c_credit");
			c.c_credit_lim = rs.getDouble("c_credit_lim");
			c.c_discount = rs.getDouble("c_discount");
			c.c_balance = rs.getDouble("c_balance");
			c.c_ytd_payment = rs.getDouble("c_ytd_payment");
			c.c_payment_cnt = rs.getInt("c_payment_cnt");
			c.c_since = rs.getTimestamp("c_since");

			c.c_id = c_id;
			c.c_last = rs.getString("C_LAST");
			rs.close();
        }
	}

	@ChoppedTransaction(originalTransaction="orderStatus", microservice="m1")
	public void orderStatus_o_order_ops(int d_id, int w_id, int c_id, int y) throws SQLException {
		String ordStatGetNewestOrdSQL = 
			"SELECT O_ID, O_CARRIER_ID, O_ENTRY_D " +
			"  FROM " + "OORDER" + 
			" WHERE O_W_ID = ? " + 
			"   AND O_D_ID = ? " + 
			"   AND O_C_ID = ? " +
			" ORDER BY O_ID DESC LIMIT 1";
		PreparedStatement ordStatGetNewestOrd = connect.prepareStatement(ordStatGetNewestOrdSQL);			
        ordStatGetNewestOrd.setInt(1, w_id);
        ordStatGetNewestOrd.setInt(2, d_id);
        ordStatGetNewestOrd.setInt(3, c_id);
        ResultSet rs = ordStatGetNewestOrd.executeQuery();
        if (!rs.next()) {
			System.out.println("Empty!");
        }
        int o_id = rs.getInt("O_ID");
        int o_carrier_id = rs.getInt("O_CARRIER_ID");
        Timestamp o_entry_d = rs.getTimestamp("O_ENTRY_D");
        rs.close();
	}

	@ChoppedTransaction(originalTransaction="orderStatus", microservice="m3")
	public void orderStatus_order_line_ops(int o_id, int d_id, int w_id, int c_id, int y) throws SQLException {
		String ordStatGetOrderLinesSQL = 
			"SELECT OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DELIVERY_D " + 
			"  FROM " + "ORDER_LINE" + 
			" WHERE OL_O_ID = ?" + 
			"   AND OL_D_ID = ?" + 
			"   AND OL_W_ID = ?";
		PreparedStatement ordStatGetOrderLines = connect.prepareStatement(ordStatGetOrderLinesSQL);		
        ordStatGetOrderLines.setInt(1, o_id);
        ordStatGetOrderLines.setInt(2, d_id);
        ordStatGetOrderLines.setInt(3, w_id);
        ResultSet rs = ordStatGetOrderLines.executeQuery();
        ArrayList<String> orderLines = new ArrayList<String>();
		while (rs.next()) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append(rs.getLong("OL_SUPPLY_W_ID"));
            sb.append(" - ");
            sb.append(rs.getLong("OL_I_ID"));
            sb.append(" - ");
            sb.append(rs.getLong("OL_QUANTITY"));
            sb.append(" - ");
			String dS = "" + rs.getDouble("OL_AMOUNT");
			dS = dS.length() > 6 ? dS.substring(0, 6) : dS;
            sb.append(dS);
            sb.append(" - ");
            if (rs.getTimestamp("OL_DELIVERY_D") != null)
                sb.append(rs.getTimestamp("OL_DELIVERY_D"));
            else
                sb.append("99-99-9999");
            sb.append("]");
            orderLines.add(sb.toString());
        }
        rs.close();
        rs = null;
	}

	//
	// Payment
	//
	@ChoppedTransaction(microservice="m2")
	public void payment(int w_id, int numWarehouses,
            int terminalDistrictLowerID, int terminalDistrictUpperID) throws SQLException {

		String payUpdateWhseSQL =
				"UPDATE " + "WAREHOUSE" + 
				"   SET W_YTD = ? " +
				" WHERE W_ID = ? ";
		
		String payGetWhseSQL = 
				"SELECT W_YTD, W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP, W_NAME" + 
				"  FROM " + "WAREHOUSE" + 
				" WHERE W_ID = ?";

		String payUpdateDistSQL =
				"UPDATE " + "DISTRICT" + 
				"   SET D_YTD = ? " +
				" WHERE D_W_ID = ? " +
				"   AND D_ID = ?";
		
		String payGetDistSQL = 
				"SELECT D_YTD, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP, D_NAME" + 
				"  FROM " + "DISTRICT" + 
				" WHERE D_W_ID = ? " +
				"   AND D_ID = ?";
		
		String payGetCustSQL =
				"SELECT C_FIRST, C_MIDDLE, C_LAST, C_STREET_1, C_STREET_2, " + 
				"       C_CITY, C_STATE, C_ZIP, C_PHONE, C_CREDIT, C_CREDIT_LIM, " + 
				"       C_DISCOUNT, C_BALANCE, C_YTD_PAYMENT, C_PAYMENT_CNT, C_SINCE " +
				"  FROM " + "CUSTOMER" + 
				" WHERE C_W_ID = ? " +
				"   AND C_D_ID = ? " +
				"   AND C_ID = ?";
		
		String payGetCustCdataSQL =
				"SELECT C_DATA " +
				"  FROM " + "CUSTOMER" + 
				" WHERE C_W_ID = ? " +
				"   AND C_D_ID = ? " +
				"   AND C_ID = ?";
		
		String payUpdateCustBalCdataSQL =
				"UPDATE " + "CUSTOMER" + 
				"   SET C_BALANCE = ?, " +
				"       C_YTD_PAYMENT = ?, " + 
				"       C_PAYMENT_CNT = ?, " +
				"       C_DATA = ? " +
				" WHERE C_W_ID = ? " +
				"   AND C_D_ID = ? " + 
				"   AND C_ID = ?";
		
		String payUpdateCustBalSQL =
				"UPDATE " + "CUSTOMER" + 
				"   SET C_BALANCE = ?, " +
				"       C_YTD_PAYMENT = ?, " +
				"       C_PAYMENT_CNT = ? " +
				" WHERE C_W_ID = ? " + 
				"   AND C_D_ID = ? " + 
				"   AND C_ID = ?";
		
		String payInsertHistSQL =
				"INSERT INTO " + "HISTORY" + 
				" (H_C_D_ID, H_C_W_ID, H_C_ID, H_D_ID, H_W_ID, H_DATE, H_AMOUNT, H_DATA) " +
				" VALUES (?,?,?,?,?,?,?,?)";
		
		String customerByNameSQL =
				"SELECT C_FIRST, C_MIDDLE, C_ID, C_STREET_1, C_STREET_2, C_CITY, " + 
				"       C_STATE, C_ZIP, C_PHONE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, " +
				"       C_BALANCE, C_YTD_PAYMENT, C_PAYMENT_CNT, C_SINCE " +
				"  FROM " + "CUSTOMER" + 
				" WHERE C_W_ID = ? " +
				"   AND C_D_ID = ? " +
				"   AND C_LAST = ? " +
				" ORDER BY C_FIRST";

		PreparedStatement payUpdateWhse = connect.prepareStatement(payUpdateWhseSQL);
        PreparedStatement payGetWhse = connect.prepareStatement(payGetWhseSQL);
        PreparedStatement payUpdateDist = connect.prepareStatement(payUpdateDistSQL);
        PreparedStatement payGetDist = connect.prepareStatement(payGetDistSQL);
        PreparedStatement payGetCust = connect.prepareStatement(payGetCustSQL);
        PreparedStatement payGetCustCdata = connect.prepareStatement(payGetCustCdataSQL);
        PreparedStatement payUpdateCustBalCdata = connect.prepareStatement(payUpdateCustBalCdataSQL);
        PreparedStatement payUpdateCustBal = connect.prepareStatement(payUpdateCustBalSQL);
        PreparedStatement payInsertHist = connect.prepareStatement(payInsertHistSQL);
        PreparedStatement customerByName = connect.prepareStatement(customerByNameSQL);

		int districtID = r.nextInt(terminalDistrictUpperID - terminalDistrictLowerID + 1) + terminalDistrictLowerID;
        int customerID = r.nextInt(3000) + 1;

        int x = r.nextInt(100) + 1;
        int customerDistrictID;
        int customerWarehouseID;
        if (x <= 85) {
            customerDistrictID = districtID;
            customerWarehouseID = w_id;
        } else {
            customerDistrictID = r.nextInt(10) + 1;
            do {
                customerWarehouseID = r.nextInt(numWarehouses) + 1;
            } while (customerWarehouseID == w_id && numWarehouses > 1);
        }

        long y = r.nextInt(100) + 1;
        boolean c_by_name;
        String customerLastName = null;
        customerID = -1;
        if (y <= 60) {
            // 60% lookups by last name
            c_by_name = true;
			String[] nameTokens = { "BAR", "OUGHT", "ABLE", "PRI",
			"PRES", "ESE", "ANTI", "CALLY", "ATION", "EING" };
            int num = r.nextInt(1000);
			customerLastName = nameTokens[num / 100] + nameTokens[(num / 10) % 10] + nameTokens[num % 10];
        } else {
            // 40% lookups by customer ID
            c_by_name = false;
            customerID = r.nextInt(3000) + 1;
        }

        double paymentAmount = (double) ((r.nextInt(500000-100+1) + 100) / 100.0);

		double current_w_ytd;
        String w_street_1, w_street_2, w_city, w_state, w_zip, w_name;
		double current_d_ytd;
        String d_street_1, d_street_2, d_city, d_state, d_zip, d_name;

		payGetWhse.setInt(1, w_id);
		ResultSet rs = payGetWhse.executeQuery();
		if (!rs.next())
            System.out.println("Empty!");
		current_w_ytd = rs.getDouble("W_YTD");
        w_street_1 = rs.getString("W_STREET_1");
        w_street_2 = rs.getString("W_STREET_2");
        w_city = rs.getString("W_CITY");
        w_state = rs.getString("W_STATE");
        w_zip = rs.getString("W_ZIP");
        w_name = rs.getString("W_NAME");
        rs.close();
        rs = null;

        payUpdateWhse.setDouble(1, current_w_ytd + paymentAmount);
        payUpdateWhse.setInt(2, w_id);
        // MySQL reports deadlocks due to lock upgrades:
        // t1: read w_id = x; t2: update w_id = x; t1 update w_id = x
        int result = payUpdateWhse.executeUpdate();
		
		payGetDist.setInt(1, w_id);
        payGetDist.setInt(2, districtID);
        rs = payGetDist.executeQuery();
        if (!rs.next())
            System.out.println("Not found!");
		current_d_ytd = rs.getDouble("D_YTD");
		d_street_1 = rs.getString("D_STREET_1");
        d_street_2 = rs.getString("D_STREET_2");
        d_city = rs.getString("D_CITY");
        d_state = rs.getString("D_STATE");
        d_zip = rs.getString("D_ZIP");
        d_name = rs.getString("D_NAME");
        rs.close();
        rs = null;

        payUpdateDist.setDouble(1, current_d_ytd + paymentAmount);
        payUpdateDist.setInt(2, w_id);
        payUpdateDist.setInt(3, districtID);
        result = payUpdateDist.executeUpdate();

        Customer c;
        if (c_by_name) {
            assert customerID <= 0;
			ArrayList<Customer> customers = new ArrayList<Customer>();

			customerByName.setInt(1, customerWarehouseID);
			customerByName.setInt(2, customerDistrictID);
			customerByName.setString(3, customerLastName);
			rs = customerByName.executeQuery();

			while (rs.next()) {
				c = new Customer();
				c.c_first = rs.getString("c_first");
				c.c_middle = rs.getString("c_middle");
				c.c_street_1 = rs.getString("c_street_1");
				c.c_street_2 = rs.getString("c_street_2");
				c.c_city = rs.getString("c_city");
				c.c_state = rs.getString("c_state");
				c.c_zip = rs.getString("c_zip");
				c.c_phone = rs.getString("c_phone");
				c.c_credit = rs.getString("c_credit");
				c.c_credit_lim = rs.getDouble("c_credit_lim");
				c.c_discount = rs.getDouble("c_discount");
				c.c_balance = rs.getDouble("c_balance");
				c.c_ytd_payment = rs.getDouble("c_ytd_payment");
				c.c_payment_cnt = rs.getInt("c_payment_cnt");
				c.c_since = rs.getTimestamp("c_since");

				c.c_id = rs.getInt("C_ID");
				c.c_last = customerLastName;
				customers.add(c);
			}
			rs.close();

			if (customers.size() == 0)
				System.out.println("No customers!");

			// TPC-C 2.5.2.2: Position n / 2 rounded up to the next integer, but
			// that counts starting from 1.
			int index = customers.size() / 2;
			if (customers.size() % 2 == 0) {
				index -= 1;
			}
			c = customers.get(index);
        } else {
            assert customerLastName == null;
			payGetCust.setInt(1, customerWarehouseID);
			payGetCust.setInt(2, customerDistrictID);
			payGetCust.setInt(3, customerID);
			rs = payGetCust.executeQuery();
			if (!rs.next())
				System.out.println("Not found!");

			c = new Customer();
			c.c_first = rs.getString("c_first");
			c.c_middle = rs.getString("c_middle");
			c.c_street_1 = rs.getString("c_street_1");
			c.c_street_2 = rs.getString("c_street_2");
			c.c_city = rs.getString("c_city");
			c.c_state = rs.getString("c_state");
			c.c_zip = rs.getString("c_zip");
			c.c_phone = rs.getString("c_phone");
			c.c_credit = rs.getString("c_credit");
			c.c_credit_lim = rs.getDouble("c_credit_lim");
			c.c_discount = rs.getDouble("c_discount");
			c.c_balance = rs.getDouble("c_balance");
			c.c_ytd_payment = rs.getDouble("c_ytd_payment");
			c.c_payment_cnt = rs.getInt("c_payment_cnt");
			c.c_since = rs.getTimestamp("c_since");

			c.c_id = customerID;
			c.c_last = rs.getString("C_LAST");
			rs.close();
        }

        c.c_balance -= paymentAmount;
        c.c_ytd_payment += paymentAmount;
        c.c_payment_cnt += 1;
        String c_data = null;
        if (c.c_credit.equals("BC")) { // bad credit
            payGetCustCdata.setInt(1, customerWarehouseID);
            payGetCustCdata.setInt(2, customerDistrictID);
            payGetCustCdata.setInt(3, c.c_id);
            rs = payGetCustCdata.executeQuery();
            if (!rs.next())
                System.out.println("Not found!");
            c_data = rs.getString("C_DATA");
            rs.close();
            rs = null;

            c_data = c.c_id + " " + customerDistrictID + " " + customerWarehouseID + " " + districtID + " " + w_id + " " + paymentAmount + " | " + c_data;
            if (c_data.length() > 500)
                c_data = c_data.substring(0, 500);

            payUpdateCustBalCdata.setDouble(1, c.c_balance);
            payUpdateCustBalCdata.setDouble(2, c.c_ytd_payment);
            payUpdateCustBalCdata.setInt(3, c.c_payment_cnt);
            payUpdateCustBalCdata.setString(4, c_data);
            payUpdateCustBalCdata.setInt(5, customerWarehouseID);
            payUpdateCustBalCdata.setInt(6, customerDistrictID);
            payUpdateCustBalCdata.setInt(7, c.c_id);
            result = payUpdateCustBalCdata.executeUpdate();

        } else { // GoodCredit

            payUpdateCustBal.setDouble(1, c.c_balance);
            payUpdateCustBal.setDouble(2, c.c_ytd_payment);
            payUpdateCustBal.setInt(3, c.c_payment_cnt);
            payUpdateCustBal.setInt(4, customerWarehouseID);
            payUpdateCustBal.setInt(5, customerDistrictID);
            payUpdateCustBal.setInt(6, c.c_id);
            result = payUpdateCustBal.executeUpdate();
        }

        if (w_name.length() > 10)
            w_name = w_name.substring(0, 10);
        if (d_name.length() > 10)
            d_name = d_name.substring(0, 10);
        String h_data = w_name + "    " + d_name;

        payInsertHist.setInt(1, customerDistrictID);
        payInsertHist.setInt(2, customerWarehouseID);
        payInsertHist.setInt(3, c.c_id);
        payInsertHist.setInt(4, districtID);
        payInsertHist.setInt(5, w_id);
        payInsertHist.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        payInsertHist.setDouble(7, paymentAmount);
        payInsertHist.setString(8, h_data);
        payInsertHist.executeUpdate();
	}

	//
	// StockLevel
	//
	@ChoppedTransaction(originalTransaction="stockLevel", microservice="m2")
	public void stockLevel_district_ops(int w_id, int terminalDistrictLowerID,
			int terminalDistrictUpperID) throws SQLException {
		String stockGetDistOrderIdSQL =
			"SELECT D_NEXT_O_ID " + 
			"  FROM " + "DISTRICT" +
			" WHERE D_W_ID = ? " +
			"   AND D_ID = ?";
		PreparedStatement stockGetDistOrderId = connect.prepareStatement(stockGetDistOrderIdSQL);
		int d_id = r.nextInt(terminalDistrictUpperID - terminalDistrictLowerID + 1) + terminalDistrictLowerID;
		stockGetDistOrderId.setInt(1, w_id);
		stockGetDistOrderId.setInt(2, d_id);
		ResultSet rs = stockGetDistOrderId.executeQuery();
		if (!rs.next()) {
			System.out.println("Not found!");
		}
		int o_id = rs.getInt("D_NEXT_O_ID");
		rs.close();
	}

	@ChoppedTransaction(originalTransaction="stockLevel", microservice="m3")
	public void stockLevel_order_line_ops(int w_id, int d_id, int o_id) throws SQLException {
		String stockGetOLIdSQL =
			"SELECT OL_I_ID " +
			" FROM " + "ORDER_LINE" +
			" WHERE OL_W_ID = ?" +
			" AND OL_D_ID = ?" +
			" AND OL_O_ID < ?" +
			" AND OL_O_ID >= ?";
		
		String stockGetCountStockSQL =
			"SELECT S_I_ID " +
			" FROM " + "STOCK" +
			" WHERE S_W_ID = ?" +
			" AND S_I_ID = ?" + 
			" AND S_QUANTITY < ?";
		PreparedStatement stockGetOLId = connect.prepareStatement(stockGetOLIdSQL);
		stockGetOLId.setInt(1, w_id);
		stockGetOLId.setInt(2, d_id);
		stockGetOLId.setInt(3, o_id);
		stockGetOLId.setInt(4, o_id - 20);
		ResultSet rs = stockGetOLId.executeQuery();

		int threshold = r.nextInt(11) + 10; // 10-20
		int stock_count = 0;
		
		PreparedStatement stockGetCountStock = connect.prepareStatement(stockGetCountStockSQL);
		ResultSet rs1;
		int current_ol_i_id;
		ArrayList<Integer> seen_s_i_ids = new ArrayList<Integer>();
		while(rs.next()) {
			current_ol_i_id = rs.getInt("OL_I_ID");
			stockGetCountStock.setInt(1, w_id);
			stockGetCountStock.setInt(2, current_ol_i_id);
			stockGetCountStock.setInt(3, threshold);
			rs1 = stockGetCountStock.executeQuery();
			if (!rs1.next()) {
				System.out.println("Failed to get stock level!");
			}
			int current_s_i_id = rs1.getInt("S_I_ID");
			if(!seen_s_i_ids.contains(current_s_i_id)) {
				seen_s_i_ids.add(current_s_i_id);
				stock_count += 1;
			}
			rs1.close();
		}
		rs.close();
	}
}
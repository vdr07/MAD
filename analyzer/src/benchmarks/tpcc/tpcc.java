package benchmarks.tpcc;

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

public class tpcc {

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

	public tpcc(int id) {
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

	public void delivery(int d_id, int w_id, int C_DELIVERY_CNT, int C_BALANCE) throws SQLException {
		String delivGetOrderIdSQL = 
				"SELECT NO_O_ID FROM " + "NEW_ORDER" + 
				" WHERE NO_D_ID = ? " +
				"   AND NO_W_ID = ? " +
				" ORDER BY NO_O_ID ASC " +
				" LIMIT 1";
		
		String delivDeleteNewOrderSQL = 
				"DELETE FROM " + "NEW_ORDER" +
				" WHERE NO_O_ID = ? " +
				"   AND NO_D_ID = ?" +
				"   AND NO_W_ID = ?";
		
		String delivGetCustIdSQL = 
				"SELECT O_C_ID FROM " + "OORDER" + 
				" WHERE O_ID = ? " +
				"   AND O_D_ID = ? " +
				"   AND O_W_ID = ?";
		
		String delivUpdateCarrierIdSQL = 
				"UPDATE " + "OORDER" + 
				"   SET O_CARRIER_ID = ? " +
				" WHERE O_ID = ? " +
				"   AND O_D_ID = ?" +
				"   AND O_W_ID = ?";
		
		String delivUpdateDeliveryDateSQL = 
				"UPDATE " + "ORDER_LINE" +
				"   SET OL_DELIVERY_D = ? " +
				" WHERE OL_O_ID = ? " +
				"   AND OL_D_ID = ? " +
				"   AND OL_W_ID = ? ";
		
		String delivSumOrderAmountSQL = 
				"SELECT OL_AMOUNT FROM " + "ORDER_LINE" + 
				" WHERE OL_O_ID = ? " +
				"   AND OL_D_ID = ? " +
				"   AND OL_W_ID = ?";
		
		String delivGetCustBalDelivCntSQL = 
				"SELECT C_BALANCE, C_DELIVERY_CNT FROM " + "CUSTOMER" + 
				" WHERE C_W_ID = ? " +
				"   AND C_D_ID = ? " +
				"   AND C_ID = ?";

		String delivUpdateCustBalDelivCntSQL = 
				"UPDATE " + "CUSTOMER" +
				"   SET C_BALANCE = ?," +
				"       C_DELIVERY_CNT = ? " +
				" WHERE C_W_ID = ? " +
				"   AND C_D_ID = ? " +
				"   AND C_ID = ? ";

		PreparedStatement delivGetOrderId = connect.prepareStatement(delivGetOrderIdSQL);
		PreparedStatement delivDeleteNewOrder =  connect.prepareStatement(delivDeleteNewOrderSQL);
		PreparedStatement delivGetCustId = connect.prepareStatement(delivGetCustIdSQL);
		PreparedStatement delivUpdateCarrierId = connect.prepareStatement(delivUpdateCarrierIdSQL);
		PreparedStatement delivUpdateDeliveryDate = connect.prepareStatement(delivUpdateDeliveryDateSQL);
		PreparedStatement delivSumOrderAmount = connect.prepareStatement(delivSumOrderAmountSQL);
		PreparedStatement delivGetCustBalDelivCnt = connect.prepareStatement(delivGetCustBalDelivCntSQL);
		PreparedStatement delivUpdateCustBalDelivCnt = connect.prepareStatement(delivUpdateCustBalDelivCntSQL);
	
		delivGetOrderId.setInt(1, d_id);
		delivGetOrderId.setInt(2, w_id);
		ResultSet rs = delivGetOrderId.executeQuery();
		rs.next();
		int no_o_id = rs.getInt("NO_O_ID");

		delivDeleteNewOrder.setInt(1, no_o_id);
		delivDeleteNewOrder.setInt(2, d_id);
		delivDeleteNewOrder.setInt(3, w_id);
		int result = delivDeleteNewOrder.executeUpdate();

		delivGetCustId.setInt(1, no_o_id);
		delivGetCustId.setInt(2, d_id);
		delivGetCustId.setInt(3, w_id);
		ResultSet customer = delivGetCustId.executeQuery();
		customer.next();
		int c_id = customer.getInt("O_C_ID");
		
		int o_carrier_id = r.nextInt(10) + 1; // 1-10
		delivUpdateCarrierId.setInt(1, o_carrier_id);
		delivUpdateCarrierId.setInt(2, no_o_id);
		delivUpdateCarrierId.setInt(3, d_id);
		delivUpdateCarrierId.setInt(4, w_id);
		result = delivUpdateCarrierId.executeUpdate();

		delivUpdateDeliveryDate.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		delivUpdateDeliveryDate.setInt(2, no_o_id);
		delivUpdateDeliveryDate.setInt(3, d_id);
		delivUpdateDeliveryDate.setInt(4, w_id);
		result = delivUpdateDeliveryDate.executeUpdate();

		delivSumOrderAmount.setInt(1, no_o_id);
		delivSumOrderAmount.setInt(2, d_id);
		delivSumOrderAmount.setInt(3, w_id);
		ResultSet orderAmount = delivSumOrderAmount.executeQuery();
		orderAmount.next();
		double ol_total = orderAmount.getDouble("OL_AMOUNT");
		while(orderAmount.next()) {
			ol_total += orderAmount.getDouble("OL_AMOUNT");
		}	
		
		delivGetCustBalDelivCnt.setInt(1, w_id);
		delivGetCustBalDelivCnt.setInt(2, d_id);
		delivGetCustBalDelivCnt.setInt(3, c_id);
		ResultSet custBalDelivCnt = delivGetCustBalDelivCnt.executeQuery();
		custBalDelivCnt.next();
		double c_balance = custBalDelivCnt.getDouble("C_BALANCE");
		int c_delivery_cnt = custBalDelivCnt.getInt("C_DELIVERY_CNT");

		delivUpdateCustBalDelivCnt.setDouble(1, c_balance + ol_total);
		delivUpdateCustBalDelivCnt.setInt(2, c_delivery_cnt + 1);
		delivUpdateCustBalDelivCnt.setInt(3, w_id);
		delivUpdateCustBalDelivCnt.setInt(4, d_id);
		delivUpdateCustBalDelivCnt.setInt(5, c_id);

		result = delivUpdateCustBalDelivCnt.executeUpdate();
	}

	public void newOrder(int terminalWarehouseID, int numWarehouses,
			int terminalDistrictLowerID, int terminalDistrictUpperID,
			int w_id, int d_id, int c_id,
			int o_ol_cnt, int o_all_local) throws SQLException {
		String stmtGetCustSQL = 
				"SELECT C_DISCOUNT, C_LAST, C_CREDIT" +
				"  FROM " + "CUSTOMER" + 
				" WHERE C_W_ID = ? " + 
				"   AND C_D_ID = ? " +
				"   AND C_ID = ?";

		String stmtGetWhseSQL = 
				"SELECT W_TAX " + 
				"  FROM " + "WAREHOUSE" + 
				" WHERE W_ID = ?";
		
		String stmtGetDistSQL = 
				"SELECT D_NEXT_O_ID, D_TAX " +
				"  FROM " + "DISTRICT" +
				" WHERE D_W_ID = ? AND D_ID = ?";

		String stmtInsertNewOrderSQL = 
				"INSERT INTO " + "NEW_ORDER" +
				" (NO_O_ID, NO_D_ID, NO_W_ID) " +
				" VALUES ( ?, ?, ?)";

		String stmtUpdateDistSQL = 
				"UPDATE " + "DISTRICT" + 
				"   SET D_NEXT_O_ID = ? " +
				" WHERE D_W_ID = ? " +
				"   AND D_ID = ?";

		String stmtInsertOOrderSQL = 
				"INSERT INTO " + "OORDER" + 
				" (O_ID, O_D_ID, O_W_ID, O_C_ID, O_CARRIER_ID, O_ENTRY_D, O_OL_CNT, O_ALL_LOCAL)" + 
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		String stmtGetItemSQL = 
				"SELECT I_PRICE, I_NAME , I_DATA " +
				"  FROM " + "ITEM" + 
				" WHERE I_ID = ?";

		String stmtGetStockSQL = 
				"SELECT S_QUANTITY, S_YTD, S_ORDER_CNT, S_REMOTE_CNT, S_DATA, S_DIST_01, S_DIST_02, S_DIST_03, S_DIST_04, S_DIST_05, S_DIST_06, S_DIST_07, S_DIST_08, S_DIST_09, S_DIST_10 " +
				"  FROM " + "STOCK" + 
				" WHERE S_I_ID = ? " +
				"   AND S_W_ID = ?";

		String stmtUpdateStockSQL = 
				"UPDATE " + "STOCK" + 
				"   SET S_QUANTITY = ?, " +
				"       S_YTD = ?, " + 
				"       S_ORDER_CNT = ?, " +
				"       S_REMOTE_CNT = ? " +
				" WHERE S_I_ID = ? " +
				"   AND S_W_ID = ?";

		String stmtInsertOrderLineSQL = 
				"INSERT INTO " + "ORDER_LINE" + 
				" (OL_W_ID, OL_D_ID, OL_O_ID, OL_NUMBER, OL_I_ID, OL_DELIVERY_DM, OL_AMOUNT, OL_SUPPLY_W_ID, OL_QUANTITY, OL_DIST_INFO) " +
				" VALUES (?,?,?,?,?,?,?,?,?,?)";

		PreparedStatement stmtGetCust = connect.prepareStatement(stmtGetCustSQL);
		PreparedStatement stmtGetWhse = connect.prepareStatement(stmtGetWhseSQL);
		PreparedStatement stmtGetDist = connect.prepareStatement(stmtGetDistSQL);
		PreparedStatement stmtInsertNewOrder = connect.prepareStatement(stmtInsertNewOrderSQL);
		PreparedStatement stmtUpdateDist = connect.prepareStatement(stmtUpdateDistSQL);
		PreparedStatement stmtInsertOOrder = connect.prepareStatement(stmtInsertOOrderSQL);
		PreparedStatement stmtGetItem = connect.prepareStatement(stmtGetItemSQL);
		PreparedStatement stmtGetStock = connect.prepareStatement(stmtGetStockSQL);
		PreparedStatement stmtUpdateStock = connect.prepareStatement(stmtUpdateStockSQL);
		PreparedStatement stmtInsertOrderLine = connect.prepareStatement(stmtInsertOrderLineSQL);

		int districtID = r.nextInt(terminalDistrictUpperID - terminalDistrictLowerID + 1) + terminalDistrictLowerID;
		int customerID = r.nextInt(3000) + 1; // 1-3000
		
		int numItems = r.nextInt(10) + 5; // 5-15
		int[] itemIDs = new int[numItems];
		int[] supplierWarehouseIDs = new int[numItems];
		int[] orderQuantities = new int[numItems];
		int allLocal = 1;

		for (int i = 0; i < numItems; i++) {
			itemIDs[i] = r.nextInt(100000) + 1; // 1-100000
			if (r.nextInt(100) + 1 > 1) {
				supplierWarehouseIDs[i] = terminalWarehouseID;
			} else {
				do {
					supplierWarehouseIDs[i] = r.nextInt(numWarehouses) + 1;
				} while (supplierWarehouseIDs[i] == terminalWarehouseID
						&& numWarehouses > 1);
				allLocal = 0;
			}
			orderQuantities[i] = r.nextInt(10) + 1; // 1-10
		}
		
		double c_discount, w_tax, d_tax = 0, i_price;
		int d_next_o_id, o_id = -1, s_quantity;
		String c_last = null, c_credit = null, i_name, i_data, s_data;
		String s_dist_01, s_dist_02, s_dist_03, s_dist_04, s_dist_05;
		String s_dist_06, s_dist_07, s_dist_08, s_dist_09, s_dist_10, ol_dist_info;
		double[] itemPrices = new double[o_ol_cnt];
		double[] orderLineAmounts = new double[o_ol_cnt];
		String[] itemNames = new String[o_ol_cnt];
		int[] stockQuantities = new int[o_ol_cnt];
		char[] brandGeneric = new char[o_ol_cnt];
		int ol_supply_w_id, ol_i_id, ol_quantity;
		int s_remote_cnt_increment;
		double ol_amount, total_amount = 0;
		double s_ytd;
		int s_order_cnt, s_remote_cnt;
		
		stmtGetCust.setInt(1, w_id);
		stmtGetCust.setInt(2, d_id);
		stmtGetCust.setInt(3, c_id);
		ResultSet rs = stmtGetCust.executeQuery();
		rs.next();
		c_discount = rs.getDouble("C_DISCOUNT");
		c_last = rs.getString("C_LAST");
		c_credit = rs.getString("C_CREDIT");

		stmtGetWhse.setInt(1, w_id);
		ResultSet whse = stmtGetWhse.executeQuery();
		whse.next();
		w_tax = whse.getDouble("W_TAX");

		stmtGetDist.setInt(1, w_id);
		stmtGetDist.setInt(2, d_id);
		ResultSet dist = stmtGetDist.executeQuery();
		dist.next();
		d_next_o_id = dist.getInt("D_NEXT_O_ID");
		d_tax = dist.getDouble("D_TAX");

		stmtUpdateDist.setInt(1, d_next_o_id + 1);
		stmtUpdateDist.setInt(2, w_id);
		stmtUpdateDist.setInt(3, d_id);
		int result = stmtUpdateDist.executeUpdate();

		o_id = d_next_o_id;

		stmtInsertOOrder.setInt(1, o_id);
		stmtInsertOOrder.setInt(2, d_id);
		stmtInsertOOrder.setInt(3, w_id);
		stmtInsertOOrder.setInt(4, c_id);
		stmtInsertOOrder.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
		stmtInsertOOrder.setInt(7, o_ol_cnt);
		stmtInsertOOrder.setInt(8, o_all_local);
		stmtInsertOOrder.executeUpdate();

		stmtInsertNewOrder.setInt(1, o_id);
		stmtInsertNewOrder.setInt(2, d_id);
		stmtInsertNewOrder.setInt(3, w_id);
		stmtInsertNewOrder.executeUpdate();

		for (int ol_number = 1; ol_number <= o_ol_cnt; ol_number++) {
			ol_supply_w_id = supplierWarehouseIDs[ol_number - 1];
			ol_i_id = itemIDs[ol_number - 1];
			ol_quantity = orderQuantities[ol_number - 1];
			stmtGetItem.setInt(1, ol_i_id);
			ResultSet item = stmtGetItem.executeQuery();
			if (!item.next()) {
				// This is (hopefully) an expected error: this is an
				// expected new order rollback
				assert ol_number == o_ol_cnt;
				assert ol_i_id == -12345;
			}

			i_price = item.getDouble("I_PRICE");
			i_name = item.getString("I_NAME");
			i_data = item.getString("I_DATA");

			itemPrices[ol_number - 1] = i_price;
			itemNames[ol_number - 1] = i_name;


			stmtGetStock.setInt(1, ol_i_id);
			stmtGetStock.setInt(2, ol_supply_w_id);
			ResultSet stock = stmtGetStock.executeQuery();
			stock.next();
			s_quantity = stock.getInt("S_QUANTITY");
			s_ytd = stock.getDouble("S_YTD");
			s_order_cnt = stock.getInt("S_ORDER_CNT");
			s_remote_cnt = stock.getInt("S_REMOTE_CNT");
			s_data = stock.getString("S_DATA");
			s_dist_01 = stock.getString("S_DIST_01");
			s_dist_02 = stock.getString("S_DIST_02");
			s_dist_03 = stock.getString("S_DIST_03");
			s_dist_04 = stock.getString("S_DIST_04");
			s_dist_05 = stock.getString("S_DIST_05");
			s_dist_06 = stock.getString("S_DIST_06");
			s_dist_07 = stock.getString("S_DIST_07");
			s_dist_08 = stock.getString("S_DIST_08");
			s_dist_09 = stock.getString("S_DIST_09");
			s_dist_10 = stock.getString("S_DIST_10");

			stockQuantities[ol_number - 1] = s_quantity;

			if (s_quantity - ol_quantity >= 10) {
				s_quantity -= ol_quantity;
			} else {
				s_quantity += -ol_quantity + 91;
			}

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

			ol_amount = ol_quantity * i_price;
			orderLineAmounts[ol_number - 1] = ol_amount;
			total_amount += ol_amount;

			if (i_data.indexOf("ORIGINAL") != -1
					&& s_data.indexOf("ORIGINAL") != -1) {
				brandGeneric[ol_number - 1] = 'B';
			} else {
				brandGeneric[ol_number - 1] = 'G';
			}

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
			else
				ol_dist_info = "";
			

			stmtInsertOrderLine.setInt(1, w_id);
			stmtInsertOrderLine.setInt(2, d_id);
			stmtInsertOrderLine.setInt(3, o_id);
			stmtInsertOrderLine.setInt(4, ol_number);
			stmtInsertOrderLine.setInt(5, ol_i_id);
			stmtInsertOrderLine.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			stmtInsertOrderLine.setDouble(7, ol_amount);
			stmtInsertOrderLine.setInt(8, ol_supply_w_id);
			stmtInsertOrderLine.setInt(9, ol_quantity);
			stmtInsertOrderLine.setString(10, ol_dist_info);
			stmtInsertOrderLine.executeUpdate();

		} // end-for

		total_amount *= (1 + w_tax + d_tax) * (1 - c_discount);
	}

	public void orderStatus(int w_id, int terminalDistrictLowerID,
			int terminalDistrictUpperID) throws SQLException {
		String ordStatGetNewestOrdSQL = 
				"SELECT O_ID, O_CARRIER_ID, O_ENTRY_D " +
				"  FROM " + "OORDER" + 
				" WHERE O_W_ID = ? " + 
				"   AND O_D_ID = ? " + 
				"   AND O_C_ID = ? " +
				" ORDER BY O_ID DESC LIMIT 1";

		String ordStatGetOrderLinesSQL = 
				"SELECT OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DELIVERY_D " + 
				"  FROM " + "ORDER_LINE" + 
				" WHERE OL_O_ID = ?" + 
				"   AND OL_D_ID = ?" + 
				"   AND OL_W_ID = ?";

		String payGetCustSQL = 
				"SELECT C_FIRST, C_MIDDLE, C_LAST, C_STREET_1, C_STREET_2, " + 
				"       C_CITY, C_STATE, C_ZIP, C_PHONE, C_CREDIT, C_CREDIT_LIM, " + 
				"       C_DISCOUNT, C_BALANCE, C_YTD_PAYMENT, C_PAYMENT_CNT, C_SINCE " +
				"  FROM " + "CUSTOMER" + 
				" WHERE C_W_ID = ? " +
				"   AND C_D_ID = ? " +
				"   AND C_ID = ?";

		String customerByNameSQL = 
				"SELECT C_FIRST, C_MIDDLE, C_ID, C_STREET_1, C_STREET_2, C_CITY, " + 
				"       C_STATE, C_ZIP, C_PHONE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, " +
				"       C_BALANCE, C_YTD_PAYMENT, C_PAYMENT_CNT, C_SINCE " +
				"  FROM " + "CUSTOMER" + 
				" WHERE C_W_ID = ? " +
				"   AND C_D_ID = ? " +
				"   AND C_LAST = ? ";

		PreparedStatement ordStatGetNewestOrd = connect.prepareStatement(ordStatGetNewestOrdSQL);
		PreparedStatement ordStatGetOrderLines = connect.prepareStatement(ordStatGetOrderLinesSQL);
		PreparedStatement payGetCust = connect.prepareStatement(payGetCustSQL);
		PreparedStatement customerByName = connect.prepareStatement(customerByNameSQL);
	
		int d_id = r.nextInt(terminalDistrictUpperID - terminalDistrictLowerID + 1) + terminalDistrictLowerID;
		boolean c_by_name = false;
        int y = r.nextInt(100) + 1;
        String c_last = null;
        int c_id = -1;
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
        Timestamp o_entry_d;
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
			rs.next();

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
        }

        ordStatGetNewestOrd.setInt(1, w_id);
        ordStatGetNewestOrd.setInt(2, d_id);
        ordStatGetNewestOrd.setInt(3, c_id);
        ResultSet newestOrder = ordStatGetNewestOrd.executeQuery();
        newestOrder.next();
        o_id = newestOrder.getInt("O_ID");
        o_carrier_id = newestOrder.getInt("O_CARRIER_ID");
        o_entry_d = newestOrder.getTimestamp("O_ENTRY_D");

        // retrieve the order lines for the most recent order
        ordStatGetOrderLines.setInt(1, o_id);
        ordStatGetOrderLines.setInt(2, d_id);
        ordStatGetOrderLines.setInt(3, w_id);
        ResultSet orderLinesRS = ordStatGetOrderLines.executeQuery();
        while (orderLinesRS.next()) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append(orderLinesRS.getInt("OL_SUPPLY_W_ID"));
            sb.append(" - ");
            sb.append(orderLinesRS.getInt("OL_I_ID"));
            sb.append(" - ");
            sb.append(orderLinesRS.getInt("OL_QUANTITY"));
            sb.append(" - ");
			String dS = "" + orderLinesRS.getDouble("OL_AMOUNT");
			dS = dS.length() > 6 ? dS.substring(0, 6) : dS;
            sb.append(dS);
            sb.append(" - ");
            if (orderLinesRS.getTimestamp("OL_DELIVERY_D") != null)
                sb.append(orderLinesRS.getTimestamp("OL_DELIVERY_D"));
            else
                sb.append("99-99-9999");
            sb.append("]");
            orderLines.add(sb.toString());
        }
	}

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
		ResultSet whse = payGetWhse.executeQuery();
		whse.next();
		current_w_ytd = whse.getDouble("W_YTD");
        w_street_1 = whse.getString("W_STREET_1");
        w_street_2 = whse.getString("W_STREET_2");
        w_city = whse.getString("W_CITY");
        w_state = whse.getString("W_STATE");
        w_zip = whse.getString("W_ZIP");
        w_name = whse.getString("W_NAME");

        payUpdateWhse.setDouble(1, current_w_ytd + paymentAmount);
        payUpdateWhse.setInt(2, w_id);
        // MySQL reports deadlocks due to lock upgrades:
        // t1: read w_id = x; t2: update w_id = x; t1 update w_id = x
        int result = payUpdateWhse.executeUpdate();
		
		payGetDist.setInt(1, w_id);
        payGetDist.setInt(2, districtID);
        ResultSet dist = payGetDist.executeQuery();
        dist.next();
		current_d_ytd = dist.getDouble("D_YTD");
		d_street_1 = dist.getString("D_STREET_1");
        d_street_2 = dist.getString("D_STREET_2");
        d_city = dist.getString("D_CITY");
        d_state = dist.getString("D_STATE");
        d_zip = dist.getString("D_ZIP");
        d_name = dist.getString("D_NAME");

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
			ResultSet customer = customerByName.executeQuery();

			while (customer.next()) {
				c = new Customer();
				c.c_first = customer.getString("c_first");
				c.c_middle = customer.getString("c_middle");
				c.c_street_1 = customer.getString("c_street_1");
				c.c_street_2 = customer.getString("c_street_2");
				c.c_city = customer.getString("c_city");
				c.c_state = customer.getString("c_state");
				c.c_zip = customer.getString("c_zip");
				c.c_phone = customer.getString("c_phone");
				c.c_credit = customer.getString("c_credit");
				c.c_credit_lim = customer.getDouble("c_credit_lim");
				c.c_discount = customer.getDouble("c_discount");
				c.c_balance = customer.getDouble("c_balance");
				c.c_ytd_payment = customer.getDouble("c_ytd_payment");
				c.c_payment_cnt = customer.getInt("c_payment_cnt");
				c.c_since = customer.getTimestamp("c_since");

				c.c_id = customer.getInt("C_ID");
				c.c_last = customerLastName;
				customers.add(c);
			}

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
			ResultSet payCustomer = payGetCust.executeQuery();
			payCustomer.next();

			c = new Customer();
			c.c_first = payCustomer.getString("c_first");
			c.c_middle = payCustomer.getString("c_middle");
			c.c_street_1 = payCustomer.getString("c_street_1");
			c.c_street_2 = payCustomer.getString("c_street_2");
			c.c_city = payCustomer.getString("c_city");
			c.c_state = payCustomer.getString("c_state");
			c.c_zip = payCustomer.getString("c_zip");
			c.c_phone = payCustomer.getString("c_phone");
			c.c_credit = payCustomer.getString("c_credit");
			c.c_credit_lim = payCustomer.getDouble("c_credit_lim");
			c.c_discount = payCustomer.getDouble("c_discount");
			c.c_balance = payCustomer.getDouble("c_balance");
			c.c_ytd_payment = payCustomer.getDouble("c_ytd_payment");
			c.c_payment_cnt = payCustomer.getInt("c_payment_cnt");
			c.c_since = payCustomer.getTimestamp("c_since");

			c.c_id = customerID;
			c.c_last = payCustomer.getString("C_LAST");
        }

        c.c_balance -= paymentAmount;
        c.c_ytd_payment += paymentAmount;
        c.c_payment_cnt += 1;
        String c_data = null;
        if (c.c_credit.equals("BC")) { // bad credit
            payGetCustCdata.setInt(1, customerWarehouseID);
            payGetCustCdata.setInt(2, customerDistrictID);
            payGetCustCdata.setInt(3, c.c_id);
            ResultSet customerCdata = payGetCustCdata.executeQuery();
            customerCdata.next();
            c_data = customerCdata.getString("C_DATA");

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

	public void stockLevel(int w_id, int terminalDistrictLowerID,
			int terminalDistrictUpperID) throws SQLException {
		String stockGetDistOrderIdSQL =
				"SELECT D_NEXT_O_ID " + 
				"  FROM " + "DISTRICT" +
				" WHERE D_W_ID = ? " +
				"   AND D_ID = ?";

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
		
		PreparedStatement stockGetDistOrderId = connect.prepareStatement(stockGetDistOrderIdSQL);
		PreparedStatement stockGetOLId = connect.prepareStatement(stockGetOLIdSQL);
	    PreparedStatement stockGetCountStock = connect.prepareStatement(stockGetCountStockSQL);

		int threshold = r.nextInt(11) + 10; // 10-20
		int d_id = r.nextInt(terminalDistrictUpperID - terminalDistrictLowerID + 1) + terminalDistrictLowerID;

		int o_id = 0;
		int stock_count = 0;

		stockGetDistOrderId.setInt(1, w_id);
		stockGetDistOrderId.setInt(2, d_id);
		ResultSet rs = stockGetDistOrderId.executeQuery();
		rs.next();
		o_id = rs.getInt("D_NEXT_O_ID");

		stockGetOLId.setInt(1, w_id);
		stockGetOLId.setInt(2, d_id);
		stockGetOLId.setInt(3, o_id);
		stockGetOLId.setInt(4, o_id - 20);
		ResultSet orderLine = stockGetOLId.executeQuery();
		
		int current_ol_i_id;
		ArrayList<Integer> seen_s_i_ids = new ArrayList<Integer>();
		ResultSet rs1;
		while(orderLine.next()) {
			current_ol_i_id = orderLine.getInt("OL_I_ID");
			stockGetCountStock.setInt(1, w_id);
			stockGetCountStock.setInt(2, current_ol_i_id);
			stockGetCountStock.setInt(3, threshold);
			rs1 = stockGetCountStock.executeQuery();
			rs1.next();
			int current_s_i_id = rs1.getInt("S_I_ID");
			if(!seen_s_i_ids.contains(current_s_i_id)) {
				seen_s_i_ids.add(current_s_i_id);
				stock_count += 1;
			}
		}
	}
}
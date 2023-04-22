CREATE TABLE order_line (
  ol_w_id INT,
  ol_d_id INT,
  ol_o_id INT,
  ol_number INT,
  ol_i_id INT,
  ol_delivery_d TIME,
  ol_amount DOUBLE PRECISION,
  ol_supply_w_id INT,
  ol_quantity DOUBLE PRECISION,
  ol_dist_info VARCHAR(24),
  PRIMARY KEY (ol_w_id,ol_d_id,ol_o_id,ol_number)
);

CREATE TABLE new_order (
  no_w_id INT,
  no_d_id INT,
  no_o_id INT,
  PRIMARY KEY (no_w_id,no_d_id,no_o_id)
);

CREATE TABLE stock (
  s_w_id INT,
  s_i_id INT,
  s_quantity DOUBLE PRECISION,
  s_ytd DOUBLE PRECISION,
  s_order_cnt INT,
  s_remote_cnt INT,
  s_data VARCHAR(50),
  s_dist_01 VARCHAR(24),
  s_dist_02 VARCHAR(24),
  s_dist_03 VARCHAR(24),
  s_dist_04 VARCHAR(24),
  s_dist_05 VARCHAR(24),
  s_dist_06 VARCHAR(24),
  s_dist_07 VARCHAR(24),
  s_dist_08 VARCHAR(24),
  s_dist_09 VARCHAR(24),
  s_dist_10 VARCHAR(24),
  PRIMARY KEY (s_w_id,s_i_id)
);

CREATE TABLE oorder (
  o_w_id INT,
  o_d_id INT,
  o_id INT,
  o_c_id INT,
  o_carrier_id INT DEFAULT NULL,
  o_ol_cnt DOUBLE PRECISION,
  o_all_local DOUBLE PRECISION,
  o_entry_d TIME DEFAULT CURRENT_TIME,
  PRIMARY KEY (o_w_id,o_d_id,o_id),
  UNIQUE (o_w_id,o_d_id,o_c_id,o_id)
);

CREATE TABLE customer (
  c_w_id INT,
  c_d_id INT,
  c_id INT,
  c_discount DOUBLE PRECISION,
  c_credit VARCHAR(2),
  c_last VARCHAR(16),
  c_first VARCHAR(16),
  c_credit_lim DOUBLE PRECISION,
  c_balance DOUBLE PRECISION,
  c_ytd_payment DOUBLE PRECISION,
  c_payment_cnt INT,
  c_delivery_cnt INT,
  c_street_1 VARCHAR(20),
  c_street_2 VARCHAR(20),
  c_city VARCHAR(20),
  c_state VARCHAR(2),
  c_zip VARCHAR(9),
  c_phone VARCHAR(16),
  c_since TIME DEFAULT CURRENT_TIME,
  c_middle VARCHAR(2),
  c_data VARCHAR(500),
  PRIMARY KEY (c_w_id,c_d_id,c_id)
);

CREATE TABLE district (
  d_w_id INT,
  d_id INT,
  d_ytd DOUBLE PRECISION,
  d_tax DOUBLE PRECISION,
  d_next_o_id INT,
  d_name VARCHAR(10),
  d_street_1 VARCHAR(20),
  d_street_2 VARCHAR(20),
  d_city VARCHAR(20),
  d_state VARCHAR(2),
  d_zip VARCHAR(9),
  PRIMARY KEY (d_w_id,d_id)
);


CREATE TABLE item (
  i_id INT,
  i_name VARCHAR(24),
  i_price DOUBLE PRECISION,
  i_data VARCHAR(50),
  i_im_id INT,
  PRIMARY KEY (i_id)
);

CREATE TABLE warehouse (
  w_id INT,
  w_ytd DOUBLE PRECISION,
  w_tax DOUBLE PRECISION,
  w_name VARCHAR(10),
  w_street_1 VARCHAR(20),
  w_street_2 VARCHAR(20),
  w_city VARCHAR(20),
  w_state VARCHAR(2),
  w_zip VARCHAR(9),
  PRIMARY KEY (w_id)
);

ALTER TABLE district  ADD CONSTRAINT fkey_district_1 FOREIGN KEY(d_w_id) REFERENCES warehouse(w_id);
ALTER TABLE customer ADD CONSTRAINT fkey_customer_1 FOREIGN KEY(c_w_id,c_d_id) REFERENCES district(d_w_id,d_id);
ALTER TABLE new_order ADD CONSTRAINT fkey_new_order_1 FOREIGN KEY(no_w_id,no_d_id,no_o_id) REFERENCES oorder(o_w_id,o_d_id,o_id);
ALTER TABLE oorder ADD CONSTRAINT fkey_order_1 FOREIGN KEY(o_w_id,o_d_id,o_c_id) REFERENCES customer(c_w_id,c_d_id,c_id);
ALTER TABLE order_line ADD CONSTRAINT fkey_order_line_1 FOREIGN KEY(ol_w_id,ol_d_id,ol_o_id) REFERENCES oorder(o_w_id,o_d_id,o_id);
ALTER TABLE order_line ADD CONSTRAINT fkey_order_line_2 FOREIGN KEY(ol_supply_w_id,ol_i_id) REFERENCES stock(s_w_id,s_i_id);
ALTER TABLE stock ADD CONSTRAINT fkey_stock_1 FOREIGN KEY(s_w_id) REFERENCES warehouse(w_id);
ALTER TABLE stock ADD CONSTRAINT fkey_stock_2 FOREIGN KEY(s_i_id) REFERENCES item(i_id);
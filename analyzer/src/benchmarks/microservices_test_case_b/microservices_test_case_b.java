package benchmarks.microservices_test_case_b;

import ar.ChoppedTransaction;

import java.sql.*;
import java.util.Properties;

public class microservices_test_case_b {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public microservices_test_case_b(int id) {
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

	@ChoppedTransaction(originalTransaction="AtualizarPopularidadeProduto1", microservice="m1")
	public void AtualizarPopularidadeProduto1_1() throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("SELECT valor " + "FROM " + "PRECO" + " WHERE id = 1");
		ResultSet rs1 = stmt1.executeQuery();
		rs1.next();
		int valorAtual = rs1.getInt("VALOR");
	}

	@ChoppedTransaction(originalTransaction="AtualizarPopularidadeProduto1", microservice="m2")
	public void AtualizarPopularidadeProduto1_2(int valorAtual) throws SQLException {
		int MAX_V1 = 2;
		int MAX_P1 = 2;
		PreparedStatement stmt2 = connect.prepareStatement("UPDATE INFO SET popularidade = ?" + " WHERE id = 1");
		stmt2.setInt(1, MAX_P1*valorAtual/MAX_V1);
		stmt2.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="AtualizarPrecoProduto2", microservice="m2")
	public void AtualizarPrecoProduto2_1() throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("SELECT popularidade " + "FROM " + "INFO" + " WHERE id = 2");
		ResultSet rs1 = stmt1.executeQuery();
		rs1.next();
		int popularidadeAtual = rs1.getInt("POPULARIDADE");
	}

	@ChoppedTransaction(originalTransaction="AtualizarPrecoProduto2", microservice="m1")
	public void AtualizarPrecoProduto2_2(int popularidadeAtual) throws SQLException {
		int MAX_P2 = 1;
		int MAX_V2 = 1;
		PreparedStatement stmt2 = connect.prepareStatement("UPDATE PRECO SET valor = ?" + " WHERE id = 2");
		stmt2.setInt(1, MAX_V2*popularidadeAtual/MAX_P2);
		stmt2.executeUpdate();
	}
}

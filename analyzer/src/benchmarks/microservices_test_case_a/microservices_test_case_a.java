package benchmarks.microservices_test_case_a;

import ar.ChoppedTransaction;

import java.sql.*;
import java.util.Properties;

public class microservices_test_case_a {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public microservices_test_case_a(int id) {
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

	@ChoppedTransaction(originalTransaction="AtualizarProduto", microservice="m1")
	public void AtualizarProduto_1(int idProduto, int nValor) throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("UPDATE PRECO SET valor = ?" + " WHERE id = ?");
		stmt1.setInt(1, nValor);
		stmt1.setInt(2, idProduto);
		stmt1.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="AtualizarProduto", microservice="m2")
	public void AtualizarProduto_2(int idProduto, int nPopularidade) throws SQLException {
		PreparedStatement stmt2 = connect.prepareStatement("UPDATE INFO SET popularidade = ?" + " WHERE id = ?");
		stmt2.setInt(1, nPopularidade);
		stmt2.setInt(2, idProduto);
		stmt2.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="InvalidarProduto", microservice="m1")
	public void InvalidarProduto_1(int idProduto) throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("UPDATE PRECO SET valor = ?" + " WHERE id = ?");
		stmt1.setInt(1, -1);
		stmt1.setInt(2, idProduto);
		stmt1.executeUpdate();
	}

	@ChoppedTransaction(originalTransaction="InvalidarProduto", microservice="m2")
	public void InvalidarProduto_2(int idProduto) throws SQLException {
		PreparedStatement stmt2 = connect.prepareStatement("UPDATE INFO SET popularidade = ?" + " WHERE id = ?");
		stmt2.setInt(1, -1);
		stmt2.setInt(2, idProduto);
		stmt2.executeUpdate();
	}
}

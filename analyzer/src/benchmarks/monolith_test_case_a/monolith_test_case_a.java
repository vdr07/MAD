package benchmarks.monolith_test_case_a;

import ar.ChoppedTransaction;

import java.sql.*;
import java.util.Properties;

public class monolith_test_case_a {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public monolith_test_case_a(int id) {
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

	@ChoppedTransaction(microservice="m1")
	public void AtualizarProduto(int idProduto, int nValor, int nPopularidade) throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("UPDATE PRECO SET valor = ?" + " WHERE id = ?");
		stmt1.setInt(1, nValor);
		stmt1.setInt(2, idProduto);
		stmt1.executeUpdate();

		PreparedStatement stmt2 = connect.prepareStatement("UPDATE INFO SET popularidade = ?" + " WHERE id = ?");
		stmt2.setInt(1, nPopularidade);
		stmt2.setInt(2, idProduto);
		stmt2.executeUpdate();
	}

	@ChoppedTransaction(microservice="m1")
	public void InvalidarProduto(int idProduto) throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("UPDATE PRECO SET valor = ?" + " WHERE id = ?");
		stmt1.setInt(1, -1);
		stmt1.setInt(2, idProduto);
		stmt1.executeUpdate();

		PreparedStatement stmt2 = connect.prepareStatement("UPDATE INFO SET popularidade = ?" + " WHERE id = ?");
		stmt2.setInt(1, -1);
		stmt2.setInt(2, idProduto);
		stmt2.executeUpdate();
	}
}

package benchmarks.petclinic;

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

public class petclinic {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public petclinic(int id) {
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

	// OwnerController
	public void ownerProcessCreationForm(int ownerId, String firstName, String lastName,
			String address, String city, String telephone) throws SQLException {
		String insertOwnerSQL = 
				"INSERT INTO " + "OWNERS" +
				" (id, first_name, last_name, address, city, telephone) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";

		PreparedStatement insertOwner = connect.prepareStatement(insertOwnerSQL);
		insertOwner.setInt(1, ownerId);
		insertOwner.setString(2, firstName);
		insertOwner.setString(3, lastName);
		insertOwner.setString(4, address);
		insertOwner.setString(5, city);
		insertOwner.setString(6, telephone);
		insertOwner.executeUpdate();
	}

	public void ownerProcessFindForm(int ownerId) throws SQLException {
		String findOwnerByIdSQL = 
				"SELECT last_name FROM " + "OWNERS"+
				" WHERE id = ?";

		String findOwnerByLastNameSQL = 
				"SELECT * FROM " + "OWNERS"+
				" WHERE last_name = ?";

		PreparedStatement findOwnerById = connect.prepareStatement(findOwnerByIdSQL);
		findOwnerById.setLong(1, ownerId);
		ResultSet owner = findOwnerById.executeQuery();
		String lastName = "";
		if (owner.next()) {
			lastName = owner.getString("last_name");
		}

		PreparedStatement findOwnerByLastName = connect.prepareStatement(findOwnerByLastNameSQL);
		findOwnerByLastName.setString(1, lastName);
		ResultSet ownersByLastName = findOwnerByLastName.executeQuery();
		if (!ownersByLastName.next()) {
			System.out.println("No owners found");
		}
	}

	public void ownerInitUpdateOwnerForm(int ownerId) throws SQLException {
		String findOwnerByIdSQL = 
				"SELECT * FROM " + "OWNERS"+
				" WHERE id = ?";

		PreparedStatement findOwnerById = connect.prepareStatement(findOwnerByIdSQL);
		findOwnerById.setInt(1, ownerId);
		ResultSet owner = findOwnerById.executeQuery();
		if (!owner.next()) {
			System.out.println("Empty");
		}
	}

	public void ownerProcessUpdateOwnerForm(int currentId, int newId) throws SQLException {
		String updateOwnerSQL = 
				"UPDATE " + "OWNERS" +
				"   SET id = ?" +
				" WHERE id = ?";

		PreparedStatement updateOwner = connect.prepareStatement(updateOwnerSQL);
		updateOwner.setInt(1, currentId);
		updateOwner.setInt(2, newId);
		updateOwner.executeUpdate();
	}

	public void ownerShowOwner(int ownerId) throws SQLException {
		String findOwnerByIdSQL = 
				"SELECT * FROM " + "OWNERS"+
				" WHERE id = ?";

		PreparedStatement findOwnerById = connect.prepareStatement(findOwnerByIdSQL);
		findOwnerById.setInt(1, ownerId);
		ResultSet owner = findOwnerById.executeQuery();
		if (!owner.next()) {
			System.out.println("Empty");
		}
	}

	// PetController
	public void petInitCreationForm(int petId, String name, String birthDate, 
			int typeId, int ownerId) throws SQLException {
		String insertPetSQL = 
				"INSERT INTO " + "PETS" +
				" (id, name, birth_date, type_id, owner_id) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		PreparedStatement insertPet = connect.prepareStatement(insertPetSQL);
		insertPet.setInt(1, petId);
		insertPet.setString(2, name);
		insertPet.setString(3, birthDate);
		insertPet.setInt(4, typeId);
		insertPet.setInt(5, ownerId);
		insertPet.executeUpdate();
	}

	public void petProcessCreationForm(String petName, int ownerId) throws SQLException {
		String findPetByNameSQL = 
				"SELECT id FROM " + "PETS"+
				" WHERE name = ?";

		String updatePetOwnerSQL = 
				"UPDATE " + "PETS" +
				"   SET owner_id = ?" +
				" WHERE id = ?";

		PreparedStatement findPetByName = connect.prepareStatement(findPetByNameSQL);
		findPetByName.setString(1, petName);
		ResultSet pet = findPetByName.executeQuery();
		if (pet.next()) {
			System.out.println("duplicate pet name");
			return;
		}
		int petId = pet.getInt("id");

		PreparedStatement updatePetOwner = connect.prepareStatement(updatePetOwnerSQL);
		updatePetOwner.setInt(1, ownerId);
		updatePetOwner.setInt(2, petId);
		updatePetOwner.executeUpdate();
	}

	public void petInitUpdateForm(int petId) throws SQLException {
		String findPetByIdSQL = 
				"SELECT * FROM " + "PETS"+
				" WHERE id = ?";

		PreparedStatement findPetById = connect.prepareStatement(findPetByIdSQL);
		findPetById.setInt(1, petId);
		ResultSet pet = findPetById.executeQuery();
		if (!pet.next()) {
			System.out.println("Empty");
		}
	}

	public void petProcessUpdateForm(int ownerId, int petId) throws SQLException {
		String updatePetOwnerSQL = 
				"UPDATE " + "PETS" +
				"   SET owner_id = ?" +
				" WHERE id = ?";

		PreparedStatement updatePetOwner = connect.prepareStatement(updatePetOwnerSQL);
		updatePetOwner.setInt(1, ownerId);
		updatePetOwner.setInt(2, petId);
		updatePetOwner.executeUpdate();
	}

	// VetController
	public void vetShowVetList() throws SQLException {
		String findVetsSQL = 
				"SELECT * FROM " + "VETS"+
				" WHERE 1 = 1";

		String findVetSpecialitiesSQL = 
				"SELECT specialty_id FROM " + "VET_SPECIALTIES"+
				" WHERE vet_id = ?";

		String findSpecialitiesSQL = 
				"SELECT * FROM " + "SPECIALTIES"+
				" WHERE id = ?";

		PreparedStatement findVets = connect.prepareStatement(findVetsSQL);
		ResultSet vets = findVets.executeQuery();
		while (vets.next()) {
			int vetId = vets.getInt("id");

			PreparedStatement findVetSpecialities = connect.prepareStatement(findVetSpecialitiesSQL);
			findVetSpecialities.setInt(1, vetId);
			ResultSet vetSpecialty = findVetSpecialities.executeQuery();
			if (!vetSpecialty.next()) {
				System.out.println("Empty");
			}
			int specialtyId = vetSpecialty.getInt("id");

			PreparedStatement findSpecialities = connect.prepareStatement(findSpecialitiesSQL);
			findSpecialities.setInt(1, specialtyId);
			ResultSet specialty = findSpecialities.executeQuery();
			if (!specialty.next()) {
				System.out.println("Empty");
			}
		}
	}

	// VisitController
	public void visitProcessNewVisitForm(int visitId, String date,
			String description, int petId) throws SQLException {
		String insertVisitSQL = 
				"INSERT INTO " + "VISITS" +
				" (id, date, description, pet_id) " +
				" VALUES ( ?, ?, ?, ? )";

		PreparedStatement insertVisit = connect.prepareStatement(insertVisitSQL);
		insertVisit.setInt(1, visitId);
		insertVisit.setString(2, date);
		insertVisit.setString(3, description);
		insertVisit.setInt(4, petId);
		insertVisit.executeUpdate();
	}

	public void visitShowVisits(int petId) throws SQLException {
		String findPetByIdSQL = 
				"SELECT * FROM " + "PETS"+
				" WHERE id = ?";
		
		String findPetVisitsSQL = 
				"SELECT * FROM " + "VISITS"+
				" WHERE pet_id = ?";

		PreparedStatement findPetById = connect.prepareStatement(findPetByIdSQL);
		findPetById.setInt(1, petId);
		ResultSet pet = findPetById.executeQuery();
		if (!pet.next()) {
			System.out.println("Empty");
		}
		int petTableId = pet.getInt("id");

		PreparedStatement findPetVisits = connect.prepareStatement(findPetVisitsSQL);
		findPetVisits.setInt(1, petTableId);
		ResultSet petVisits = findPetVisits.executeQuery();
		if (!petVisits.next()) {
			System.out.println("Empty");
		}		
	}
}
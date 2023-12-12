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

	public void ownerProcessFindForm(String ownerLastName) throws SQLException {
		String findOwnersByLastNameSQL = 
				"SELECT * FROM " + "OWNERS"+
				" WHERE last_name = ?";

		String findOwnerPetsSQL = 
				"SELECT * FROM " + "PETS"+
				" WHERE owner_id = ?";

		String findPetsVisitsSQL = 
				"SELECT * FROM " + "VISITS"+
				" WHERE pet_id = ?";

		String findPetTypesSQL = 
				"SELECT * FROM " + "TYPES"+
				" WHERE 1 = 1";

		PreparedStatement findOwnersByLastName = connect.prepareStatement(findOwnersByLastNameSQL);
		findOwnersByLastName.setString(1, ownerLastName);
		ResultSet ownersByLastName = findOwnersByLastName.executeQuery();
		while (ownersByLastName.next()) {
			int ownerId = ownersByLastName.getInt("id");
			PreparedStatement findOwnerPets = connect.prepareStatement(findOwnerPetsSQL);
			findOwnerPets.setInt(1, ownerId);
			ResultSet pets = findOwnerPets.executeQuery();
			while (pets.next()) {
				int petId = pets.getInt("id");
				PreparedStatement findPetsVisits = connect.prepareStatement(findPetsVisitsSQL);
				findPetsVisits.setInt(1, petId);
				ResultSet petVisits = findPetsVisits.executeQuery();
				petVisits.next();
			}
			PreparedStatement findPetTypes = connect.prepareStatement(findPetTypesSQL);
			ResultSet petTypes = findPetTypes.executeQuery();
			petTypes.next();
		}
	}

	public void ownerInitUpdateOwnerForm(int ownerId) throws SQLException {
		String findOwnerByIdSQL = 
				"SELECT * FROM " + "OWNERS"+
				" WHERE id = ?";

		String findOwnerPetsSQL = 
				"SELECT * FROM " + "PETS"+
				" WHERE owner_id = ?";

		String findPetsVisitsSQL = 
				"SELECT * FROM " + "VISITS"+
				" WHERE pet_id = ?";

		String findPetTypesSQL = 
				"SELECT * FROM " + "TYPES"+
				" WHERE 1 = 1";

		PreparedStatement findOwnerById = connect.prepareStatement(findOwnerByIdSQL);
		findOwnerById.setInt(1, ownerId);
		ResultSet owner = findOwnerById.executeQuery();
		owner.next();

		PreparedStatement findOwnerPets = connect.prepareStatement(findOwnerPetsSQL);
		findOwnerPets.setInt(1, ownerId);
		ResultSet pets = findOwnerPets.executeQuery();
		while (pets.next()) {
			int petId = pets.getInt("id");
			PreparedStatement findPetsVisits = connect.prepareStatement(findPetsVisitsSQL);
			findPetsVisits.setInt(1, petId);
			ResultSet petVisits = findPetsVisits.executeQuery();
			petVisits.next();
		}
		PreparedStatement findPetTypes = connect.prepareStatement(findPetTypesSQL);
		ResultSet petTypes = findPetTypes.executeQuery();
		petTypes.next();
	}

	public void ownerProcessUpdateOwnerForm(int newOwner, int currentId, int newId, String firstName, String lastName,
			String address, String city, String telephone) throws SQLException {
		String insertOwnerSQL = 
				"INSERT INTO " + "OWNERS" +
				" (id, first_name, last_name, address, city, telephone) " +
				" VALUES ( ?, ?, ?, ?, ?, ? )";

		String updateOwnerSQL = 
				"UPDATE " + "OWNERS" +
				"   SET id = ?" +
				" WHERE id = ?";
				
		if (newOwner == 1) {
			PreparedStatement insertOwner = connect.prepareStatement(insertOwnerSQL);
			insertOwner.setInt(1, newId);
			insertOwner.setString(2, firstName);
			insertOwner.setString(3, lastName);
			insertOwner.setString(4, address);
			insertOwner.setString(5, city);
			insertOwner.setString(6, telephone);
			insertOwner.executeUpdate();
		} else {
			PreparedStatement updateOwner = connect.prepareStatement(updateOwnerSQL);
			updateOwner.setInt(1, currentId);
			updateOwner.setInt(2, newId);
			updateOwner.executeUpdate();
		}
	}

	public void ownerShowOwner(int ownerId) throws SQLException {
		String findOwnerByIdSQL = 
				"SELECT * FROM " + "OWNERS"+
				" WHERE id = ?";

		String findOwnerPetsSQL = 
				"SELECT * FROM " + "PETS"+
				" WHERE owner_id = ?";

		String findPetsVisitsSQL = 
				"SELECT * FROM " + "VISITS"+
				" WHERE pet_id = ?";

		String findPetTypesSQL = 
				"SELECT * FROM " + "TYPES"+
				" WHERE 1 = 1";

		PreparedStatement findOwnerById = connect.prepareStatement(findOwnerByIdSQL);
		findOwnerById.setInt(1, ownerId);
		ResultSet owner = findOwnerById.executeQuery();
		owner.next();

		PreparedStatement findOwnerPets = connect.prepareStatement(findOwnerPetsSQL);
		findOwnerPets.setInt(1, ownerId);
		ResultSet pets = findOwnerPets.executeQuery();
		while (pets.next()) {
			int petId = pets.getInt("id");
			PreparedStatement findPetsVisits = connect.prepareStatement(findPetsVisitsSQL);
			findPetsVisits.setInt(1, petId);
			ResultSet petVisits = findPetsVisits.executeQuery();
			petVisits.next();
		}
		PreparedStatement findPetTypes = connect.prepareStatement(findPetTypesSQL);
		ResultSet petTypes = findPetTypes.executeQuery();
		petTypes.next();
	}

	// PetController
	public void petInitCreationForm(int petId, int ownerId) throws SQLException {
		String insertPetSQL = 
				"INSERT INTO " + "PETS" +
				" (id, name, birth_date, type_id, owner_id) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		String updatePetOwnerSQL = 
				"UPDATE " + "PETS" +
				"   SET owner_id = ?" +
				" WHERE id = ?";

		PreparedStatement insertPet = connect.prepareStatement(insertPetSQL);
		insertPet.setInt(1, petId);
		insertPet.setString(2, "");
		insertPet.setString(3, "");
		insertPet.setInt(4, -1);
		insertPet.setInt(5, -1);
		insertPet.executeUpdate();

		PreparedStatement updatePetOwner = connect.prepareStatement(updatePetOwnerSQL);
		updatePetOwner.setInt(1, ownerId);
		updatePetOwner.executeUpdate();
	}

	public void petProcessCreationForm(int petId, String petName, String birthDate, 
			int typeId, int ownerId, int isNew) throws SQLException {
		String findPetByNameAndOwnerIdSQL = 
				"SELECT id FROM " + "PETS"+
				" WHERE name = ? and owner_id = ?";

		String insertPetSQL = 
				"INSERT INTO " + "PETS" +
				" (id, name, birth_date, type_id, owner_id) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		String updatePetSQL = 
				"UPDATE " + "PETS" +
				"   SET name = ?," +
				"       birth_date = ?," +
				"       type_id = ?," +
				"       owner_id = ?" +
				" WHERE id = ?";

		PreparedStatement findPetByNameAndOwnerId = connect.prepareStatement(findPetByNameAndOwnerIdSQL);
		findPetByNameAndOwnerId.setString(1, petName);
		findPetByNameAndOwnerId.setInt(2, ownerId);
		ResultSet pet = findPetByNameAndOwnerId.executeQuery();
		if (pet.next()) {
			System.out.println("duplicate pet name");
			return;
		}

		if (isNew == 1) {
			PreparedStatement insertPet = connect.prepareStatement(insertPetSQL);
			insertPet.setInt(1, petId);
			insertPet.setString(2, petName);
			insertPet.setString(3, birthDate);
			insertPet.setInt(4, typeId);
			insertPet.setInt(5, ownerId);
			insertPet.executeUpdate();
		} else {
			PreparedStatement updatePet = connect.prepareStatement(updatePetSQL);
			updatePet.setString(1, petName);
			updatePet.setString(2, birthDate);
			updatePet.setInt(3, typeId);
			updatePet.setInt(4, ownerId);
			updatePet.setInt(5, petId);
			updatePet.executeUpdate();
		}
	}

	public void petInitUpdateForm(int petId) throws SQLException {
		String findPetByIdSQL = 
				"SELECT * FROM " + "PETS"+
				" WHERE id = ?";

		PreparedStatement findPetById = connect.prepareStatement(findPetByIdSQL);
		findPetById.setInt(1, petId);
		ResultSet pet = findPetById.executeQuery();
		pet.next();
	}

	public void petProcessUpdateForm(int petId, String petName, String birthDate, 
			int typeId, int ownerId, int isNew) throws SQLException {
		String findPetByNameAndOwnerIdSQL = 
				"SELECT id FROM " + "PETS"+
				" WHERE name = ? and owner_id = ?";

		String insertPetSQL = 
				"INSERT INTO " + "PETS" +
				" (id, name, birth_date, type_id, owner_id) " +
				" VALUES ( ?, ?, ?, ?, ? )";

		String updatePetSQL = 
				"UPDATE " + "PETS" +
				"   SET name = ?," +
				"       birth_date = ?," +
				"       type_id = ?," +
				"       owner_id = ?" +
				" WHERE id = ?";

		PreparedStatement findPetByNameAndOwnerId = connect.prepareStatement(findPetByNameAndOwnerIdSQL);
		findPetByNameAndOwnerId.setString(1, petName);
		findPetByNameAndOwnerId.setInt(2, ownerId);
		ResultSet pet = findPetByNameAndOwnerId.executeQuery();
		if (pet.next()) {
			System.out.println("duplicate pet name");
			return;
		}

		if (isNew == 1) {
			PreparedStatement insertPet = connect.prepareStatement(insertPetSQL);
			insertPet.setInt(1, petId);
			insertPet.setString(2, petName);
			insertPet.setString(3, birthDate);
			insertPet.setInt(4, typeId);
			insertPet.setInt(5, ownerId);
			insertPet.executeUpdate();
		} else {
			PreparedStatement updatePet = connect.prepareStatement(updatePetSQL);
			updatePet.setString(1, petName);
			updatePet.setString(2, birthDate);
			updatePet.setInt(3, typeId);
			updatePet.setInt(4, ownerId);
			updatePet.setInt(5, petId);
			updatePet.executeUpdate();
		}
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
			vetSpecialty.next();
			int specialtyId = vetSpecialty.getInt("specialty_id");

			PreparedStatement findSpecialities = connect.prepareStatement(findSpecialitiesSQL);
			findSpecialities.setInt(1, specialtyId);
			ResultSet specialty = findSpecialities.executeQuery();
			specialty.next();
		}
	}

	public void vetShowResourcesVetList() throws SQLException {
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
			vetSpecialty.next();
			int specialtyId = vetSpecialty.getInt("specialty_id");

			PreparedStatement findSpecialities = connect.prepareStatement(findSpecialitiesSQL);
			findSpecialities.setInt(1, specialtyId);
			ResultSet specialty = findSpecialities.executeQuery();
			specialty.next();
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
}
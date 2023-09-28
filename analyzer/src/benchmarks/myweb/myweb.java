package benchmarks.myweb;

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

public class myweb {

	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;
	private Random r;

	public myweb(int id) {
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

	// IndexController
	public void index(long userId) throws SQLException {
		String getUserRolesSQL = 
				"SELECT * FROM " + "SYS_USER_ROLE"+
				" WHERE user_id = ?";

		String getRolesSQL = 
				"SELECT * FROM " + "SYS_ROLES"+
				" WHERE id = ?";

		String getPermissionsByRoleSQL = 
				"SELECT * FROM " + "SYS_ROLE_PERMISSION"+
				" WHERE role_id = ?";

		String getResourceByPermissionIdSQL = 
				"SELECT * FROM " + "SYS_PERMISSIONS"+
				" WHERE id = ?";

		PreparedStatement getUserRoles = connect.prepareStatement(getUserRolesSQL);
		getUserRoles.setLong(1, userId);
		ResultSet rolesIds = getUserRoles.executeQuery();
		while (rolesIds.next()) {
			long roleId = rolesIds.getLong("role_id");

			PreparedStatement getRoles = connect.prepareStatement(getRolesSQL);
			getRoles.setLong(1, roleId);
			ResultSet roles = getRoles.executeQuery();
			if (!roles.next()) {
				System.out.println("No roles");
			}
			
			PreparedStatement getPermissionsByRole = connect.prepareStatement(getPermissionsByRoleSQL);
			getPermissionsByRole.setLong(1, roleId);
			ResultSet permissions = getPermissionsByRole.executeQuery();
			while (permissions.next()) {
				long permissionId = permissions.getLong("permission_id");

				PreparedStatement getResourceByPermissionId = connect.prepareStatement(getResourceByPermissionIdSQL);
				getResourceByPermissionId.setLong(1, permissionId);
				ResultSet resource = getResourceByPermissionId.executeQuery();
				if (!resource.next()) {
					System.out.println("Empty");
				}
			}
		}
	}

	// OperationController
	public void operationGetAll() throws SQLException {
		String getAllOperationsSQL = 
				"SELECT * FROM " + "SYS_OPERATIONS"+
				" WHERE 1 = 1";

		PreparedStatement getAllOperations = connect.prepareStatement(getAllOperationsSQL);
		ResultSet ops = getAllOperations.executeQuery();
		if (!ops.next()) {
			System.out.println("Empty");
		}
	}

	public void operationGetOne(long operationId) throws SQLException {
		String getOperationByIdSQL = 
				"SELECT * FROM " + "SYS_OPERATIONS"+
				" WHERE id = ?";

		PreparedStatement getOperationById = connect.prepareStatement(getOperationByIdSQL);
		getOperationById.setLong(1, operationId);
		ResultSet op = getOperationById.executeQuery();
		if (!op.next()) {
			System.out.println("Empty");
		}
	}

	public void operationSave(long operationId, String operationName, String operation,
			String description) throws SQLException {
		String insertOperationSQL = 
				"INSERT INTO " + "SYS_OPERATIONS" +
				" (id, name, operation, description) " +
				" VALUES ( ?, ?, ?, ? )";

		PreparedStatement insertOperation = connect.prepareStatement(insertOperationSQL);
		insertOperation.setLong(1, operationId);
		insertOperation.setString(2, operationName);
		insertOperation.setString(3, operation);
		insertOperation.setString(4, description);
		insertOperation.executeUpdate();
	}

	public void operationGetOperation(long operationId, String operationName, String operation,
			String description) throws SQLException {
		String getOperationByIdSQL = 
				"SELECT * FROM " + "SYS_OPERATIONS"+
				" WHERE id = ?";

		String updateOperationSQL = 
				"UPDATE " + "SYS_OPERATIONS" +
				"   SET name = ?," +
				"       operation = ?," +
				"       description = ?" +
				" WHERE id = ?";

		PreparedStatement getOperationById = connect.prepareStatement(getOperationByIdSQL);
		getOperationById.setLong(1, operationId);
		ResultSet op = getOperationById.executeQuery();
		if (!op.next()) {
			System.out.println("Empty");
		}

		PreparedStatement updateOperation = connect.prepareStatement(updateOperationSQL);
		updateOperation.setString(1, operationName);
		updateOperation.setString(2, operation);
		updateOperation.setString(3, description);
		updateOperation.setLong(4, operationId);
		updateOperation.executeUpdate();
	}

	public void operationDelete(long operationId) throws SQLException {
		String deleteOperationSQL = 
				"DELETE FROM " + "SYS_OPERATIONS"+
				" WHERE id = ?";

		PreparedStatement deleteOperation = connect.prepareStatement(deleteOperationSQL);
		deleteOperation.setLong(1, operationId);
		deleteOperation.executeUpdate();
	}

	// ResourceController
	public void resourceGetAll() throws SQLException {
		String getAllResourcesSQL = 
				"SELECT * FROM " + "SYS_RESOURCES"+
				" WHERE 1 = 1";

		PreparedStatement getAllResources = connect.prepareStatement(getAllResourcesSQL);
		ResultSet resources = getAllResources.executeQuery();
		if (!resources.next()) {
			System.out.println("Empty");
		}
	}

	public void resourceGetOne(long resourceId) throws SQLException {
		String getResourceByIdSQL = 
				"SELECT * FROM " + "SYS_RESOURCES"+
				" WHERE id = ?";

		PreparedStatement getResourceById = connect.prepareStatement(getResourceByIdSQL);
		getResourceById.setLong(1, resourceId);
		ResultSet resource = getResourceById.executeQuery();
		if (!resource.next()) {
			System.out.println("Empty");
		}
	}

	public void resourceSave(long resourceId, String resourceName, String resourceIdentity,
			String resourceUrl) throws SQLException {
		String insertResourceSQL = 
				"INSERT INTO " + "SYS_RESOURCES" +
				" (id, name, identity, url) " +
				" VALUES ( ?, ?, ?, ? )";

		PreparedStatement insertResource = connect.prepareStatement(insertResourceSQL);
		insertResource.setLong(1, resourceId);
		insertResource.setString(2, resourceName);
		insertResource.setString(3, resourceIdentity);
		insertResource.setString(4, resourceUrl);
		insertResource.executeUpdate();
	}

	public void resourceGetResource(long resourceId, String resourceName, String resourceIdentity,
			String resourceUrl) throws SQLException {
		String getResourceByIdSQL = 
				"SELECT * FROM " + "SYS_RESOURCES"+
				" WHERE id = ?";

		String updateResourceSQL = 
				"UPDATE " + "SYS_RESOURCES" +
				"   SET name = ?," +
				"       identity = ?" +
				" WHERE id = ?";

		PreparedStatement getResourceById = connect.prepareStatement(getResourceByIdSQL);
		getResourceById.setLong(1, resourceId);
		ResultSet resource = getResourceById.executeQuery();
		if (!resource.next()) {
			System.out.println("Empty");
		}

		PreparedStatement updateResource = connect.prepareStatement(updateResourceSQL);
		updateResource.setString(1, resourceName);
		updateResource.setString(2, resourceIdentity);
		updateResource.setLong(3, resourceId);
		updateResource.executeUpdate();
	}

	public void resourceDelete(long resourceId) throws SQLException {
		String deleteResourceSQL = 
				"DELETE FROM " + "SYS_RESOURCES"+
				" WHERE id = ?";

		PreparedStatement deleteResource = connect.prepareStatement(deleteResourceSQL);
		deleteResource.setLong(1, resourceId);
		deleteResource.executeUpdate();
	}

	// RoleController
	public void roleGetAll() throws SQLException {
		String getAllRolesSQL = 
				"SELECT * FROM " + "SYS_ROLES"+
				" WHERE 1 = 1";

		PreparedStatement getAllRoles = connect.prepareStatement(getAllRolesSQL);
		ResultSet roles = getAllRoles.executeQuery();
		if (!roles.next()) {
			System.out.println("Empty");
		}
	}

	public void roleDelete(long roleId) throws SQLException {
		String deleteRoleSQL = 
				"DELETE FROM " + "SYS_ROLES"+
				" WHERE id = ?";

		PreparedStatement deleteRole = connect.prepareStatement(deleteRoleSQL);
		deleteRole.setLong(1, roleId);
		deleteRole.executeUpdate();
	}

	public void roleSave(long roleId, String roleName, String roleRole,
			String roleDescription) throws SQLException {
		String insertRoleSQL = 
				"INSERT INTO " + "SYS_ROLES" +
				" (id, name, role, description) " +
				" VALUES ( ?, ?, ?, ? )";

		PreparedStatement insertRole = connect.prepareStatement(insertRoleSQL);
		insertRole.setLong(1, roleId);
		insertRole.setString(2, roleName);
		insertRole.setString(3, roleRole);
		insertRole.setString(4, roleDescription);
		insertRole.executeUpdate();
	}

	public void roleAuthorise(long roleId, long resourceId, long[] opsIds,
			long newPermissionId) throws SQLException {
		String getRoleByIdSQL = 
				"SELECT * FROM " + "SYS_ROLES"+
				" WHERE id = ?";

		String getResourceByIdSQL = 
				"SELECT * FROM " + "SYS_RESOURCES"+
				" WHERE id = ?";

		String getAllOperationsSQL = 
				"SELECT * FROM " + "SYS_OPERATIONS"+
				" WHERE id = ?";

		String getPermissionsByRoleSQL = 
				"SELECT permission_id FROM " + "SYS_ROLE_PERMISSION"+
				" WHERE role_id = ?";

		String getResourceByPermissionIdSQL = 
				"SELECT * FROM " + "SYS_PERMISSIONS"+
				" WHERE id = ?";

		String updatePermissionResourceSQL = 
				"UPDATE " + "SYS_PERMISSIONS" +
				"   SET resource_id = ?" +
				" WHERE id = ?";

		String insertPermissionOpSQL = 
				"INSERT INTO " + "SYS_PERMISSION_OPERATION" +
				" (permission_id, operation_id) " +
				" VALUES ( ?, ? )";
		
		String insertRolePermissionSQL = 
				"INSERT INTO " + "SYS_ROLE_PERMISSION" +
				" (role_id, permission_id) " +
				" VALUES ( ?, ? )";

		PreparedStatement getRoleById = connect.prepareStatement(getRoleByIdSQL);
		getRoleById.setLong(1, roleId);
		ResultSet role = getRoleById.executeQuery();
		if (!role.next()) {
			System.out.println("Empty");
		}

		PreparedStatement getResourceById = connect.prepareStatement(getResourceByIdSQL);
		getResourceById.setLong(1, resourceId);
		ResultSet resource = getResourceById.executeQuery();
		if (!resource.next()) {
			System.out.println("Empty");
		}

		for (long opId : opsIds) {
			PreparedStatement getAllOperations = connect.prepareStatement(getAllOperationsSQL);
			getAllOperations.setLong(1, opId);
			ResultSet operations = getAllOperations.executeQuery();
			if (!operations.next()) {
				System.out.println("Empty");
			}
		}

		PreparedStatement getPermissionsByRole = connect.prepareStatement(getPermissionsByRoleSQL);
		getPermissionsByRole.setLong(1, roleId);
		ResultSet permissions = getPermissionsByRole.executeQuery();
		long permissionId = -1;
		while (permissions.next()) {
			permissionId = permissions.getLong("permission_id");

			PreparedStatement getResourceByPermissionId = connect.prepareStatement(getResourceByPermissionIdSQL);
			getResourceByPermissionId.setLong(1, permissionId);
			ResultSet permission = getResourceByPermissionId.executeQuery();
			if (!permission.next()) {
				System.out.println("Empty");
			}
			long permissionResourceId = permission.getLong("resource_id");

			if (resourceId == permissionResourceId) {
				break;
			}
		}

		if (permissionId == -1) {
			PreparedStatement updatePermissionResource = connect.prepareStatement(updatePermissionResourceSQL);
			updatePermissionResource.setLong(1, resourceId);
			updatePermissionResource.setLong(2, newPermissionId);
			updatePermissionResource.executeUpdate();
			for (long opId : opsIds) {
				PreparedStatement insertPermissionOp = connect.prepareStatement(insertPermissionOpSQL);
				insertPermissionOp.setLong(1, newPermissionId);
				insertPermissionOp.setLong(2, opId);
				insertPermissionOp.executeUpdate();
			}
			PreparedStatement insertRolePermission = connect.prepareStatement(insertRolePermissionSQL);
			insertRolePermission.setLong(1, roleId);
			insertRolePermission.setLong(2, newPermissionId);
			insertRolePermission.executeUpdate();
		} else {
			for (long opId : opsIds) {
				PreparedStatement insertPermissionOp = connect.prepareStatement(insertPermissionOpSQL);
				insertPermissionOp.setLong(1, permissionId);
				insertPermissionOp.setLong(2, opId);
				insertPermissionOp.executeUpdate();
			}
		}
	}

	// UserController
	public void userDelete(long userId) throws SQLException {
		String deleteUserSQL = 
				"DELETE FROM " + "SYS_USERS"+
				" WHERE id = ?";

		PreparedStatement deleteUser = connect.prepareStatement(deleteUserSQL);
		deleteUser.setLong(1, userId);
		deleteUser.executeUpdate();
	}

	public void userSaveUser(long userId, String username, String password,
			String salt, String email, String currentDate, long[] roleIds) throws SQLException {
		String insertUserSQL = 
				"INSERT INTO " + "SYS_USERS" +
				" (id, username, password, salt, locked, email, createDate) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ? )";
		
		String getRoleByIdSQL = 
				"SELECT * FROM " + "SYS_ROLES"+
				" WHERE id = ?";

		String insertUserRoleSQL = 
				"INSERT INTO " + "SYS_USER_ROLE" +
				" (user_id, role_id) " +
				" VALUES ( ?, ? )";

		PreparedStatement insertUser = connect.prepareStatement(insertUserSQL);
		insertUser.setLong(1, userId);
		insertUser.setString(2, username);
		insertUser.setString(3, password);
		insertUser.setString(4, salt);
		insertUser.setInt(5, 0);
		insertUser.setString(6, email);
		insertUser.setString(7, currentDate);
		insertUser.executeUpdate();

		for (long roleId : roleIds) {
			PreparedStatement getRoleById = connect.prepareStatement(getRoleByIdSQL);
			getRoleById.setLong(1, roleId);
			ResultSet role = getRoleById.executeQuery();
			if (!role.next()) {
				System.out.println("Empty");
			}

			PreparedStatement insertUserRole = connect.prepareStatement(insertUserRoleSQL);
			insertUserRole.setLong(1, userId);
			insertUserRole.setLong(2, roleId);
			insertUserRole.executeUpdate();
		}
	}

	public void userUpdate(long userId, long[] roleIds) throws SQLException {
		String getUserByIdSQL = 
				"SELECT * FROM " + "SYS_USERS"+
				" WHERE id = ?";
		
		String getRoleByIdSQL = 
				"SELECT * FROM " + "SYS_ROLES"+
				" WHERE id = ?";

		String getRoleIdByUserIdSQL = 
				"SELECT role_id FROM " + "SYS_USER_ROLE"+
				" WHERE user_id = ?";

		String insertUserRoleSQL = 
				"INSERT INTO " + "SYS_USER_ROLE" +
				" (user_id, role_id) " +
				" VALUES ( ?, ? )";

		PreparedStatement getUserById = connect.prepareStatement(getUserByIdSQL);
		getUserById.setLong(1, userId);
		ResultSet user = getUserById.executeQuery();
		if (!user.next()) {
			System.out.println("Empty");
		}

		for (long roleId : roleIds) {
			PreparedStatement getRoleById = connect.prepareStatement(getRoleByIdSQL);
			getRoleById.setLong(1, roleId);
			ResultSet role = getRoleById.executeQuery();
			if (!role.next()) {
				System.out.println("Empty");
			}
		}

		PreparedStatement getRoleIdByUserId = connect.prepareStatement(getRoleIdByUserIdSQL);
		getRoleIdByUserId.setLong(1, userId);
		ResultSet userRole = getRoleIdByUserId.executeQuery();
		for (long roleId : roleIds) {
			boolean found = false;
			while (userRole.next()) {
				long userRoleId = userRole.getLong("role_id");
				if (roleId == userRoleId) {
					found = true;
					break;
				}
			}
			if (!found) {
				PreparedStatement insertUserRole = connect.prepareStatement(insertUserRoleSQL);
				insertUserRole.setLong(1, userId);
				insertUserRole.setLong(2, roleId);
				insertUserRole.executeUpdate();
			}
		}
	}

	public void userGetAll() throws SQLException {
		String getUsersSQL = 
				"SELECT * FROM " + "SYS_USERS"+
				" WHERE 1 = 1";

		PreparedStatement getUsers = connect.prepareStatement(getUsersSQL);
		ResultSet users = getUsers.executeQuery();
		if (!users.next()) {
			System.out.println("Empty");
		}
	}

	public void userRegister(long userId, String username, String password,
			String salt, String email, String currentDate) throws SQLException {
		String insertUserSQL = 
				"INSERT INTO " + "SYS_USERS" +
				" (id, username, password, salt, locked, email, createDate) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ? )";

		PreparedStatement insertUser = connect.prepareStatement(insertUserSQL);
		insertUser.setLong(1, userId);
		insertUser.setString(2, username);
		insertUser.setString(3, password);
		insertUser.setString(4, salt);
		insertUser.setInt(5, 0);
		insertUser.setString(6, email);
		insertUser.setString(7, currentDate);
		insertUser.executeUpdate();
	}
}
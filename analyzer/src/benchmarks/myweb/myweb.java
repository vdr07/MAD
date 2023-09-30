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
	public void index(int userId) throws SQLException {
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
		getUserRoles.setInt(1, userId);
		ResultSet rolesIds = getUserRoles.executeQuery();
		while (rolesIds.next()) {
			int roleId = rolesIds.getInt("role_id");

			PreparedStatement getRoles = connect.prepareStatement(getRolesSQL);
			getRoles.setInt(1, roleId);
			ResultSet roles = getRoles.executeQuery();
			roles.next();
			
			PreparedStatement getPermissionsByRole = connect.prepareStatement(getPermissionsByRoleSQL);
			getPermissionsByRole.setInt(1, roleId);
			ResultSet permissions = getPermissionsByRole.executeQuery();
			while (permissions.next()) {
				int permissionId = permissions.getInt("permission_id");

				PreparedStatement getResourceByPermissionId = connect.prepareStatement(getResourceByPermissionIdSQL);
				getResourceByPermissionId.setInt(1, permissionId);
				ResultSet resource = getResourceByPermissionId.executeQuery();
				resource.next();
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
		ops.next();
	}

	public void operationGetOne(int operationId) throws SQLException {
		String getOperationByIdSQL = 
				"SELECT * FROM " + "SYS_OPERATIONS"+
				" WHERE id = ?";

		PreparedStatement getOperationById = connect.prepareStatement(getOperationByIdSQL);
		getOperationById.setInt(1, operationId);
		ResultSet op = getOperationById.executeQuery();
		op.next();
	}

	public void operationSave(int operationId, String operationName, String operation,
			String description) throws SQLException {
		String insertOperationSQL = 
				"INSERT INTO " + "SYS_OPERATIONS" +
				" (id, name, operation, description) " +
				" VALUES ( ?, ?, ?, ? )";

		PreparedStatement insertOperation = connect.prepareStatement(insertOperationSQL);
		insertOperation.setInt(1, operationId);
		insertOperation.setString(2, operationName);
		insertOperation.setString(3, operation);
		insertOperation.setString(4, description);
		insertOperation.executeUpdate();
	}

	public void operationGetOperation(int operationId, String operationName, String operation,
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
		getOperationById.setInt(1, operationId);
		ResultSet op = getOperationById.executeQuery();
		op.next();

		PreparedStatement updateOperation = connect.prepareStatement(updateOperationSQL);
		updateOperation.setString(1, operationName);
		updateOperation.setString(2, operation);
		updateOperation.setString(3, description);
		updateOperation.setInt(4, operationId);
		updateOperation.executeUpdate();
	}

	public void operationDelete(int operationId) throws SQLException {
		String deleteOperationSQL = 
				"DELETE FROM " + "SYS_OPERATIONS"+
				" WHERE id = ?";

		PreparedStatement deleteOperation = connect.prepareStatement(deleteOperationSQL);
		deleteOperation.setInt(1, operationId);
		deleteOperation.executeUpdate();
	}

	// ResourceController
	public void resourceGetAll() throws SQLException {
		String getAllResourcesSQL = 
				"SELECT * FROM " + "SYS_RESOURCES"+
				" WHERE 1 = 1";

		PreparedStatement getAllResources = connect.prepareStatement(getAllResourcesSQL);
		ResultSet resources = getAllResources.executeQuery();
		resources.next();
	}

	public void resourceGetOne(int resourceId) throws SQLException {
		String getResourceByIdSQL = 
				"SELECT * FROM " + "SYS_RESOURCES"+
				" WHERE id = ?";

		PreparedStatement getResourceById = connect.prepareStatement(getResourceByIdSQL);
		getResourceById.setInt(1, resourceId);
		ResultSet resource = getResourceById.executeQuery();
		resource.next();
	}

	public void resourceSave(int resourceId, String resourceName, String resourceIdentity,
			String resourceUrl) throws SQLException {
		String insertResourceSQL = 
				"INSERT INTO " + "SYS_RESOURCES" +
				" (id, name, identity, url) " +
				" VALUES ( ?, ?, ?, ? )";

		PreparedStatement insertResource = connect.prepareStatement(insertResourceSQL);
		insertResource.setInt(1, resourceId);
		insertResource.setString(2, resourceName);
		insertResource.setString(3, resourceIdentity);
		insertResource.setString(4, resourceUrl);
		insertResource.executeUpdate();
	}

	public void resourceGetResource(int resourceId, String resourceName, String resourceIdentity,
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
		getResourceById.setInt(1, resourceId);
		ResultSet resource = getResourceById.executeQuery();
		resource.next();

		PreparedStatement updateResource = connect.prepareStatement(updateResourceSQL);
		updateResource.setString(1, resourceName);
		updateResource.setString(2, resourceIdentity);
		updateResource.setInt(3, resourceId);
		updateResource.executeUpdate();
	}

	public void resourceDelete(int resourceId) throws SQLException {
		String deleteResourceSQL = 
				"DELETE FROM " + "SYS_RESOURCES"+
				" WHERE id = ?";

		PreparedStatement deleteResource = connect.prepareStatement(deleteResourceSQL);
		deleteResource.setInt(1, resourceId);
		deleteResource.executeUpdate();
	}

	// RoleController
	public void roleGetAll() throws SQLException {
		String getAllRolesSQL = 
				"SELECT * FROM " + "SYS_ROLES"+
				" WHERE 1 = 1";

		PreparedStatement getAllRoles = connect.prepareStatement(getAllRolesSQL);
		ResultSet roles = getAllRoles.executeQuery();
		roles.next();
	}

	public void roleDelete(int roleId) throws SQLException {
		String deleteRoleSQL = 
				"DELETE FROM " + "SYS_ROLES"+
				" WHERE id = ?";

		PreparedStatement deleteRole = connect.prepareStatement(deleteRoleSQL);
		deleteRole.setInt(1, roleId);
		deleteRole.executeUpdate();
	}

	public void roleSave(int roleId, String roleName, String roleRole,
			String roleDescription) throws SQLException {
		String insertRoleSQL = 
				"INSERT INTO " + "SYS_ROLES" +
				" (id, name, role, description) " +
				" VALUES ( ?, ?, ?, ? )";

		PreparedStatement insertRole = connect.prepareStatement(insertRoleSQL);
		insertRole.setInt(1, roleId);
		insertRole.setString(2, roleName);
		insertRole.setString(3, roleRole);
		insertRole.setString(4, roleDescription);
		insertRole.executeUpdate();
	}

	public void roleAuthorise(int roleId, int resourceId, int[] opsIds,
			int newPermissionId) throws SQLException {
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
		getRoleById.setInt(1, roleId);
		ResultSet role = getRoleById.executeQuery();
		role.next();

		PreparedStatement getResourceById = connect.prepareStatement(getResourceByIdSQL);
		getResourceById.setInt(1, resourceId);
		ResultSet resource = getResourceById.executeQuery();
		resource.next();

		for (int opId : opsIds) {
			PreparedStatement getAllOperations = connect.prepareStatement(getAllOperationsSQL);
			getAllOperations.setInt(1, opId);
			ResultSet operations = getAllOperations.executeQuery();
			operations.next();
		}

		PreparedStatement getPermissionsByRole = connect.prepareStatement(getPermissionsByRoleSQL);
		getPermissionsByRole.setInt(1, roleId);
		ResultSet permissions = getPermissionsByRole.executeQuery();
		int permissionId = -1;
		while (permissions.next()) {
			permissionId = permissions.getInt("permission_id");

			PreparedStatement getResourceByPermissionId = connect.prepareStatement(getResourceByPermissionIdSQL);
			getResourceByPermissionId.setInt(1, permissionId);
			ResultSet permission = getResourceByPermissionId.executeQuery();
			permission.next();
			int permissionResourceId = permission.getInt("resource_id");

			if (resourceId == permissionResourceId) {
				break;
			}
		}

		if (permissionId == -1) {
			PreparedStatement updatePermissionResource = connect.prepareStatement(updatePermissionResourceSQL);
			updatePermissionResource.setInt(1, resourceId);
			updatePermissionResource.setInt(2, newPermissionId);
			updatePermissionResource.executeUpdate();
			for (int opId : opsIds) {
				PreparedStatement insertPermissionOp = connect.prepareStatement(insertPermissionOpSQL);
				insertPermissionOp.setInt(1, newPermissionId);
				insertPermissionOp.setInt(2, opId);
				insertPermissionOp.executeUpdate();
			}
			PreparedStatement insertRolePermission = connect.prepareStatement(insertRolePermissionSQL);
			insertRolePermission.setInt(1, roleId);
			insertRolePermission.setInt(2, newPermissionId);
			insertRolePermission.executeUpdate();
		} else {
			for (int opId : opsIds) {
				PreparedStatement insertPermissionOp = connect.prepareStatement(insertPermissionOpSQL);
				insertPermissionOp.setInt(1, permissionId);
				insertPermissionOp.setInt(2, opId);
				insertPermissionOp.executeUpdate();
			}
		}
	}

	// UserController
	public void userDelete(int userId) throws SQLException {
		String deleteUserSQL = 
				"DELETE FROM " + "SYS_USERS"+
				" WHERE id = ?";

		PreparedStatement deleteUser = connect.prepareStatement(deleteUserSQL);
		deleteUser.setInt(1, userId);
		deleteUser.executeUpdate();
	}

	public void userSaveUser(int userId, String username, String password,
			String salt, String email, String currentDate, int[] roleIds) throws SQLException {
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
		insertUser.setInt(1, userId);
		insertUser.setString(2, username);
		insertUser.setString(3, password);
		insertUser.setString(4, salt);
		insertUser.setInt(5, 0);
		insertUser.setString(6, email);
		insertUser.setString(7, currentDate);
		insertUser.executeUpdate();

		for (int roleId : roleIds) {
			PreparedStatement getRoleById = connect.prepareStatement(getRoleByIdSQL);
			getRoleById.setInt(1, roleId);
			ResultSet role = getRoleById.executeQuery();
			role.next();

			PreparedStatement insertUserRole = connect.prepareStatement(insertUserRoleSQL);
			insertUserRole.setInt(1, userId);
			insertUserRole.setInt(2, roleId);
			insertUserRole.executeUpdate();
		}
	}

	public void userUpdate(int userId, int[] roleIds) throws SQLException {
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
		getUserById.setInt(1, userId);
		ResultSet user = getUserById.executeQuery();
		user.next();

		for (int roleId : roleIds) {
			PreparedStatement getRoleById = connect.prepareStatement(getRoleByIdSQL);
			getRoleById.setInt(1, roleId);
			ResultSet role = getRoleById.executeQuery();
			role.next();
		}

		PreparedStatement getRoleIdByUserId = connect.prepareStatement(getRoleIdByUserIdSQL);
		getRoleIdByUserId.setInt(1, userId);
		ResultSet userRole = getRoleIdByUserId.executeQuery();
		for (int roleId : roleIds) {
			boolean found = false;
			while (userRole.next()) {
				int userRoleId = userRole.getInt("role_id");
				if (roleId == userRoleId) {
					found = true;
					break;
				}
			}
			if (!found) {
				PreparedStatement insertUserRole = connect.prepareStatement(insertUserRoleSQL);
				insertUserRole.setInt(1, userId);
				insertUserRole.setInt(2, roleId);
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
		users.next();
	}

	public void userRegister(int userId, String username, String password,
			String salt, String email, String currentDate) throws SQLException {
		String insertUserSQL = 
				"INSERT INTO " + "SYS_USERS" +
				" (id, username, password, salt, locked, email, createDate) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ? )";

		PreparedStatement insertUser = connect.prepareStatement(insertUserSQL);
		insertUser.setInt(1, userId);
		insertUser.setString(2, username);
		insertUser.setString(3, password);
		insertUser.setString(4, salt);
		insertUser.setInt(5, 0);
		insertUser.setString(6, email);
		insertUser.setString(7, currentDate);
		insertUser.executeUpdate();
	}
}
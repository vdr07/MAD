CREATE TABLE sys_operations (
  id BIGINT,
  name VARCHAR(24),
  operation VARCHAR(24),
  description VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE sys_permissions (
  id BIGINT,
  resource_id BIGINT,
  name VARCHAR(24),
  description VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE sys_permission_operation (
  permission_id BIGINT,
  operation_id BIGINT,
  PRIMARY KEY (permission_id, operation_id)
);

CREATE TABLE sys_resources (
  id BIGINT,
  name VARCHAR(24),
  identity VARCHAR(24),
  url VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE sys_roles (
  id BIGINT,
  name VARCHAR(24),
  role VARCHAR(24),
  description VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE sys_role_permission (
  role_id BIGINT,
  permission_id BIGINT,
  PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE sys_users (
  id BIGINT,
  username VARCHAR(24),
  password VARCHAR(24),
  salt VARCHAR(24),
  locked INT,
  email VARCHAR(24),
  createDate VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE sys_user_role (
  user_id BIGINT,
  role_id BIGINT,
  PRIMARY KEY (user_id, role_id)
);


ALTER TABLE sys_permissions  ADD CONSTRAINT fkey_sys_permission_1 FOREIGN KEY(resource_id) REFERENCES sys_resources(id);
ALTER TABLE sys_permission_operation  ADD CONSTRAINT fkey_sys_permission_operation_1 FOREIGN KEY(permission_id) REFERENCES sys_permissions(id);
ALTER TABLE sys_permission_operation  ADD CONSTRAINT fkey_sys_permission_operation_2 FOREIGN KEY(operation_id) REFERENCES sys_operations(id);
ALTER TABLE sys_role_permission  ADD CONSTRAINT fkey_sys_role_permission_1 FOREIGN KEY(role_id) REFERENCES sys_roles(id);
ALTER TABLE sys_role_permission  ADD CONSTRAINT fkey_sys_role_permission_2 FOREIGN KEY(permission_id) REFERENCES sys_permissions(id);
ALTER TABLE sys_user_role  ADD CONSTRAINT fkey_sys_user_role_1 FOREIGN KEY(user_id) REFERENCES sys_users(id);
ALTER TABLE sys_user_role  ADD CONSTRAINT fkey_sys_user_role_2 FOREIGN KEY(role_id) REFERENCES sys_roles(id);

CREATE TABLE owners (
  id INT,
  first_name VARCHAR(24),
  last_name VARCHAR(24),
  address VARCHAR(24),
  city VARCHAR(24),
  telephone VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE pets (
  id INT,
  name VARCHAR(24),
  birth_date VARCHAR(24),
  type_id INT,
  owner_id INT,
  PRIMARY KEY (id)
);

CREATE TABLE types (
  id INT,
  name VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE specialties (
  id INT,
  name VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE vets (
  id INT,
  first_name VARCHAR(24),
  last_name VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE vet_specialties (
  vet_id INT,
  specialty_id INT,
  PRIMARY KEY (vet_id, specialty_id)
);

CREATE TABLE visits (
  id INT,
  date VARCHAR(24),
  description VARCHAR(24),
  pet_id INT,
  PRIMARY KEY (id)
);

ALTER TABLE pets  ADD CONSTRAINT fkey_pets_1 FOREIGN KEY(type_id) REFERENCES types(id);
ALTER TABLE pets  ADD CONSTRAINT fkey_pets_2 FOREIGN KEY(owner_id) REFERENCES owners(id);
ALTER TABLE vet_specialties  ADD CONSTRAINT fkey_vet_specialties_1 FOREIGN KEY(vet_id) REFERENCES vets(id);
ALTER TABLE vet_specialties  ADD CONSTRAINT fkey_vet_specialties_2 FOREIGN KEY(specialty_id) REFERENCES specialties(id);
ALTER TABLE visits  ADD CONSTRAINT fkey_visits_1 FOREIGN KEY(pet_id) REFERENCES pets(id);

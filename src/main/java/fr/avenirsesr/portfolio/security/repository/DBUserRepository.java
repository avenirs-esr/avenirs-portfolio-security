package fr.avenirsesr.portfolio.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.avenirsesr.portfolio.security.models.DBUser;


public interface DBUserRepository extends JpaRepository<DBUser, Integer> {

		public DBUser findByUsername(String username);
}

package org.sayari.booknetwork.role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Roles,Integer> {
    Optional<Roles> findByName(String role);
}

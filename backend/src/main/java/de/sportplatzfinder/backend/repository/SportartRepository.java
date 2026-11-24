package de.sportplatzfinder.backend.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.sportplatzfinder.backend.model.Sportart;

public interface SportartRepository extends JpaRepository<Sportart, Long> {

  Optional<Sportart> findByBezeichnung(String bezeichnung);

}
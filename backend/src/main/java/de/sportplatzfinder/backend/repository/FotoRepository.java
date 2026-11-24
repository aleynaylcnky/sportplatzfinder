package de.sportplatzfinder.backend.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.sportplatzfinder.backend.model.Foto;

public interface FotoRepository extends JpaRepository<Foto, Long> {

  List<Foto> findBySportplatzId(long sportplatzId);

}
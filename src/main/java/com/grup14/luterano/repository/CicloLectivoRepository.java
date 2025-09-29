package com.grup14.luterano.repository;

import com.grup14.luterano.entities.CicloLectivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CicloLectivoRepository extends JpaRepository<CicloLectivo,Long> {
}

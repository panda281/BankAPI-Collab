package com.gebeya.bankapi.Repository;


import com.gebeya.bankapi.Model.Entities.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History,Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM history WHERE rrn = :id")
    History findByRrn(@Param("id") int id);
}
package com.cab_app.cab.dao.repo;

import com.cab_app.cab.dao.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByCityNameContainingIgnoreCase(String cityName);
}

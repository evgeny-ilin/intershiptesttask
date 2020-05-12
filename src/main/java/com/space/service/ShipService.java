package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ShipService {
    Ship create(Ship ship);

    Ship update(Long id, Ship ship);

    void deleteById(Long id);

    Ship getById(Long id);

    Integer getCount(String hql);

    List<Ship> getAll(String hql, Optional<Integer> pageNumber, Optional<Integer> pageSize);
    String getHQL(Optional<String> name, Optional<String> planet, Optional<ShipType> shipType, Optional<Long> after, Optional<Long> before, Optional<Boolean> isUsed, Optional<Double> minSpeed, Optional<Double> maxSpeed, Optional<Integer> minCrewSize, Optional<Integer> maxCrewSize, Optional<Double> minRating, Optional<Double> maxRating, Optional<ShipOrder> order);
}

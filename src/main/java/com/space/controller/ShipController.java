package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.*;

@RestController
public class ShipController {

    @Autowired
    private ShipService shipService;

    @RequestMapping(value = "/rest/ships", method = RequestMethod.GET)
    public ResponseEntity<List<Ship>> getShips(@RequestParam Optional<String> name,
                                              @RequestParam Optional<String> planet,
                                              @RequestParam Optional<ShipType> shipType,
                                              @RequestParam Optional<Long> after,
                                              @RequestParam Optional<Long> before,
                                              @RequestParam Optional<Boolean> isUsed,
                                              @RequestParam Optional<Double> minSpeed,
                                              @RequestParam Optional<Double> maxSpeed,
                                              @RequestParam Optional<Integer> minCrewSize,
                                              @RequestParam Optional<Integer> maxCrewSize,
                                              @RequestParam Optional<Double> minRating,
                                              @RequestParam Optional<Double> maxRating,
                                              @RequestParam Optional<ShipOrder> order,
                                              @RequestParam (defaultValue = "0") Optional<Integer> pageNumber,
                                              @RequestParam (defaultValue = "3") Optional<Integer> pageSize)
    {

        String hql = shipService.getHQL(name,
                planet,
                shipType,
                after,
                before,
                isUsed,
                minSpeed,
                maxSpeed,
                minCrewSize,
                maxCrewSize,
                minRating,
                maxRating,
                order);

        List<Ship> ships = shipService.getAll(hql, pageNumber, pageSize);

        return new ResponseEntity<>(ships,HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/ships/count", method = RequestMethod.GET)
    public Integer shipsCount (@RequestParam Optional<String> name,
                               @RequestParam Optional<String> planet,
                               @RequestParam Optional<ShipType> shipType,
                               @RequestParam Optional<Long> after,
                               @RequestParam Optional<Long> before,
                               @RequestParam Optional<Boolean> isUsed,
                               @RequestParam Optional<Double> minSpeed,
                               @RequestParam Optional<Double> maxSpeed,
                               @RequestParam Optional<Integer> minCrewSize,
                               @RequestParam Optional<Integer> maxCrewSize,
                               @RequestParam Optional<Double> minRating,
                               @RequestParam Optional<Double> maxRating)
    {
        String hql = shipService.getHQL(name,
                planet,
                shipType,
                after,
                before,
                isUsed,
                minSpeed,
                maxSpeed,
                minCrewSize,
                maxCrewSize,
                minRating,
                maxRating,
                Optional.empty());

        return shipService.getCount(hql);
    }

    @RequestMapping(value = "/rest/ships", method = RequestMethod.POST)
    public ResponseEntity<Ship> createShip (@RequestBody Ship ship) {
        return new ResponseEntity<>(shipService.create(ship), HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.GET)
    public ResponseEntity<Ship> getShip(@PathVariable Long id)
    {
        return new ResponseEntity<>(shipService.getById(id),HttpStatus.OK);
    }


    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.POST)
    public ResponseEntity<Ship> updateShip(@PathVariable Long id, @RequestBody Ship ship)
    {
        return new ResponseEntity<>(shipService.update(id, ship),HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.DELETE)
    public void deleteShip(@PathVariable Long id) {
        shipService.deleteById(id);
    }

}

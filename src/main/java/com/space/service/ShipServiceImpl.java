package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.sql.Date;

/*
TODO Нужно ли делать проверку?
 6. Не валидным считается id, если он:
 - не числовой
 - не целое число
 - не положительный
 */

@Service
@Transactional
public class ShipServiceImpl implements ShipService {

    @Autowired
    private ShipRepository repository;

    @Autowired
    private EntityManager entityManager;

    private void checkId(Long id) {
        if (id == null || id < 1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Id is not valid: "+id);
    }

    @Override
    public Ship create(Ship ship) {
        if (isEmptyBody(ship)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Body is empty");
        checkParams(ship);
        if (ship.isUsed() == null) ship.setUsed(false);
        ship.setRating();
        return repository.save(ship);
    }

    @Override
    public Ship update(Long id, Ship shipRest) {
        checkId(id);
        shipRest.setId(id);
        //Check if updatable ship exists
        Ship shipForUpdate = getById(id);

        if (isEmptyBody(shipRest)) return shipForUpdate;

        //Fill empty fields
        shipForUpdate = fillFields(shipRest,shipForUpdate);

        checkParams(shipForUpdate);
        shipForUpdate.setRating();

        return repository.save(shipForUpdate);
    }

    private boolean isEmptyBody (Ship ship) {
        String name = ship.getName();
        String planet = ship.getPlanet();
        ShipType shipType = ship.getShipType();
        java.util.Date prodDate = ship.getProdDate();
        Boolean isUsed = ship.isUsed();
        Double speed = ship.getSpeed();
        Integer crewSize = ship.getCrewSize();

        if (name == null && planet == null && shipType == null && prodDate == null && isUsed == null && speed == null && crewSize == null) {
            //If creation in progress - throw, else - we have an update then return
            if (ship.getId() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Empty body request!");
            else
                return true;
        }
        return false;
    }

    private void checkParams(Ship ship) {
        if (ship == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Ship is null!");

        String name = ship.getName();
        String planet = ship.getPlanet();
        ShipType shipType = ship.getShipType();
        java.util.Date prodDate = ship.getProdDate();
        Boolean isUsed = ship.isUsed();
        Double speed = ship.getSpeed();
        Integer crewSize = ship.getCrewSize();

        if (name == null || name.isEmpty() || name.length() > 50) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Name is not valid: " + name);
        if (planet == null || planet.isEmpty() || planet.length() > 50) throw new  ResponseStatusException(HttpStatus.BAD_REQUEST,"planet is not valid: " + planet);
        if (shipType == null || shipType.name() == null || shipType.name().isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"shipType is not valid: " + shipType);

        //Check prodDate
        Calendar calStart = new GregorianCalendar();
        calStart.set(Calendar.YEAR,2800);
        Calendar calEnd = new GregorianCalendar();
        calEnd.set(Calendar.YEAR,3019);
        if (prodDate == null || prodDate.after(calEnd.getTime())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"prodDate is not valid: " + prodDate);
        if (prodDate.before(calStart.getTime())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"prodDate is not valid: " + prodDate);

        //Check speed
        if (speed == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"speed is null");
        speed = new BigDecimal(speed).setScale(2, RoundingMode.HALF_UP).doubleValue();
        if (speed < 0.01 || speed > 0.99) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"speed is not valid: " + speed);

        if (crewSize == null || crewSize < 1 || crewSize > 9999) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"crewSize is not valid: " + crewSize);
    }

    private Ship fillFields (Ship shipRest, Ship shipForUpdate) {
        if (shipRest.getName() != null) shipForUpdate.setName(shipRest.getName());
        if (shipRest.getPlanet() != null) shipForUpdate.setPlanet(shipRest.getPlanet());
        if (shipRest.getShipType() != null) shipForUpdate.setShipType(shipRest.getShipType());
        if (shipRest.getProdDate() != null) shipForUpdate.setProdDate(shipRest.getProdDate());
        if (shipRest.isUsed() != null) shipForUpdate.setUsed(shipRest.isUsed());
        if (shipRest.getSpeed() != null) shipForUpdate.setSpeed(shipRest.getSpeed());
        if (shipRest.getCrewSize() != null) shipForUpdate.setCrewSize(shipRest.getCrewSize());
        if (shipForUpdate.isUsed() == null) shipForUpdate.setUsed(false);

        return shipForUpdate;
    }

    @Override
    public void deleteById(Long id) {
        checkId(id);
        getById(id);
        repository.deleteById(id);
    }

    @Override
    public Ship getById(Long id) {
        checkId(id);
        Ship ship = null;
        try {
            ship = repository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Ship does not exist in database by id:"+id);
        }

        return ship;
    }

    @Override
    public String getHQL(Optional<String> name,
                             Optional<String> planet,
                             Optional<ShipType> shipType,
                             Optional<Long> after,
                             Optional<Long> before,
                             Optional<Boolean> isUsed,
                             Optional<Double> minSpeed,
                             Optional<Double> maxSpeed,
                             Optional<Integer> minCrewSize,
                             Optional<Integer> maxCrewSize,
                             Optional<Double> minRating,
                             Optional<Double> maxRating,
                             Optional<ShipOrder> order
                             )
    {
        String hqlFrom = "from Ship";

        String hqlWhereClause = "";
        hqlWhereClause += name.map(s -> "name like '%" + s + "%' and ").orElse("");
        hqlWhereClause += planet.map(s -> "planet like '%" + s + "%' and ").orElse("");
        hqlWhereClause += shipType.map(s -> "shipType = '" + s.name() + "' and ").orElse("");
        hqlWhereClause += after.map(s -> "prodDate >= '" + new Date(s) + "' and ").orElse("");
        hqlWhereClause += before.map(s -> "prodDate <= '" + new Date(s) + "' and ").orElse("");
        hqlWhereClause += isUsed.map(s -> "isUsed = '" + s + "' and ").orElse("");
        hqlWhereClause += minSpeed.map(s -> "speed >= '" + s + "' and ").orElse("");
        hqlWhereClause += maxSpeed.map(s -> "speed <= '" + s + "' and ").orElse("");
        hqlWhereClause += minCrewSize.map(s -> "crewSize >= '" + s + "' and ").orElse("");
        hqlWhereClause += maxCrewSize.map(s -> "crewSize <= '" + s + "' and ").orElse("");
        hqlWhereClause += minRating.map(s -> "rating >= '" + s + "' and ").orElse("");
        hqlWhereClause += maxRating.map(s -> "rating <= '" + s + "' and ").orElse("");

        if (!hqlWhereClause.isEmpty()) {
            hqlWhereClause = " where " + hqlWhereClause;
            hqlWhereClause = hqlWhereClause.substring(0, hqlWhereClause.lastIndexOf(" and "));
        }

        String hqlOrderBy = order.map(s -> " order by " + s.getFieldName()).orElse("");

        String hql = hqlFrom + hqlWhereClause + hqlOrderBy;

        return hql;
    }

    @Override
    public List<Ship> getAll(String hql, Optional<Integer> pageNumber, Optional<Integer> pageSize) {
        Integer pSize = pageSize.get();
        Integer pNumber = pageNumber.get();

        Query<Ship> query = (Query<Ship>) entityManager.createQuery(hql);
        query.setMaxResults(pSize);
        query.setFirstResult(pNumber * pSize);

        List<Ship> ships = query.getResultList();

        return ships;
    }

    @Override
    public Integer getCount(String hql) {

        Object result = entityManager.createQuery("select count(*) " + hql).getSingleResult();
        Integer integer = Integer.parseInt(result.toString());
        return integer;
    }
}

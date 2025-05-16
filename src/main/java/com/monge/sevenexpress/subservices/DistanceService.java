/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.subservices;

import com.monge.sevenexpress.entities.Address;
import com.monge.sevenexpress.entities.Customer;
import com.monge.sevenexpress.entities.Distance;
import com.monge.sevenexpress.repositories.AddressRepository;
import com.monge.sevenexpress.repositories.DistanceRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author DeliveryExpress
 */
@Data
@Service
public class DistanceService implements ServiceCacheable<Distance, String> {

    @Autowired
    private DistanceRepository distanceRepository;

    private final Map<String, Distance> addressCache = new ConcurrentHashMap<>();

    public Distance registerDistanceMAtrixResult(String origin, String destination, double distance, double duration) {
        Distance _distance = new Distance(origin, destination, distance, duration);
        _distance = distanceRepository.save(_distance);
        if (_distance != null) {

            cacheEntity(_distance.getId(), _distance);

        }

        return _distance;

    }

    public Distance getDistance(String id) {
        // Buscar en caché
        if (getCache().containsKey(id)) {
            return getCache().get(id);
        }

        // Buscar en BD si no está en caché
        Distance _distance = distanceRepository.findById(id).orElse(null);
        if (_distance != null) {
            cacheEntity(_distance.getId(), _distance);
        }
        return _distance;
    }

    @Override
    public Map<String, Distance> getCache() {
        return addressCache;
    }

}

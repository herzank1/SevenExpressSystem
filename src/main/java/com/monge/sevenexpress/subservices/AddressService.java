/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.subservices;

import com.monge.sevenexpress.entities.Address;
import com.monge.sevenexpress.entities.Customer;
import com.monge.sevenexpress.repositories.AddressRepository;
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
public class AddressService implements ServiceCacheable<Address, String> {

    @Autowired
    private AddressRepository addressRespository;

    private final Map<String, Address> addressCache = new ConcurrentHashMap<>();

    public Address registerGeocodingResult(String address, String coordenates,String formatedAddress) {
        Address _address = getAddress(address);
        if (_address == null) {

            _address = new Address();
            _address.setAddress(address);
            _address.setCoordenates(coordenates);
            _address.setFormatedAddress(formatedAddress);
            _address = addressRespository.save(_address);
            cacheEntity(_address.getAddress(),_address);
        
        }
        
        return _address;

    }

    public Address getAddress(String adress) {
        // Buscar en caché
        if (getCache().containsKey(adress)) {
            return getCache().get(adress);
        }

        // Buscar en BD si no está en caché
        Address _adress = addressRespository.findByAddress(adress).orElse(null);
        if (_adress != null) {
            cacheEntity(_adress.getAddress(), _adress);
        }
        return _adress;
    }

    @Override
    public Map<String, Address> getCache() {
        return addressCache;
    }

}

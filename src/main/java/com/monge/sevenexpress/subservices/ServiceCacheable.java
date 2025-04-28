/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.monge.sevenexpress.subservices;

import java.util.Map;

/**
 *
 * @author DeliveryExpress
 */
public interface ServiceCacheable<T, ID> {

    Map<ID, T> getCache(); // Método para obtener la caché

    default void cacheEntity(ID id, T entity) {
        if (entity != null) {
            getCache().put(id, entity);
        }

    }

    default void removeFromCache(ID id) {
        getCache().remove(id);
    }

    default void clearCache() {
        getCache().clear();
    }

    default T getFromCache(ID id) {
        return getCache().get(id);
    }
}

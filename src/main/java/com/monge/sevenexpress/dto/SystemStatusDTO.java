
package com.monge.sevenexpress.dto;

import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class SystemStatusDTO {

    long inProcessOrders;
    long connectedDeliveries;
    double saturationScore;

    public SystemStatusDTO() {
    }

    public SystemStatusDTO(long inProcessOrders, long connectedDeliveries) {
        this.inProcessOrders = inProcessOrders;
        this.connectedDeliveries = connectedDeliveries;

        // Calcular la capacidad total de los repartidores (3 órdenes por repartidor)
        long totalCapacity = connectedDeliveries * 3;
        this.saturationScore = totalCapacity > 0 ? (double) inProcessOrders / totalCapacity : 0.0;

        if (connectedDeliveries == 0) {
            saturationScore = 3;
        }

    }

}

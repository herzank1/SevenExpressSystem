/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.BusinessContract;
import com.monge.sevenexpress.repositories.BusinessContractRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author DeliveryExpress
 */
@Service
@Data
public class BusinessContractService {

    @Autowired
    private BusinessContractRepository businessContractRepository;


    public BusinessContract save(BusinessContract businessContract) {
        return businessContractRepository.save(businessContract);
    }



    /**
     * Obtiene o crea un BusinessContract asociado al Business.
     * todo business debe tener un contrato asiciado
     * asegurate de que este contrato este asociado a busines al ser llamado
     */
    @Transactional
    public BusinessContract getBusinessContract(Business business) {
         BusinessContract businessContract = null;
        
        if (business.getBusinessContract() == null) {
            businessContract = new BusinessContract();
            businessContract = businessContractRepository.save(businessContract);
 
        }
        return businessContract;
    }

    /**
     * *
     *
     * @return un contrato basico como el de delivery express
     */
    public BusinessContract generate_perOrderService_contract() {
        BusinessContract businessContract = new BusinessContract();
        businessContract.setInsuranceType(BusinessContract.InsuranceType.NONE);
        businessContract.setKmBase(5);
        businessContract.setKmBaseCost(45);
        businessContract.setKmExtraCost(8);
        businessContract.setPaysCuota(false);
        businessContract.setServiceType(BusinessContract.ServiceType.PER_ORDER);
        businessContract.setServiceCost(20);

        return businessContract;
    }

    /**
     * *
     * Generar un contrato de costo de servicio por porcentaje, con seguro y
     * cuota
     *
     * @return
     */
    public BusinessContract generate_percentage_ensured_cuotable_contract() {

        BusinessContract businessContract = new BusinessContract();
        businessContract.setInsuranceType(BusinessContract.InsuranceType.EXTENDED_PLUS);
        businessContract.setKmBase(5);
        businessContract.setKmBaseCost(45);
        businessContract.setKmExtraCost(8);
        businessContract.setPaysCuota(true);
        businessContract.setServiceType(BusinessContract.ServiceType.PER_ORDER_PERCENTAGE);
        businessContract.setServiceCost(6);

        return businessContract;

    }

}

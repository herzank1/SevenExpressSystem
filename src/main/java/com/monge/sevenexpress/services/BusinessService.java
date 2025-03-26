/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.dto.BusinessQuoteRequest;
import com.monge.sevenexpress.entities.BalanceAccount;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.BusinessContract;
import com.monge.sevenexpress.entities.Order;
import com.monge.sevenexpress.entities.Transaction;
import com.monge.sevenexpress.entities.User;
import com.monge.sevenexpress.repositories.BusinessRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Data
public class BusinessService implements ServiceCacheable<Business, Long> {

    @Autowired
    private final UserService userService;

    @Autowired
    private final BusinessRepository businessRepository;

    @Autowired
    private final BalanceAccountService balanceAccountService;

    @Autowired
    private final BusinessContractService businessContractService;

    @Autowired
    private final TransactionService transactionService;

    @Autowired
    private final GoogleMapsService googleMapsService;

    // Caché de negocios indexado por ID
    private final Map<Long, Business> businessCache = new ConcurrentHashMap<>();

//    @Autowired
//    public BusinessService(UserService userService,
//            BusinessRepository businessRepository,
//            BalanceAccountService balanceAccountService,
//            BusinessContractService businessContractService,
//            TransactionService TransactionService,
//            GoogleMapsService googleMapsService) {
//        this.userService = userService;
//        this.businessRepository = businessRepository;
//        this.balanceAccountService = balanceAccountService;
//        this.businessContractService = businessContractService;
//        this.transactionService = TransactionService;
//        this.googleMapsService = googleMapsService;
//    }
    @Transactional
    public boolean executeContractPostOrderDelivered(Order order) {

        BusinessContract contract = order.getBusiness().getBusinessContract();
        BalanceAccount balanceAccount = order.getBusiness().getBalanceAccount();
        Double amount = (double) contract.getServiceCost();

        switch (contract.getServiceType()) {

            case BusinessContract.ServiceType.PER_ORDER:
                transactionService.createTransaction(balanceAccount.getId(), amount, "Order delivered " + order.getId(), Transaction.TransactionType.CHARGE);
                balanceAccount.sub(amount);
                break;

            case BusinessContract.ServiceType.PER_ORDER_PERCENTAGE:
                double percentageRate = contract.getServiceCost(); // 6% en este caso
                double percentageAmount = order.getDeliveryCost() * (percentageRate / 100);

                transactionService.createTransaction(balanceAccount.getId(), percentageAmount,
                        "Order delivered " + order.getId(), Transaction.TransactionType.CHARGE);

                balanceAccount.sub(percentageAmount);
                break;

            case BusinessContract.ServiceType.SHIPMENT_CHARGED_TO_BUSINESS:
                transactionService.createTransaction(balanceAccount.getId(), amount, "Order delivered " + order.getId(), Transaction.TransactionType.CHARGE);
                balanceAccount.sub(amount);
                
                transactionService.createTransaction(balanceAccount.getId(), amount, "Order delivered (ship)" + order.getId(), Transaction.TransactionType.CHARGE);
                balanceAccount.sub(order.getDeliveryCost());
                break;

            default:
                transactionService.createTransaction(balanceAccount.getId(), amount, "Order delivered " + order.getId(), Transaction.TransactionType.CHARGE);

                break;

        }

        balanceAccountService.save(balanceAccount);
        /*quitamos del cache para que se carge balance account*/
        removeFromCache(order.getBusiness().getId());

        return true;

    }

    /**
     * *
     * Metodo seguro para obtener el businessContract de un business
     *
     * @param business
     * @return
     */
    @Transactional
    public BusinessContract getBusinessContract(Business business) {

        BusinessContract businessContract = business.getBusinessContract();
        if (business.getBusinessContract() == null) {
            businessContract = businessContractService.getBusinessContract(business);
            business.setBusinessContract(businessContract);
            businessRepository.save(business);
        }

        return businessContract;

    }

    /**
     * *
     * Metodo seguro para obtener el balanceAccount de un business
     *
     * @param business
     * @return
     */
    @Transactional
    public BalanceAccount getBalanceAccount(Business business) {
        BalanceAccount balanceAccount = business.getBalanceAccount();
        if (balanceAccount == null) {
            balanceAccount = balanceAccountService.getBalanceAccount(business);
            business.setBalanceAccount(balanceAccount);
            businessRepository.save(business);
        }

        return balanceAccount;

    }

    /**
     * Obtiene un Business por su ID, primero en caché.
     */
    public Business getById(long id) {
        // Buscar en caché
        if (getCache().containsKey(id)) {
            return getCache().get(id);
        }

        // Buscar en BD si no está en caché
        Business business = businessRepository.findById(id).orElse(null);
        if (business != null) {
            cacheEntity(business.getId(), business);
        }
        return business;
    }

    /**
     * Obtiene un Business basado en el username.
     */
    public Business getByUserName(String username) {
        // Buscar en BD si no está en caché
        User user = userService.findByUserName(username);
        if (user != null) {
            Business findById = businessRepository.findById(user.getAccountId()).orElse(null);

            if (findById != null) {
                cacheEntity(findById.getId(), findById);
                return findById;
            }

        }

        return null;
    }

    /**
     * Guarda un Business en la base de datos y lo almacena en caché.
     */
    public Business save(Business business) {
        Business savedBusiness = businessRepository.save(business);
        if (savedBusiness != null) {
            cacheEntity(savedBusiness.getId(), savedBusiness);
        }
        return savedBusiness;
    }

    @Override
    public Map<Long, Business> getCache() {
        return businessCache;
    }

    /**
     * *
     * esta funcion debe retornar mas infomracion hacerca de la distancia
     *
     * @param bqr
     * @return
     */
    public double calculateDeliveryCost(BusinessQuoteRequest bqr) {

        Business byId = getById(bqr.getRequesterId());
        GoogleMapsService.DistanceDetails distanceDetails = googleMapsService.getDistanceDetails(byId.getAddress(), bqr.getAddress());
        if (distanceDetails.getKilometers() == -1) {
            return 50.0;
        } else {
            BusinessContract businessContract = byId.getBusinessContract();

            return 40;
        }

    }

}

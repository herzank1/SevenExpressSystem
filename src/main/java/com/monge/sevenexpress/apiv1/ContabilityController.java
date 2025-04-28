/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.apiv1;

import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.dto.ReceiptUpdateRequest;
import com.monge.sevenexpress.entities.Admin;
import com.monge.sevenexpress.entities.PaymentReceipt;
import com.monge.sevenexpress.entities.Transaction;
import com.monge.sevenexpress.entities.UserProfile;
import com.monge.sevenexpress.entities.dto.PaymentReceiptDTO;
import com.monge.sevenexpress.entities.dto.TransferDTO;
import com.monge.sevenexpress.services.ContabilityService;
import com.monge.sevenexpress.services.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author DeliveryExpress
 */
@RestController
@RequestMapping("/api/v1")
public class ContabilityController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContabilityService contabilityService;

    @Autowired
    AuthController authController;

    // Endpoint para obtener sugerencias de direcciones
    @GetMapping("/accounting/getTransactions")
    public ResponseEntity<ApiResponse> getTransactions() {
        UserProfile account;

        try {
            Object anyAuthenticated = authController.getAnyAuthenticated();

            if (anyAuthenticated == null || !(anyAuthenticated instanceof UserProfile)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Account is not authenticated or invalid type"));
            }

            account = (UserProfile) anyAuthenticated;

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
        
        if(account.getBalanceAccount()==null){
         return ResponseEntity.ok(ApiResponse.success("Transactions, no se encontro balance account de esta cuenta", new ArrayList()));
        }

        List<Transaction> last10Transactions = contabilityService.getTransactionService()
                .getLast10Transactions(account.getBalanceAccount().getId());

        return ResponseEntity.ok(ApiResponse.success("Transactions", last10Transactions));
    }

    /***
     * El balance account del paymentrequest se liga al contexto usuario
     * @param request
     * @return 
     */
    @PostMapping("/accounting/createPaymentReceipt")
    public ResponseEntity<ApiResponse> createPaymentReceipt(@RequestBody PaymentReceiptDTO request) {
        UserProfile account;

        try {
            Object anyAuthenticated = authController.getAnyAuthenticated();

            if (anyAuthenticated == null || !(anyAuthenticated instanceof UserProfile)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Account is not authenticated or invalid type"));
            }

            account = (UserProfile) anyAuthenticated;

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
        }

        // Validación del balanceAccount
        if (account.getBalanceAccount() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Balance account not found"));
        }

        request.setStatus(PaymentReceipt.PaymentStatus.PENDING);
        request.setBalanceAccountId(account.getBalanceAccount().getId());

        PaymentReceipt receipt = contabilityService.getPaymentReceiptService().savePaymentReceipt(request);

        return ResponseEntity.ok(ApiResponse.success("Comprobante recibido", receipt));
    }

    @GetMapping("/accounting/getPaymentsReceipts")
    public ResponseEntity<ApiResponse> getPaymentsReceipts(@RequestParam PaymentReceipt.PaymentStatus status) {

        try {
            // Verificar que el usuario autenticado es un Admin
            Admin admin = (Admin) authController.getAnyAuthenticated();  // Asegurarse de que el tipo sea Admin

            // Obtener los recibos de pago por estado
            List<PaymentReceipt> findByStatus = contabilityService.getPaymentReceiptService()
                    .getPaymentReceiptRepository()
                    .findByStatus(status);

            // Devolver los resultados
            List<PaymentReceiptDTO> paymentReceiptDTOs = findByStatus.stream()
                    .map(PaymentReceiptDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Resultado", paymentReceiptDTOs));

        } catch (Exception ex) {
            // Manejo de excepciones (si no es Admin o cualquier otro error)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/accounting/setPaymentReceiptStatus")
    public ResponseEntity<ApiResponse> setPaymentReceiptStatus(@RequestBody ReceiptUpdateRequest request) {

        try {
            // Verificar que el usuario autenticado es un Admin
            Admin admin = (Admin) authController.getAnyAuthenticated();  // Asegurarse de que el tipo sea Admin

            // Actualizar el estado del recibo de pago
            PaymentReceipt success = contabilityService.updatePaymentReceipt(request.getId(), request.getStatus());

            // Devolver el resultado
            return ResponseEntity.ok(ApiResponse.success("Resultado", success));

        } catch (Exception ex) {
            // Manejo de excepciones: si no es Admin o cualquier otro error
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/accounting/createTransaction")
    public ResponseEntity<ApiResponse> createTransaction(@RequestBody TransferDTO transaction) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        // Admin admin = userService.getAdminByUserName(authentication.getName());
        transaction = contabilityService.executeTransferDTO(transaction);

        return ResponseEntity.ok(ApiResponse.success("transaction executed!", transaction));

    }

}

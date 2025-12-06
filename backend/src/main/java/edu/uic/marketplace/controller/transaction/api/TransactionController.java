package edu.uic.marketplace.controller.transaction;

import edu.uic.marketplace.dto.request.transaction.CancelTransactionRequest;
import edu.uic.marketplace.dto.request.transaction.CreateTransactionRequest;
import edu.uic.marketplace.dto.response.transaction.TransactionResponse;
import edu.uic.marketplace.model.transaction.TransactionStatus;
import edu.uic.marketplace.service.transaction.TransactionService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final AuthValidator authValidator;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request
    ) {

        String buyerUsername = authValidator.extractUsername();

        TransactionResponse response = transactionService.createTransaction(
                request.listingPublicId(),
                buyerUsername,
                request.finalPrice()
        );

        return ResponseEntity
                .created(URI.create("/api/transactions/" + response.getPublicId()))
                .body(response);
    }

    @GetMapping("/{transactionPublicId}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable String transactionPublicId
    ) {
        String username = authValidator.extractUsername();

        TransactionResponse response =
                transactionService.getTransactionByPublicId(transactionPublicId, username);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/purchases")
    public ResponseEntity<List<TransactionResponse>> getMyPurchases() {
        String username = authValidator.extractUsername();
        List<TransactionResponse> responses = transactionService.getUserPurchases(username);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/me/sales")
    public ResponseEntity<List<TransactionResponse>> getMySales() {
        String username = authValidator.extractUsername();
        List<TransactionResponse> responses = transactionService.getUserSales(username);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{transactionPublicId}/status")
    public ResponseEntity<TransactionResponse> updateStatus(
            @PathVariable String transactionPublicId,
            @RequestParam("status") TransactionStatus status
    ) {
        String username = authValidator.extractUsername();

        TransactionResponse response = transactionService.updateTransactionStatus(
                transactionPublicId,
                username,
                status
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{transactionPublicId}/complete")
    public ResponseEntity<TransactionResponse> completeTransaction(
            @PathVariable String transactionPublicId
    ) {
        String username = authValidator.extractUsername();

        TransactionResponse response =
                transactionService.completeTransaction(transactionPublicId, username);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{transactionPublicId}/cancel")
    public ResponseEntity<TransactionResponse> cancelTransaction(
            @PathVariable String transactionPublicId,
            @Valid @RequestBody CancelTransactionRequest request
    ) {
        String username = authValidator.extractUsername();

        TransactionResponse response =
                transactionService.cancelTransaction(transactionPublicId, username, request.reason());

        return ResponseEntity.ok(response);
    }
}

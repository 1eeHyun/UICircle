package edu.uic.marketplace.dto.request.transaction;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for cancelling a transaction.
 */
public record CancelTransactionRequest(

        @NotBlank
        String reason

) {}

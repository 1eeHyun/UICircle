package edu.uic.marketplace.dto.request.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request body for creating a transaction.
 */
public record CreateTransactionRequest(

        @NotBlank
        String listingPublicId,

        @NotNull
        @DecimalMin(value = "0.01", message = "Final price must be greater than zero")
        BigDecimal finalPrice

) {}

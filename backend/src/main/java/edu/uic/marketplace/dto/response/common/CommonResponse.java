package edu.uic.marketplace.dto.response.common;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private Instant timestamp;

    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.<T>builder()
                .success(true)
                .data(data)
                .message("Request succeeded")
                .timestamp(Instant.now())
                .build();
    }

    public static CommonResponse<Void> success() {
        return new CommonResponse<>(
                true,
                null,
                "Request succeeded",
                Instant.now());
    }

    public static <T> CommonResponse<T> error(String message) {
        return CommonResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }
}

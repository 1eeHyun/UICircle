package edu.uic.marketplace.dto.response.common;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.<T>builder()
                .success(true)
                .data(data)
                .message("Request succeeded")
                .build();
    }

    public static <T> CommonResponse<T> error(String message) {
        return CommonResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}

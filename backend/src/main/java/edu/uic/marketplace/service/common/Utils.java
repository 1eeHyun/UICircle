package edu.uic.marketplace.service.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class Utils {

    public static Pageable buildPageable(int page, int size, String sortBy, String sortDirection) {

        List<String> allowed = List.of("createdAt", "price", "viewCount", "favoriteCount");
        String field = allowed.contains(sortBy) ? sortBy : "createdAt";

        Sort.Direction dir = "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(dir, field));
    }
}

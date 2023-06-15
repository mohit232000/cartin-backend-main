package io.cartin.backend.Stubs;

import io.cartin.backend.Product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class GetInfo {
    private Long senderId;
    private Long userId;
    private Product product;
    private String name;
    
}

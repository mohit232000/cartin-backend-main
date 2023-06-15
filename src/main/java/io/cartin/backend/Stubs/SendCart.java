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
public class SendCart {
    private Product product;
    private int quantity;
}

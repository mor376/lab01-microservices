package com.example.productservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    // ArrayList (có thể thêm/xóa), khác với List.of() là danh sách bất biến
    private final List<Map<String, Object>> products = new ArrayList<>(List.of(
            Map.of("id", 1, "name", "Mouse", "price", 150000),
            Map.of("id", 2, "name", "Keyboard", "price", 250000),
            Map.of("id", 3, "name", "Monitor", "price", 2500000),
            Map.of("id", 4, "name", "Laptop", "price", 15000000),
            Map.of("id", 5, "name", "Headphone", "price", 500000),
            Map.of("id", 6, "name", "Webcam", "price", 400000),
            Map.of("id", 7, "name", "USB Flash Drive", "price", 120000),
            Map.of("id", 8, "name", "Router WiFi", "price", 800000),
            Map.of("id", 9, "name", "Speaker", "price", 650000),
            Map.of("id", 10, "name", "Mouse Pad", "price", 50000)
    ));

    private int nextId = 11;

    // GET /api/products  hoặc  GET /api/products?name=mouse
    @GetMapping
    public synchronized List<Map<String, Object>> getProducts(
            @RequestParam(required = false) String name) {
        if (name == null || name.isBlank()) {
            return new ArrayList<>(products);
        }
        String keyword = name.trim().toLowerCase();
        return products.stream()
                .filter(p -> p.get("name").toString().toLowerCase().contains(keyword))
                .toList();
    }

    @GetMapping("/{id}")
    public synchronized ResponseEntity<Map<String, Object>> getProduct(@PathVariable int id) {
        return products.stream()
                .filter(p -> p.get("id").equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/products  body: {"name": "...", "price": 123}
    @PostMapping
    public synchronized ResponseEntity<Map<String, Object>> addProduct(
            @RequestBody Map<String, Object> body) {
        Object nameObj = body.get("name");
        Object priceObj = body.get("price");

        if (nameObj == null || nameObj.toString().isBlank()
                || !(priceObj instanceof Number price) || price.doubleValue() < 0) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Object> product = new HashMap<>();
        product.put("id", nextId++);
        product.put("name", nameObj.toString().trim());
        product.put("price", price);
        products.add(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public synchronized ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        boolean removed = products.removeIf(p -> p.get("id").equals(id));
        return removed ? ResponseEntity.noContent().build()
                       : ResponseEntity.notFound().build();
    }
}
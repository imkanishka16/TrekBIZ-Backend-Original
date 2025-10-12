package com.zosh.modal;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String sku; // Stock Keeping Unit

    private String description;

    @Column(nullable = false)
    private Double mrp; // Maximum Retail Price

    @Column(nullable = false)
    private Double sellingPrice; // Actual sale price

    private String brand;

    private String image;

    @ManyToOne
    private Category category;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    // ✅ Add inventories mapping with cascade delete
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inventory> inventories;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

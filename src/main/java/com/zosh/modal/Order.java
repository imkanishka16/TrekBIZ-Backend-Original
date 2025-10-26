// package com.zosh.modal;

// import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.zosh.domain.OrderStatus;
// import com.zosh.domain.PaymentType;
// import jakarta.persistence.*;
// import lombok.*;

// import java.time.LocalDateTime;
// import java.util.List;

// @Entity
// @Table(name = "orders")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class Order {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     private Double totalAmount;

//     private LocalDateTime createdAt;

//     @ManyToOne
//     @JsonIgnore
//     private Branch branch;

//     @ManyToOne
//     @JsonIgnore
//     private User cashier;

//     @ManyToOne
//     private Customer customer;

//     private PaymentType paymentType;

//     @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//     private List<OrderItem> items;

//     private OrderStatus status=OrderStatus.COMPLETED;

//     @PrePersist
//     public void onCreate() {
//         createdAt = LocalDateTime.now();
//     }
// }


package com.zosh.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zosh.domain.OrderStatus;
import com.zosh.domain.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double totalAmount;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    @JsonIgnore
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", nullable = false)
    @JsonIgnore
    private User cashier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    // ✅✅✅ CRITICAL FIX: Proper cascade and orphanRemoval
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default  // ✅ Ensures list is never null
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private OrderStatus status = OrderStatus.COMPLETED;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
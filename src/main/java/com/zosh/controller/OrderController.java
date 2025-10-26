// package com.zosh.controller;

// import com.zosh.domain.OrderStatus;
// import com.zosh.domain.PaymentType;
// import com.zosh.exception.UserException;
// import com.zosh.payload.dto.OrderDTO;
// import com.zosh.service.OrderService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.data.domain.jaxb.SpringDataJaxb;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/orders")
// @RequiredArgsConstructor
// public class OrderController {

//     private final OrderService orderService;

//     @PostMapping
//     @PreAuthorize("hasAuthority('ROLE_CASHIER')")
//     public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO dto) throws UserException {
//         return ResponseEntity.ok(orderService.createOrder(dto));
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
//         return ResponseEntity.ok(orderService.getOrderById(id));
//     }


//     @GetMapping("/branch/{branchId}")
//     public ResponseEntity<List<OrderDTO>> getOrdersByBranch(
//             @PathVariable Long branchId,
//             @RequestParam(required = false) Long customerId,
//             @RequestParam(required = false) Long cashierId,
//             @RequestParam(required = false) PaymentType paymentType,
//             @RequestParam(required = false) OrderStatus status) {
//         return ResponseEntity.ok(orderService.getOrdersByBranch(
//                     branchId,
//                     customerId,
//                     cashierId,
//                     paymentType,
//                     status
//                 )
//         );
//     }

//     @GetMapping("/cashier/{cashierId}")
//     public ResponseEntity<List<OrderDTO>> getOrdersByCashier(@PathVariable Long cashierId) {
//         return ResponseEntity.ok(orderService.getOrdersByCashier(cashierId));
//     }

//     @GetMapping("/today/branch/{branchId}")
//     public ResponseEntity<List<OrderDTO>> getTodayOrders(@PathVariable Long branchId) {
//         return ResponseEntity.ok(orderService.getTodayOrdersByBranch(branchId));
//     }

//     @GetMapping("/customer/{customerId}")
//     public ResponseEntity<List<OrderDTO>> getCustomerOrders(@PathVariable Long customerId) {
//         return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
//     }

//     @GetMapping("/recent/{branchId}")
//     @PreAuthorize("hasAnyAuthority('ROLE_BRANCH_MANAGER', 'ROLE_BRANCH_ADMIN')")
//     public ResponseEntity<List<OrderDTO>> getRecentOrders(@PathVariable Long branchId) {
//         List<OrderDTO> recentOrders = orderService.getTop5RecentOrdersByBranchId(branchId);
//         return ResponseEntity.ok(recentOrders);
//     }

//     @DeleteMapping("/{id}")
//     @PreAuthorize("hasAuthority('ROLE_STORE_MANAGER') or hasAuthority('ROLE_STORE_ADMIN')")
//     public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
//         orderService.deleteOrder(id);
//         return ResponseEntity.noContent().build();
//     }


// }


package com.zosh.controller;

import com.zosh.domain.OrderStatus;
import com.zosh.domain.PaymentType;
import com.zosh.exception.UserException;
import com.zosh.payload.dto.OrderDTO;
import com.zosh.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j  // ✅ Add logging
public class OrderController {
    
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CASHIER')")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO dto) throws UserException {
        
        // ✅ Log incoming request
        log.info("=== ORDER REQUEST RECEIVED ===");
        log.info("Branch: {}, Cashier: {}, Total: {}", 
                 dto.getBranchId(), dto.getCashierId(), dto.getTotalAmount());
        log.info("Items count: {}", dto.getItems() != null ? dto.getItems().size() : 0);
        log.info("Customer: {}", dto.getCustomer() != null ? dto.getCustomer().getId() : "null");
        
        long startTime = System.currentTimeMillis();
        
        // ✅ Call service - will execute ONCE due to @Transactional
        OrderDTO createdOrder = orderService.createOrder(dto);
        
        long endTime = System.currentTimeMillis();
        
        log.info("=== ORDER RESPONSE SENT ===");
        log.info("Created Order ID: {}", createdOrder.getId());
        log.info("Time taken: {}ms", (endTime - startTime));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByBranch(
            @PathVariable Long branchId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long cashierId,
            @RequestParam(required = false) PaymentType paymentType,
            @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByBranch(
                branchId, customerId, cashierId, paymentType, status
        ));
    }

    @GetMapping("/cashier/{cashierId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCashier(@PathVariable Long cashierId) {
        return ResponseEntity.ok(orderService.getOrdersByCashier(cashierId));
    }

    @GetMapping("/today/branch/{branchId}")
    public ResponseEntity<List<OrderDTO>> getTodayOrders(@PathVariable Long branchId) {
        return ResponseEntity.ok(orderService.getTodayOrdersByBranch(branchId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getCustomerOrders(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    @GetMapping("/recent/{branchId}")
    @PreAuthorize("hasAnyAuthority('ROLE_BRANCH_MANAGER', 'ROLE_BRANCH_ADMIN')")
    public ResponseEntity<List<OrderDTO>> getRecentOrders(@PathVariable Long branchId) {
        List<OrderDTO> recentOrders = orderService.getTop5RecentOrdersByBranchId(branchId);
        return ResponseEntity.ok(recentOrders);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STORE_MANAGER') or hasAuthority('ROLE_STORE_ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
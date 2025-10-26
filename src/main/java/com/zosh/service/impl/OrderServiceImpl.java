//package com.zosh.service.impl;
//
//
//import com.zosh.domain.OrderStatus;
//import com.zosh.domain.PaymentType;
//import com.zosh.exception.UserException;
//import com.zosh.mapper.OrderMapper;
//import com.zosh.modal.*;
//import com.zosh.payload.dto.OrderDTO;
//import com.zosh.repository.*;
//
//import com.zosh.service.OrderService;
//import com.zosh.service.UserService;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class OrderServiceImpl implements OrderService {
//
//    private final OrderRepository orderRepository;
//    private final ProductRepository productRepository;
//    private final BranchRepository branchRepository;
//    private final UserService userService;
//
//    @Override
//    public OrderDTO createOrder(OrderDTO dto) throws UserException {
//        User cashier = userService.getCurrentUser();
//
//        Branch branch=cashier.getBranch();
//
//        if(branch==null){
//            throw new UserException("cashier's branch is null");
//        }
//
//        Order order = Order.builder()
//                .branch(branch)
//                .cashier(cashier)
//                .customer(dto.getCustomer())
//                .paymentType(dto.getPaymentType())
//                .build();
//
//        List<OrderItem> orderItems = dto.getItems().stream().map(itemDto -> {
//            Product product = productRepository.findById(itemDto.getProductId())
//                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));
//
//            return OrderItem.builder()
//                    .product(product)
//                    .quantity(itemDto.getQuantity())
//                    .price(product.getSellingPrice() * itemDto.getQuantity())
//                    .order(order)
//
//                    .build();
//        }).toList();
//
//        double total = orderItems.stream().mapToDouble(OrderItem::getPrice).sum();
//        order.setTotalAmount(total);
//        order.setItems(orderItems);
//
//        return OrderMapper.toDto(orderRepository.save(order));
//    }
//
//    @Override
//    public OrderDTO getOrderById(Long id) {
//        return orderRepository.findById(id)
//                .map(OrderMapper::toDto)
//                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
//    }
//
//
//
//    @Override
//    public List<OrderDTO> getOrdersByBranch(Long branchId,
//                                            Long customerId,
//                                            Long cashierId,
//                                            PaymentType paymentType,
//                                            OrderStatus status) {
//        return orderRepository.findByBranchId(branchId).stream()
//
//                // ✅ Filter by Customer ID (if provided)
//                .filter(order -> customerId == null ||
//                        (order.getCustomer() != null &&
//                                order.getCustomer().getId().equals(customerId)))
//
//                // ✅ Filter by Cashier ID (if provided)
//                .filter(order -> cashierId==null ||
//                        (order.getCashier() != null &&
//                                order.getCashier().getId().equals(cashierId)))
//
//                // ✅ Filter by Payment Type (if provided)
//                .filter(order -> paymentType == null ||
//                        order.getPaymentType() == paymentType)
//
//                // ✅ Filter by Status (if provided)
////                .filter(order -> status() == null ||
////                        order.getStatus() == status)
//
//                // ✅ Map to DTO
//                .map(OrderMapper::toDto)
//
//                // ✅ Sort by createdAt (latest first)
//                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
//
//                .collect(Collectors.toList());
////        return orderRepository.findByBranchId(branchId).stream()
////                .map(OrderMapper::toDto)
////                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<OrderDTO> getOrdersByCashier(Long cashierId) {
//        return orderRepository.findByCashierId(cashierId).stream()
//                .map(OrderMapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public void deleteOrder(Long id) {
//        if (!orderRepository.existsById(id)) {
//            throw new EntityNotFoundException("Order not found");
//        }
//        orderRepository.deleteById(id);
//    }
//
//    @Override
//    public List<OrderDTO> getTodayOrdersByBranch(Long branchId) {
//        LocalDate today = LocalDate.now();
//        LocalDateTime start = today.atStartOfDay();
//        LocalDateTime end = today.plusDays(1).atStartOfDay();
//
//        return orderRepository.findByBranchIdAndCreatedAtBetween(branchId, start, end)
//                .stream()
//                .map(OrderMapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
//        List<Order> orders = orderRepository.findByCustomerId(customerId);
//
//        return orders.stream()
//                .map(OrderMapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<OrderDTO> getTop5RecentOrdersByBranchId(Long branchId) {
//        branchRepository.findById(branchId)
//                .orElseThrow(() -> new EntityNotFoundException("Branch not found with ID: " + branchId));
//
//        List<Order> orders = orderRepository.findTop5ByBranchIdOrderByCreatedAtDesc(branchId);
//        return orders.stream()
//                .map(OrderMapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//}

//###################################################################################

// package com.zosh.service.impl;

// import com.zosh.domain.OrderStatus;
// import com.zosh.domain.PaymentType;
// import com.zosh.exception.UserException;
// import com.zosh.mapper.OrderMapper;
// import com.zosh.modal.*;
// import com.zosh.payload.dto.OrderDTO;
// import com.zosh.repository.*;
// import com.zosh.service.InventoryService;
// import com.zosh.service.OrderService;
// import com.zosh.service.UserService;
// import jakarta.persistence.EntityNotFoundException;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// @RequiredArgsConstructor
// public class OrderServiceImpl implements OrderService {

//     private final OrderRepository orderRepository;
//     private final ProductRepository productRepository;
//     private final BranchRepository branchRepository;
//     private final UserService userService;
//     private final InventoryService inventoryService; // ADD THIS

//     @Override
//     @Transactional // ADD THIS - Ensures rollback if inventory reduction fails
//     public OrderDTO createOrder(OrderDTO dto) throws UserException {
//         User cashier = userService.getCurrentUser();

//         Branch branch = cashier.getBranch();

//         if (branch == null) {
//             throw new UserException("cashier's branch is null");
//         }

//         Order order = Order.builder()
//                 .branch(branch)
//                 .cashier(cashier)
//                 .customer(dto.getCustomer())
//                 .paymentType(dto.getPaymentType())
//                 .build();

//         List<OrderItem> orderItems = dto.getItems().stream().map(itemDto -> {
//             Product product = productRepository.findById(itemDto.getProductId())
//                     .orElseThrow(() -> new EntityNotFoundException("Product not found"));

//             return OrderItem.builder()
//                     .product(product)
//                     .quantity(itemDto.getQuantity())
//                     .price(product.getSellingPrice() * itemDto.getQuantity())
//                     .order(order)
//                     .build();
//         }).toList();

//         double total = orderItems.stream().mapToDouble(OrderItem::getPrice).sum();
//         order.setTotalAmount(total);
//         order.setItems(orderItems);

//         // Save the order first
//         Order savedOrder = orderRepository.save(order);

//         // NEW CODE: Reduce inventory for each order item
//         try {
//             for (OrderItem item : savedOrder.getItems()) {
//                 inventoryService.reduceInventoryForOrder(
//                         branch.getId(),
//                         item.getProduct().getId(),
//                         item.getQuantity()
//                 );
//             }
//         } catch (UserException e) {
//             // If inventory reduction fails, the @Transactional will rollback everything
//             throw new UserException("Order creation failed: " + e.getMessage());
//         }

//         return OrderMapper.toDto(savedOrder);
//     }

//     @Override
//     public OrderDTO getOrderById(Long id) {
//         return orderRepository.findById(id)
//                 .map(OrderMapper::toDto)
//                 .orElseThrow(() -> new EntityNotFoundException("Order not found"));
//     }

//     @Override
//     public List<OrderDTO> getOrdersByBranch(Long branchId,
//                                             Long customerId,
//                                             Long cashierId,
//                                             PaymentType paymentType,
//                                             OrderStatus status) {
//         return orderRepository.findByBranchId(branchId).stream()
//                 // ✅ Filter by Customer ID (if provided)
//                 .filter(order -> customerId == null ||
//                         (order.getCustomer() != null &&
//                                 order.getCustomer().getId().equals(customerId)))

//                 // ✅ Filter by Cashier ID (if provided)
//                 .filter(order -> cashierId == null ||
//                         (order.getCashier() != null &&
//                                 order.getCashier().getId().equals(cashierId)))

//                 // ✅ Filter by Payment Type (if provided)
//                 .filter(order -> paymentType == null ||
//                         order.getPaymentType() == paymentType)

//                 // ✅ Map to DTO
//                 .map(OrderMapper::toDto)

//                 // ✅ Sort by createdAt (latest first)
//                 .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))

//                 .collect(Collectors.toList());
//     }

//     @Override
//     public List<OrderDTO> getOrdersByCashier(Long cashierId) {
//         return orderRepository.findByCashierId(cashierId).stream()
//                 .map(OrderMapper::toDto)
//                 .collect(Collectors.toList());
//     }

//     @Override
//     public void deleteOrder(Long id) {
//         if (!orderRepository.existsById(id)) {
//             throw new EntityNotFoundException("Order not found");
//         }
//         orderRepository.deleteById(id);
//     }

//     @Override
//     public List<OrderDTO> getTodayOrdersByBranch(Long branchId) {
//         LocalDate today = LocalDate.now();
//         LocalDateTime start = today.atStartOfDay();
//         LocalDateTime end = today.plusDays(1).atStartOfDay();

//         return orderRepository.findByBranchIdAndCreatedAtBetween(branchId, start, end)
//                 .stream()
//                 .map(OrderMapper::toDto)
//                 .collect(Collectors.toList());
//     }

//     @Override
//     public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
//         List<Order> orders = orderRepository.findByCustomerId(customerId);

//         return orders.stream()
//                 .map(OrderMapper::toDto)
//                 .collect(Collectors.toList());
//     }

//     @Override
//     public List<OrderDTO> getTop5RecentOrdersByBranchId(Long branchId) {
//         branchRepository.findById(branchId)
//                 .orElseThrow(() -> new EntityNotFoundException("Branch not found with ID: " + branchId));

//         List<Order> orders = orderRepository.findTop5ByBranchIdOrderByCreatedAtDesc(branchId);
//         return orders.stream()
//                 .map(OrderMapper::toDto)
//                 .collect(Collectors.toList());
//     }
// }



package com.zosh.service.impl;

import com.zosh.domain.OrderStatus;
import com.zosh.domain.PaymentType;
import com.zosh.exception.UserException;
import com.zosh.mapper.OrderMapper;
import com.zosh.modal.*;
import com.zosh.payload.dto.OrderDTO;
import com.zosh.payload.dto.OrderItemDTO;
import com.zosh.repository.*;
import com.zosh.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional  // ✅ CRITICAL FIX: This prevents duplicate saves
    public OrderDTO createOrder(OrderDTO dto) throws UserException {
        
        log.info("=== Starting Order Creation ===");
        log.info("Branch ID: {}, Cashier ID: {}, Total: {}", 
                 dto.getBranchId(), dto.getCashierId(), dto.getTotalAmount());
        
        // 1. Fetch Branch
        Branch branch = branchRepository.findById(dto.getBranchId())
            .orElseThrow(() -> new UserException("Branch not found with id: " + dto.getBranchId()));
        log.debug("Branch found: {}", branch.getId());
            
        // 2. Fetch Cashier
        User cashier = userRepository.findById(dto.getCashierId())
            .orElseThrow(() -> new UserException("Cashier not found with id: " + dto.getCashierId()));
        log.debug("Cashier found: {}", cashier.getId());
            
        // 3. ✅ Handle Customer object from frontend
        Customer customer = null;
        if (dto.getCustomer() != null) {
            if (dto.getCustomer().getId() != null) {
                // Frontend sent customer with ID - fetch from database
                customer = customerRepository.findById(dto.getCustomer().getId())
                    .orElse(null);
                if (customer != null) {
                    log.debug("Existing customer found: {}", customer.getId());
                }
            }
            // If customer not found or no ID, you can optionally create new customer
            // For now, we just use null if customer doesn't exist
        }

        // 4. ✅ Build Order entity - Initialize items as empty ArrayList
        Order order = Order.builder()
            .totalAmount(dto.getTotalAmount())
            .branch(branch)
            .cashier(cashier)
            .customer(customer)
            .paymentType(dto.getPaymentType() != null ? dto.getPaymentType() : PaymentType.CASH)
            .status(dto.getStatus() != null ? dto.getStatus() : OrderStatus.COMPLETED)
            .items(new ArrayList<>())  // ✅ Initialize empty list
            .build();

        log.debug("Order entity built, adding items...");

        // 5. ✅ Build and add OrderItems with proper bidirectional relationship
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            log.debug("Processing {} items", dto.getItems().size());
            
            for (OrderItemDTO itemDTO : dto.getItems()) {
                // Fetch Product
                Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new UserException("Product not found with id: " + itemDTO.getProductId()));

                // Build OrderItem with bidirectional relationship
                OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemDTO.getQuantity())
                    .price(itemDTO.getPrice())
                    .order(order)  // ✅ Set parent reference
                    .build();

                // Add to order's list
                order.getItems().add(orderItem);
                
                log.debug("Added item: Product={}, Qty={}, Price={}", 
                         product.getId(), itemDTO.getQuantity(), itemDTO.getPrice());
            }
        }

        // 6. ✅✅✅ MOST IMPORTANT: Save ONLY ONCE - cascade handles items
        log.info("Saving order to database (CASCADE will save items)...");
        Order savedOrder = orderRepository.save(order);
        
        log.info("=== Order Saved Successfully ===");
        log.info("Order ID: {}, Items count: {}", savedOrder.getId(), savedOrder.getItems().size());

        // 7. Convert to DTO and return
        return OrderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        log.debug("Fetching order: {}", id);
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return OrderMapper.toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByBranch(Long branchId, Long customerId, 
                                           Long cashierId, PaymentType paymentType, 
                                           OrderStatus status) {
        log.debug("Fetching orders for branch: {}", branchId);
        List<Order> orders = orderRepository.findByBranchId(branchId);
        
        // Apply filters
        if (customerId != null) {
            orders = orders.stream()
                .filter(o -> o.getCustomer() != null && o.getCustomer().getId().equals(customerId))
                .collect(Collectors.toList());
        }
        if (cashierId != null) {
            orders = orders.stream()
                .filter(o -> o.getCashier().getId().equals(cashierId))
                .collect(Collectors.toList());
        }
        if (paymentType != null) {
            orders = orders.stream()
                .filter(o -> o.getPaymentType() == paymentType)
                .collect(Collectors.toList());
        }
        if (status != null) {
            orders = orders.stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
        }
        
        return orders.stream()
            .map(OrderMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByCashier(Long cashierId) {
        List<Order> orders = orderRepository.findByCashierId(cashierId);
        return orders.stream()
            .map(OrderMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getTodayOrdersByBranch(Long branchId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        
        List<Order> orders = orderRepository.findByBranchIdAndCreatedAtBetween(
            branchId, startOfDay, endOfDay
        );
        
        return orders.stream()
            .map(OrderMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
            .map(OrderMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getTop5RecentOrdersByBranchId(Long branchId) {
        List<Order> orders = orderRepository.findTop5ByBranchIdOrderByCreatedAtDesc(branchId);
        return orders.stream()
            .map(OrderMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        log.info("Deleting order: {}", id);
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
        log.info("Order deleted: {}", id);
    }
}
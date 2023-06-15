package io.cartin.backend.CartinUser.Admin;

import io.cartin.backend.CartinUser.CartinUser;
import io.cartin.backend.CartinUser.CartinUserRepository;
import io.cartin.backend.CartinUser.Manager.Manager;
import io.cartin.backend.CartinUser.Manager.ManagerService;
import io.cartin.backend.CartinUser.Manager.ManagerStatus;
import io.cartin.backend.CartinUser.Role;
import io.cartin.backend.Order.Order;
import io.cartin.backend.Order.OrderRepository;
import io.cartin.backend.Product.ProductRepository;
import io.cartin.backend.Stubs.SendOrder;
import io.cartin.backend.Stubs.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService extends ManagerService {
    private final CartinUserRepository cartinUserRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    AdminService(CartinUserRepository cartinUserRepository, OrderRepository orderRepository, ProductRepository productRepository){
        super(cartinUserRepository, orderRepository, productRepository);
        this.orderRepository = orderRepository;
        this.cartinUserRepository = cartinUserRepository;
        this.productRepository = productRepository;
    }

    public void giveManagerAccess(Long adminId, Long userId){
        if(checkAdminStatus(adminId)){
            CartinUser user = cartinUserRepository.findById(userId).get();
            user.setRole(Role.MANAGER);
            user.setManager(new Manager(true, ManagerStatus.Approved));
            cartinUserRepository.save(user);
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have Admin level access or is not logged in");
        }
    }

    public void removeManagerAccess(Long adminId, Long userId){
        if(checkAdminStatus(adminId)){
            CartinUser user = cartinUserRepository.findById(userId).get();
            user.setRole(Role.CUSTOMER);
            user.setManager(new Manager(false, null));
            cartinUserRepository.save(user);
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have Admin level access or is not logged in");
        }
    }

    public List<SendOrder> getOrdersOfCustomerInDateRange(Long adminId, Long userId, LocalDate startDate, LocalDate endDate){
        if(checkAdminStatus(adminId)){
            CartinUser user = cartinUserRepository.findById(userId).orElseThrow(
                    () -> new IllegalStateException("User not found, despite previous check.")
            );

            List<Order> orders = orderRepository.findByCustomerAndOrderDateBetween(user, startDate, endDate);
            return orders.stream().map(order -> new SendOrder(order.getOrderTransactionId(), order.getProduct(), order.getQuantity(), order.getOrderDate())).collect(Collectors.toList());
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have Admin level access or is not logged in");
        }
        
    }

    public List<UserInfo> getCustomers(Long adminId){
        if(checkAdminStatus(adminId)){
            List<CartinUser> users = cartinUserRepository.findByRole(Role.CUSTOMER);
            return users.stream().map(user -> new UserInfo(user.getUserId(), user.getFirstName(), user.getMiddleName(), user.getLastName(), user.getEmail(), user.getDob(), user.getAddress(), user.getPhoneNumber())).collect(Collectors.toList());
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have Admin level access or is not logged in");
        }
    }


    public List<UserInfo> getManagers(Long adminId){
        if(checkAdminStatus(adminId)){
            List<CartinUser> users = cartinUserRepository.findByRole(Role.MANAGER);
            return users.stream().map(user -> new UserInfo(user.getUserId(), user.getFirstName(), user.getMiddleName(), user.getLastName(), user.getEmail(), user.getDob(), user.getAddress(), user.getPhoneNumber())).collect(Collectors.toList());
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have Admin level access or is not logged in");
        }
    }

    public List<UserInfo> getPendingManagers(Long adminId){
        if(checkAdminStatus(adminId)){
            List<CartinUser> managers = cartinUserRepository.findByManagerIsNotNull();
            managers.removeIf(manager -> manager.getManager().getManagerApplicationStatus() != ManagerStatus.Pending);
            return managers.stream().map(user -> new UserInfo(user.getUserId(), user.getFirstName(), user.getMiddleName(), user.getLastName(), user.getEmail(), user.getDob(), user.getAddress(), user.getPhoneNumber())).collect(Collectors.toList());
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have Admin level access or is not logged in");
        }
    }

    private boolean checkAdminStatus(Long userId){
        CartinUser cartinUser = cartinUserRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found.")
        );

        return cartinUser.getAdmin().isAdminPerms() && cartinUser.isLoginStatus();
    }



}

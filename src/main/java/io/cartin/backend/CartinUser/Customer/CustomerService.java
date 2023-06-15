package io.cartin.backend.CartinUser.Customer;

import io.cartin.backend.Cart.CartItem;
import io.cartin.backend.Cart.CartRepository;
import io.cartin.backend.CartinUser.Admin.Admin;
import io.cartin.backend.CartinUser.CartinUser;
import io.cartin.backend.CartinUser.CartinUserRepository;
import io.cartin.backend.CartinUser.Manager.Manager;
import io.cartin.backend.CartinUser.Manager.ManagerStatus;
import io.cartin.backend.CartinUser.Role;
import io.cartin.backend.Email.EmailService;
import io.cartin.backend.Jwt.JwtUtils;
import io.cartin.backend.Order.Order;
import io.cartin.backend.Order.OrderRepository;
import io.cartin.backend.Product.Category;
import io.cartin.backend.Product.Product;
import io.cartin.backend.Product.ProductRepository;
import io.cartin.backend.Stubs.LoginResponse;
import io.cartin.backend.Stubs.SendCart;
import io.cartin.backend.Stubs.SendOrder;

import io.cartin.backend.Stubs.SignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CartinUserRepository cartinUserRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    private final EmailService emailService;

    private final JwtUtils jwtUtils;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @Transactional
    public SignupResponse signUp(String password, String firstName, String lastName, LocalDate dob, String email, double amount, String address, String phone){
        try{
            Wallet wallet = new Wallet().builder()
            .amount(amount)
            .build();
            Customer customer = new Customer(
                    wallet
            );
            CartinUser cartinUser = new CartinUser().builder()
                    .password(hashPassword(password))
                    .loginStatus(false)
                    .firstName(firstName)
                    .lastName(lastName)
                    .dob(dob)
                    .email(email)
                    .phoneNumber(phone)
                    .address(address)
                    .customer(customer)
                    .admin(new Admin(false))
                    .manager(new Manager(false, null))
                    .role(Role.CUSTOMER)
                    .build();

            cartinUserRepository.save(cartinUser);

            String confirmationToken = jwtUtils.generateConfirmationTokenFromUsername(cartinUser.getEmail());
            emailService.sendConfirmationEmail(cartinUser.getEmail(), confirmationToken);

            return new SignupResponse(cartinUser.getUserId());
        }

        catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cold not signup: " + e.getMessage());
        }
    }

    @Transactional
    public void confirmToken(String token) {
        String email = jwtUtils.getUsernameFromJwt(token);
        CartinUser user = cartinUserRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given email not found.")
        );

        user.setEnabled(true);
        cartinUserRepository.save(user);
    }

    public LoginResponse login(String email, String password){
        CartinUser cartinUser = cartinUserRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist")
        );


        if (!encoder.matches(password, cartinUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Password");
        }

        if (!cartinUser.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is not enabled. Check confirmation email.");
        }

        cartinUser.setLoginStatus(true);
        cartinUserRepository.save(cartinUser);

        return new LoginResponse(
                cartinUser.getUserId(), cartinUser.getRole().name()
        );
    }

    public ResponseEntity<String> logout(Long userId){
        CartinUser cartinUser = cartinUserRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not logged in with current session")
        );

        cartinUser.setLoginStatus(false);
        cartinUserRepository.save(cartinUser);
        return new ResponseEntity<String>("Logged out successfully", HttpStatus.OK);
    }

    public Product getProductById(Long id) {
        Optional<Product> p1 = productRepository.findById(id);
        return p1.get();
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategory(int category) {
        return productRepository.findByCategory(Category.values()[category]);
    }

    public List<Product> getProductsByName(String name) {
        return productRepository.findByFuzzyName(name);
    }

    public List<Product> getProductsInCategoryByPriceRange(int category, double min, double max) {
        return productRepository.findByCategoryAndPriceBetween(Category.values()[category], min, max);
    }

    public CartinUser getUserInfo(Long userId){
        if(checkIfUserLoggedIn(userId)){
            Optional<CartinUser> user = cartinUserRepository.findById(userId);
            return user.get();
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not logged in");
        }
        
    }

    public List<SendCart> getCart(Long userId){
        if(checkIfUserLoggedIn(userId)){
            CartinUser user = cartinUserRepository.findById(userId).get();
            List<CartItem> cartItems =  cartRepository.findByCustomer(user);
            List<SendCart> send = cartItems.stream().map(cartItem -> new SendCart(cartItem.getProduct(), cartItem.getQuantity())).collect(Collectors.toList());
            return send;
    
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not logged in");
        }
        
    }

    public List<SendOrder> getOrders(Long userId){
        if(checkIfUserLoggedIn(userId)){
            CartinUser user = cartinUserRepository.findById(userId).get();
            List<Order> orders = orderRepository.findByCustomer(user);
            List<SendOrder> send = orders.stream().map(order -> new SendOrder(order.getOrderTransactionId(),order.getProduct(), order.getQuantity(), order.getOrderDate())).collect(Collectors.toList());
            return send;
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not logged in");
        }
    }

    public List<SendOrder> getOrdersByOrderDate(Long userId, LocalDate date){
        if(checkIfUserLoggedIn(userId)){
            CartinUser user = cartinUserRepository.findById(userId).get();
            List<Order> orders = orderRepository.findByCustomerAndOrderDate(user, date);
            List<SendOrder> send = orders.stream().map(order -> new SendOrder(order.getOrderTransactionId(),order.getProduct(), order.getQuantity(), order.getOrderDate())).collect(Collectors.toList());
            return send;
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not logged in");
        }
    }

    public List<SendOrder> getOrdersByOrderDateRange(Long userId, LocalDate startDate, LocalDate endDate){
        if(checkIfUserLoggedIn(userId)){
            CartinUser user = cartinUserRepository.findById(userId).get();
            List<Order> orders = orderRepository.findByCustomerAndOrderDateBetween(user, startDate, endDate);
            List<SendOrder> send = orders.stream().map(order -> new SendOrder(order.getOrderTransactionId(),order.getProduct(), order.getQuantity(), order.getOrderDate())).collect(Collectors.toList());
            return send;
        
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not logged in");
        }
    }

    public ResponseEntity<String> updateUserInfo(Long userId, String firstName, String middleName, String lastName, LocalDate dob, String email, String address, String phone){
        if(checkIfUserLoggedIn(userId)){
            CartinUser user = cartinUserRepository.findById(userId).get();
            user.setFirstName(firstName);
            user.setMiddleName(middleName);
            user.setLastName(lastName);
            user.setDob(dob);
            user.setEmail(email);
            user.setAddress(address);
            user.setPhoneNumber(phone);
            cartinUserRepository.save(user);
            return new ResponseEntity<String>("User info updated successfully", HttpStatus.OK);
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not logged in");
        }

    }


    public ResponseEntity<String> deleteUserInfo(Long userId){
        if(checkIfUserLoggedIn(userId)){
            cartinUserRepository.deleteById(userId);
            return new ResponseEntity<String>("User info deleted successfully", HttpStatus.OK);
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not logged in");
        }
    }

    
    public ResponseEntity<String> addToCart(Long userId, Long productId, Integer quantity){
        if(checkIfUserLoggedIn(userId)){
            CartinUser user = cartinUserRepository.findById(userId).get();
            Product product = productRepository.findById(productId).get();
            // check if product is already in cart
            Optional<CartItem> cart = cartRepository.findByCustomerAndProduct(user, product);
            if(cart.isPresent()){
                CartItem newCartItem = cart.get();
                if(newCartItem.getQuantity() + quantity > product.getQuantity()){
                    newCartItem.setQuantity(product.getQuantity());
                    cartRepository.save(newCartItem);
                    throw new ResponseStatusException(HttpStatus.OK, "Quantity set to max available");

                }
                else{
                    newCartItem.setQuantity(newCartItem.getQuantity() + quantity);
                    cartRepository.save(newCartItem);
                }
            }
            else{
                CartItem newCartItem = new CartItem();
                newCartItem.setCustomer(user);
                newCartItem.setProduct(product);
                if(quantity > product.getQuantity()){
                    newCartItem.setQuantity(product.getQuantity());
                    cartRepository.save(newCartItem);
                    throw new ResponseStatusException(HttpStatus.OK, "Quantity set to max available");
                }
                else{
                    newCartItem.setQuantity(quantity);
                    cartRepository.save(newCartItem);
                }

            }
            return new ResponseEntity<String>("Product added to cart successfully", HttpStatus.OK);
            
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not logged in");
        }
    }

    public ResponseEntity<String> removeFromCart(Long userId, Long productId){
        if(checkIfUserLoggedIn(userId)){
            CartinUser user = cartinUserRepository.findById(userId).get();
            Product product = productRepository.findById(productId).get();
            cartRepository.deleteById(cartRepository.findByCustomerAndProduct(user, product).get().getEntryId());
            return new ResponseEntity<String>("Product removed from cart successfully", HttpStatus.OK);
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not logged in");
        }
        
    }

    public ResponseEntity<String> checkOutFromCart(Long userId){
        if(checkIfUserLoggedIn(userId)){
            CartinUser user = cartinUserRepository.findById(userId).get();
            List<CartItem> cartItemList = cartRepository.findByCustomer(user);
            double total = 0;
            for(CartItem cartItem : cartItemList){
                total += (100 - cartItem.getProduct().getOffer())/100 * cartItem.getProduct().getPrice() * cartItem.getQuantity();
            }
            if(total > user.getCustomer().getWallet().getAmount()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
            }
            else{
                for(CartItem cartItem : cartItemList){
                    Order order = new Order().builder()
                            .customer(cartItem.getCustomer())
                            .product(cartItem.getProduct())
                            .quantity(cartItem.getQuantity())
                            .orderDate(LocalDate.now())
                            .build();
                    orderRepository.save(order);
                    cartRepository.deleteById(cartItem.getEntryId());
                    // decrease quantity of product
                    Product product = cartItem.getProduct();
                    product.setQuantity(product.getQuantity() - cartItem.getQuantity());
                    productRepository.save(product);
                }
                user.getCustomer().getWallet().setAmount(user.getCustomer().getWallet().getAmount() - total);
                cartinUserRepository.save(user);
                return new ResponseEntity<String>("Order placed successfully", HttpStatus.OK);
            }
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not logged in");
        }
        
        
    }

    public ResponseEntity<String> topUpWallet(Long userId, double amount){
        if(checkIfUserLoggedIn(userId)){
            if(amount > 0){
                CartinUser user = cartinUserRepository.findById(userId).get();
                user.getCustomer().getWallet().setAmount(user.getCustomer().getWallet().getAmount() + amount);
                cartinUserRepository.save(user);
                return new ResponseEntity<String>("Wallet topped up successfully", HttpStatus.OK);
            }
            else{
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount cannot be negative");
            }
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not logged in");
        }
    }

    public ResponseEntity<String> changeQuantityOfProductInCart(Long userId, Long productId, Integer quantity){

        if(checkIfUserLoggedIn(userId)){
            CartinUser user = cartinUserRepository.findById(userId).get();
            Product product = productRepository.findById(productId).get();
            CartItem cartItem = cartRepository.findByCustomerAndProduct(user, product).get();
            cartItem.setQuantity(quantity);
            cartRepository.save(cartItem);
            return new ResponseEntity<String>("Quantity changed successfully", HttpStatus.OK);
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not logged in");
        }
    }

    private boolean checkIfUserLoggedIn(Long userId){
        CartinUser user = cartinUserRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found.")
        );
        return user.isLoginStatus();
    }

    public ResponseEntity<String> applyAsManager(Long userId){
        if(checkIfUserLoggedIn(userId)){
            CartinUser user = cartinUserRepository.findById(userId).get();
            user.getManager().setManagerApplicationStatus(ManagerStatus.Pending);
            cartinUserRepository.save(user);
            return new ResponseEntity<String>("Application submitted successfully", HttpStatus.OK);
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not logged in");
        }
    }

    public double getWalletBalance(Long userId){
        if(!checkIfUserLoggedIn(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not logged in");
        }

        CartinUser user = cartinUserRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("User not found even after prior check.")
        );
        return user.getCustomer().getWallet().getAmount();
    }

    private String hashPassword(String password){

        return encoder.encode(password);
    }

    public ResponseEntity<String> resetPassword(Long userId, String oldPassword, String newPassword){
        if(!checkIfUserLoggedIn(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not logged in");
        }

        CartinUser user = cartinUserRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("User not found even after prior check.")
        );

        if(!encoder.matches(oldPassword, user.getPassword())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong password entered");

        }

        user.setPassword(hashPassword(newPassword));
        cartinUserRepository.save(user);

        return new ResponseEntity<String>("Password changed successfully", HttpStatus.OK);
    }
}
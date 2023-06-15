package io.cartin.backend.CartinUser.Customer;

import io.cartin.backend.CartinUser.CartinUser;
import io.cartin.backend.Product.Product;
import io.cartin.backend.Stubs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "/user")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @GetMapping(path = "products")
    public @ResponseBody Iterable<Product> getProducts(){
        return customerService.getProducts();
    }

    @GetMapping(path = "products/{id}")
    public @ResponseBody Product getProductById(@PathVariable Long id){
        return customerService.getProductById(id);
    }

    @GetMapping(path = "products/category/{category}")
    public @ResponseBody Iterable<Product> getProductsByCategory(@PathVariable int category){
        return customerService.getProductsByCategory(category);
    }

    @GetMapping(path = "products/name/{name}")
    public @ResponseBody Iterable<Product> getProductsByName(@PathVariable String name){
        return customerService.getProductsByName(name);
    }

    @PostMapping(path = "products/category/priceRange")
    public @ResponseBody Iterable<Product> getProductsInCategoryByPriceRange( @RequestBody GetProduct getProduct){
        return customerService.getProductsInCategoryByPriceRange(getProduct.getCategory(), getProduct.getMin(), getProduct.getMax());
    }

    @GetMapping(path = "/{userId}")
    public @ResponseBody CartinUser getUserInfo(@PathVariable Long userId){
        return customerService.getUserInfo(userId);
    }

    @PostMapping(path = "update/{userId}")
    public ResponseEntity<?> updateUserInfo(@PathVariable Long userId, @RequestBody UserInfo userInfo){
        return customerService.updateUserInfo(userInfo.getUserId(), userInfo.getFirstName(), userInfo.getMiddleName(), userInfo.getLastName(),  userInfo.getDob(),userInfo.getEmail(), userInfo.getAddress(), userInfo.getPhone());
    }

    @PostMapping(path = "/addToCart")
    public ResponseEntity<?> addToCart(@RequestBody GetCart getCart){
        return customerService.addToCart(getCart.getUserId(), getCart.getProductId(), getCart.getQuantity());
    }

    @GetMapping(path = "/{userId}/cart")
    public @ResponseBody List<SendCart> getCart(@PathVariable Long userId){
        return customerService.getCart(userId);
    }

    @PostMapping(path = "update/cart")
    public ResponseEntity<?> updateCart(@RequestBody GetCart getCart){
        return customerService.changeQuantityOfProductInCart(getCart.getUserId(), getCart.getProductId(), getCart.getQuantity());
    }

    @PostMapping(path = "/cart")
    public ResponseEntity<?> deleteFromCart(@RequestBody GetCart getCart){
        return customerService.removeFromCart(getCart.getUserId(), getCart.getProductId());
    }

    @PostMapping(path = "/cart/checkout")
    public ResponseEntity<?> checkout(@RequestBody GetCart getCart){
        return customerService.checkOutFromCart(getCart.getUserId());
    }

    @PostMapping("wallet")
    public ResponseEntity<?> addMoneyToWallet(@RequestBody GetWallet getWallet){
        return customerService.topUpWallet(getWallet.getUserId(), getWallet.getAmount());
    }

    @GetMapping(path = "/{userId}/orders")
    public @ResponseBody List<SendOrder> getOrders(@PathVariable Long userId){
        return customerService.getOrders(userId);
    }

    @PostMapping(path = "/orders")
    public @ResponseBody List<SendOrder> getOrdersByDate(@RequestBody GetOrder getOrder){
        return customerService.getOrdersByOrderDate(getOrder.getUserId(), getOrder.getStartDate());
    }

    @PostMapping(path = "/orders/dateRange")
    public @ResponseBody List<SendOrder> getOrdersByDateRange(@RequestBody GetOrder getOrder){
        return customerService.getOrdersByOrderDateRange(getOrder.getUserId(), getOrder.getStartDate(), getOrder.getEndDate());
    }

    @PostMapping(path = "/login")
    public @ResponseBody LoginResponse login(@RequestBody LoginRequest loginRequest){
        return customerService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @PostMapping(path = "/logout")
    public void logout(@RequestBody LoginRequest loginRequest){
        customerService.logout(loginRequest.getUserId());
    }

    @PostMapping(path = "/signup")
    public @ResponseBody SignupResponse register(@RequestBody SignupRequest signupRequest){
        return customerService.signUp(signupRequest.getPassword(), signupRequest.getFirstName(), signupRequest.getLastName(), signupRequest.getDob(), signupRequest.getEmail(), signupRequest.getAmount(), signupRequest.getAddress(), signupRequest.getPhone());
    }

    @GetMapping(path = "/confirm")
    public String confirm(@RequestParam String token) {
        customerService.confirmToken(token);
        return "Your account has been verified!";
    }

    @PostMapping(path = "/apply/manager")
    public ResponseEntity<?> applyForManager(@RequestBody LoginRequest loginRequest){
        return customerService.applyAsManager(loginRequest.getUserId());
    }

    @PostMapping(path = "/deleteAccount")
    public ResponseEntity<?> deleteAccount(@RequestBody LoginRequest loginRequest){
        return customerService.deleteUserInfo(loginRequest.getUserId());
    }

    @PostMapping(path = "/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest){
        return customerService.resetPassword(passwordChangeRequest.getUserId(), passwordChangeRequest.getOldPassword(), passwordChangeRequest.getNewPassword());
    }


}


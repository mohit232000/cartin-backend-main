package io.cartin.backend.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.cartin.backend.CartinUser.CartinUser;
import io.cartin.backend.CartinUser.CartinUserRepository;
import io.cartin.backend.Stubs.GetEmail;

@RestController
@CrossOrigin
@RequestMapping(path = "/mail")
public class EmailController {
    private final EmailService emailService;
    private final CartinUserRepository cartinUserRepository;

    @Autowired
    public EmailController(EmailService emailService, CartinUserRepository cartinUserRepository) {
        this.emailService = emailService;
        this.cartinUserRepository = cartinUserRepository;
    }

    @PostMapping("/reset/password")
    public void sendMail(@RequestBody GetEmail getEmail) {
        CartinUser user = cartinUserRepository.findByEmail(getEmail.getEmail()).get();
        String password = emailService.generateRandomPassword();
        user.setPassword(emailService.hashPassword(password));
        cartinUserRepository.save(user);
        Email email = Email.builder()
                .to(user.getEmail())
                .subject("Password Reset")
                .body("Your password has been reset to: " + password + "\nPlease change your password after logging in.")
                .build();
        emailService.sendSimpleMail(email);
        
    }
}
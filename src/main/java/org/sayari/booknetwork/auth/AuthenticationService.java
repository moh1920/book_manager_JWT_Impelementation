package org.sayari.booknetwork.auth;


import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.sayari.booknetwork.email.EmailService;
import org.sayari.booknetwork.email.EmailTemplateName;
import org.sayari.booknetwork.role.RoleRepo;
import org.sayari.booknetwork.user.Token;
import org.sayari.booknetwork.user.TokenRepo;
import org.sayari.booknetwork.user.User;
import org.sayari.booknetwork.user.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepo roleRepo ;
    private final PasswordEncoder passwordEncoder ;
    private  final UserRepo userRepo ;
    private final TokenRepo tokenRepo;
    private final EmailService emailService ;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(ResgistrationRequest request) throws MessagingException {
        var userRole =roleRepo.findByName("USER")
                .orElseThrow(()->new IllegalStateException("Role User was Note initialized"));
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepo.save(user);
        sendValidationEmail(user);

    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

         // send email with token
        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken
                        .toString(),
                "Account Activation"
        );
    }

    private Object generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepo.save(token);
        return  generatedToken ;

    }

    private String generateActivationCode(int length) {
        String caractres = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for(int i=0;i<length;i++){
            int randomIndex= secureRandom.nextInt(caractres.length());
            codeBuilder.append(caractres.charAt(randomIndex));

        }
        return codeBuilder.toString();

    }

}

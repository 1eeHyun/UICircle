package edu.uic.marketplace.service.email;

public interface EmailService {

    void sendVerificationEmail(String toEmail, String verificationToken);

    void sendPasswordResetEmail(String toEmail, String resetToken);

    void sendEmail(String to, String subject, String body);
}

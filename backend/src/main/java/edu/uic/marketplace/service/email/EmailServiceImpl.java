package edu.uic.marketplace.service.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final SendGrid sendGrid;

    @Value("${app.mail.from-email}")
    private String fromEmail;

    @Value("${app.mail.frontend-url}")
    private String frontendUrl;

    @Override
    public void sendVerificationEmail(String toEmail, String verificationToken) {
        String verifyLink = frontendUrl + "/verify-email?token=" + verificationToken;

        String subject = "UICircle - Verify Your Email";
        String body = buildVerificationEmailBody(verifyLink);

        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        String subject = "UIC Marketplace - Reset Your Password";
        String body = buildPasswordResetEmailBody(resetLink);

        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendEmail(String to, String subject, String body) {

        try {
            log.info("Sending email to {}, subject={}", to, subject);
            Email from = new Email(fromEmail, "UICircle");
            Email toEmail = new Email(to);
            Content content = new Content("text/html", body);

            Mail mail = new Mail(from, subject, toEmail, content);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            log.info("SendGrid response: status={}, body={}, headers={}",
                    response.getStatusCode(), response.getBody(), response.getHeaders());

            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("Failed to send email via SendGrid, status=" + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildVerificationEmailBody(String verifyLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #cc0000; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 30px; }
                        .button { display: inline-block; padding: 12px 30px; background-color: #cc0000; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>UIC Marketplace</h1>
                        </div>
                        <div class="content">
                            <h2>Welcome to UIC Marketplace!</h2>
                            <p>Thank you for signing up. Please verify your email address by clicking the button below:</p>
                            <a href="%s" class="button">Verify Email</a>
                            <p>Or copy and paste this link into your browser:</p>
                            <p style="word-break: break-all; color: #666;">%s</p>
                            <p>This link will expire in 24 hours.</p>
                            <p>If you didn't create an account, you can safely ignore this email.</p>
                        </div>
                        <div class="footer">
                            <p>© 2025 UIC Marketplace. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(verifyLink, verifyLink);
    }

    private String buildPasswordResetEmailBody(String resetLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #cc0000; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 30px; }
                        .button { display: inline-block; padding: 12px 30px; background-color: #cc0000; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>UIC Marketplace</h1>
                        </div>
                        <div class="content">
                            <h2>Password Reset Request</h2>
                            <p>We received a request to reset your password. Click the button below to create a new password:</p>
                            <a href="%s" class="button">Reset Password</a>
                            <p>Or copy and paste this link into your browser:</p>
                            <p style="word-break: break-all; color: #666;">%s</p>
                            <p>This link will expire in 1 hour.</p>
                            <p>If you didn't request a password reset, you can safely ignore this email.</p>
                        </div>
                        <div class="footer">
                            <p>© 2025 UIC Marketplace. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(resetLink, resetLink);
    }
}

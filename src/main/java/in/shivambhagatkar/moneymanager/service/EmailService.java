package in.shivambhagatkar.moneymanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.Base64;

@Service
public class EmailService {

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    private static final String SENDGRID_URL = "https://api.sendgrid.com/v3/mail/send";

    // ✅ existing method (keep as is)
    public void sendEmail(String to, String subject, String body) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, String> from = new HashMap<>();
            from.put("email", "shivambhagatkar34@gmail.com");
            from.put("name", "Money Manager App");

            Map<String, String> toMap = new HashMap<>();
            toMap.put("email", to);

            Map<String, String> contentMap = new HashMap<>();
            contentMap.put("type", "text/html");
            contentMap.put("value", body);

            Map<String, Object> message = new HashMap<>();
            message.put("personalizations", List.of(Map.of("to", List.of(toMap))));
            message.put("from", from);
            message.put("subject", subject);
            message.put("content", List.of(contentMap));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(sendGridApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(SENDGRID_URL, request, String.class);

            if (response.getStatusCode() == HttpStatus.ACCEPTED) {
                System.out.println("✅ Email sent successfully to " + to);
            } else {
                System.out.println("⚠️ Email failed: " + response.getBody());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Error sending email via SendGrid: " + e.getMessage());
        }
    }

    // ✅ NEW METHOD (for Excel or other file attachments)
    public void sendEmailWithAttachment(
            String to,
            String subject,
            String body,
            byte[] attachmentData,
            String attachmentFilename
    ) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, String> from = new HashMap<>();
            from.put("email", "shivambhagatkar34@gmail.com");
            from.put("name", "Money Manager App");

            Map<String, String> toMap = new HashMap<>();
            toMap.put("email", to);

            Map<String, String> contentMap = new HashMap<>();
            contentMap.put("type", "text/html");
            contentMap.put("value", body);

            // ✅ Add attachment (Base64 encoded)
            String base64Attachment = Base64.getEncoder().encodeToString(attachmentData);
            Map<String, Object> attachment = new HashMap<>();
            attachment.put("content", base64Attachment);
            attachment.put("type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            attachment.put("filename", attachmentFilename);
            attachment.put("disposition", "attachment");

            Map<String, Object> message = new HashMap<>();
            message.put("personalizations", List.of(Map.of("to", List.of(toMap))));
            message.put("from", from);
            message.put("subject", subject);
            message.put("content", List.of(contentMap));
            message.put("attachments", List.of(attachment)); // ✅ Added attachment list

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(sendGridApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(SENDGRID_URL, request, String.class);

            if (response.getStatusCode() == HttpStatus.ACCEPTED) {
                System.out.println("✅ Email with attachment sent successfully to " + to);
            } else {
                System.out.println("⚠️ Email with attachment failed: " + response.getBody());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Error sending email with attachment via SendGrid: " + e.getMessage());
        }
    }
}

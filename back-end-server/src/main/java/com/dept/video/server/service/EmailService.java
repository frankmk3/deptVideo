package com.dept.video.server.service;

import com.dept.video.server.common.MessagesUtility;
import com.dept.video.server.common.TemplateUtility;
import com.dept.video.server.dto.Message;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class EmailService {

    private final TemplateUtility templateUtility;

    private final MessagesUtility messagesUtility;

    private final JavaMailSender javaMailSender;

    @Value("${template.base.email}")
    private String templateBaseEmail;

    @Value("${mail.from}")
    private String mailFrom;

    @Autowired
    public EmailService(TemplateUtility templateUtility, MessagesUtility messagesUtility, JavaMailSender javaMailSender) {
        this.templateUtility = templateUtility;
        this.messagesUtility = messagesUtility;
        this.javaMailSender = javaMailSender;
    }

    /**
     * Send email messages
     */
    public void sendMessage(Message message) throws TemplateException, IOException, MessagingException {
        try {
            message.setBaseTemplate(templateBaseEmail);

            String body = templateUtility.parseTemplateAsString(message.getTemplateId(), message.getContent());
            Map<String, Object> baseContent = message.getBaseContent() != null ? message.getBaseContent() : new HashMap<>();
            baseContent.put("content", body);
            body = templateUtility.parseTemplateAsString(message.getBaseTemplate(), baseContent);

            List<InternetAddress> addresses = new ArrayList<>();
            for (String s : message.getTo()) {
                try {
                    InternetAddress address = new InternetAddress(s);
                    address.validate();
                    addresses.add(address);
                } catch (AddressException ex) {
                    log.error(messagesUtility.getMessage("exception.mail.sender.invalid.address"), ex);
                    throw ex;
                }
            }
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(addresses.toArray(new InternetAddress[0]));
            helper.setFrom(mailFrom);
            helper.setSubject(message.getSubject());
            helper.setText(body, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException | IOException | TemplateException | MailException e) {
            log.error(messagesUtility.getMessage("exception.mail.sender"), e);
            throw e;
        }
    }
}

package com.dept.video.server.service;

import com.dept.video.server.common.MessagesUtility;
import com.dept.video.server.common.QueryUtil;
import com.dept.video.server.dto.FingerPrint;
import com.dept.video.server.dto.Message;
import com.dept.video.server.dto.PaginatedResponse;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.model.UserLogin;
import com.dept.video.server.repository.UserLoginRepository;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class UserLoginService {

    private static final String COOKIE_LOGIN = "last_acc_";

    private final MongoOperations mongoOperations;

    private final QueryUtil queryUtil;

    private final UserLoginRepository userLoginRepository;

    private final EmailService emailService;

    private final MessagesUtility messagesUtility;

    @Value("${template.user.new.access}")
    private String templateUserNewAccess;

    @Autowired
    public UserLoginService(MongoOperations mongoOperations, QueryUtil queryUtil, UserLoginRepository userLoginRepository,
                            EmailService emailService, MessagesUtility messagesUtility) {
        this.mongoOperations = mongoOperations;
        this.queryUtil = queryUtil;
        this.userLoginRepository = userLoginRepository;
        this.emailService = emailService;
        this.messagesUtility = messagesUtility;
    }

    @Async
    public UserLogin create(String email, UserLogin userLogin, boolean notify) {
        if (notify) {
            long previous = userLoginRepository.countByUserIdAndFingerPrint_Hash(userLogin.getUserId(), userLogin.getFingerPrint().getHash());
            if (previous == 0) {
                userLogin.setNotified(notifyLogin(email, userLogin));
            }
        }
        return userLoginRepository.save(userLogin);
    }

    public PaginatedResponse<UserLogin> getAll(String q, Optional<String[]> orders, Optional<String> fields, Pageable pageable) throws QueryException {
        Query query = queryUtil.buildQuery(q, null, pageable, orders, fields);

        List<UserLogin> content = mongoOperations.find(query, UserLogin.class);
        long count = mongoOperations.count(query, UserLogin.class);

        return queryUtil.getPaginatedResponse(content, count, pageable);
    }

    public Map<String, Object> generateLoginInformationProperties(String userId) {
        String md5Hex = DigestUtils.md5Hex(COOKIE_LOGIN + userId).toUpperCase();
        Map<String, Object> properties = new HashMap<>();
        properties.put(md5Hex, String.valueOf(System.currentTimeMillis()));
        return properties;
    }

    public boolean loginPresent(String userId, FingerPrint fingerPrint) {
        boolean present = false;
        if (fingerPrint != null && fingerPrint.getData() != null
                && fingerPrint.getData().containsKey("properties")) {
            String md5Hex = DigestUtils.md5Hex(COOKIE_LOGIN + userId).toUpperCase();
            List<Map> properties = (List<Map>) fingerPrint.getData().get("properties");
            present = properties.stream().anyMatch(c -> String.valueOf(c.get("key")).equals("P_" + md5Hex));
        }
        return present;
    }

    private boolean notifyLogin(String email, UserLogin userLogin) {
        Map content = new HashMap();
        String account = email;
        if (!email.equals(userLogin.getUserId())) {
            account = userLogin.getUserId() + " (" + email + ")";
        }
        content.put("account", account);
        content.put("data", userLogin.getFingerPrint().getData());

        ArrayList<String> to = new ArrayList<>();
        to.add(email);
        Message message = new Message(templateUserNewAccess, messagesUtility.getMessage("account.activity.notification"), to, content, new HashMap<>());
        try {
            emailService.sendMessage(message);
        } catch (TemplateException | MessagingException | IOException e) {
            log.error("error sending account activity message", e);
            return false;
        }
        return true;
    }
}
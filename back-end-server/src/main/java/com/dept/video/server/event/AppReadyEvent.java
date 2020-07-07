package com.dept.video.server.event;

import com.dept.video.server.enums.Role;
import com.dept.video.server.model.User;
import com.dept.video.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppReadyEvent implements ApplicationListener<ApplicationReadyEvent> {

    private final UserService userService;

    @Value("${jwt.user.root.user}")
    private String rootUser;

    @Value("${jwt.user.root.pass}")
    private String rootPass;

    @Value("${jwt.user.root.name}")
    private String rootName;

    @Value("${jwt.user.root.mail}")
    private String rootEmail;

    @Autowired
    public AppReadyEvent(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates the default user
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        User user = userService.getById(rootUser);
        if (user == null) {
            user = new User();
            user.setId(rootUser);
            user.setName(rootName);
            user.setEmail(rootEmail);
            user.setAdmin(true);
            user.setRole(Role.ROLE_ADMIN);
            userService.createUnchecked(user, rootPass);
        }
    }
}

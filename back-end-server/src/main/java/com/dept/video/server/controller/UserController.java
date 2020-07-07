package com.dept.video.server.controller;

import com.dept.video.server.common.Constants;
import com.dept.video.server.common.MessagesUtility;
import com.dept.video.server.common.SecurityUtility;
import com.dept.video.server.dto.*;
import com.dept.video.server.enums.Role;
import com.dept.video.server.enums.UserSource;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.model.User;
import com.dept.video.server.model.UserLogin;
import com.dept.video.server.model.VerificationToken;
import com.dept.video.server.service.EmailService;
import com.dept.video.server.service.UserLoginService;
import com.dept.video.server.service.UserService;
import com.dept.video.server.service.VerificationTokenService;
import freemarker.template.TemplateException;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static com.dept.video.server.common.Constants.*;

/**
 * Controller class to manage user information
 */
@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private static final String LABEL = "Users";
    private static final String PASS = "password";

    private final UserService userService;

    private final UserLoginService userLoginService;

    private final MessagesUtility messagesUtility;

    private final Constants constants;

    private final VerificationTokenService verificationTokenService;

    private final EmailService emailService;

    @Value("${template.user.password.change}")
    private String templateUserPasswordChange;

    @Autowired
    public UserController(UserService userService, UserLoginService userLoginService, MessagesUtility messagesUtility
            , Constants constants, VerificationTokenService verificationTokenService, EmailService emailService) {
        this.userService = userService;
        this.userLoginService = userLoginService;
        this.messagesUtility = messagesUtility;
        this.constants = constants;
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
    }

    /**
     * Get users information
     *
     * @return a paged response with status 200 and the resultant entity collection.
     * In case of bad query parameter, 400. In case of bad credentials, 401
     */
    @ApiOperation(value = "Get users", tags = LABEL)
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity getUsers(@RequestParam(name = "q", required = false) String q,
                                   @RequestParam(name = "order", required = false) final String[] order,
                                   @RequestParam(name = "fields", required = false) String fields,
                                   @RequestParam(name = "page", required = false, defaultValue = "-1") final int page,
                                   @RequestParam(name = "size", required = false, defaultValue = "-1") final int size) {
        ResponseEntity responseEntity;
        final int requestPage = page < 0 ? constants.getPaginatorPage() : page;
        final int requestSize = size <= 0 ? constants.getPaginatorSize() : size;
        final Pageable pageable = PageRequest.of(requestPage, requestSize);

        try {
            responseEntity = new ResponseEntity(
                    userService.getAll(
                            q,
                            Optional.ofNullable(order),
                            Optional.ofNullable(fields),
                            pageable
                    ), HttpStatus.OK);
        } catch (QueryException e) {
            responseEntity = new ResponseEntity(
                    Response.builder().
                            status(HttpStatus.BAD_REQUEST.value()).
                            message(messagesUtility.getMessage("bad.request")).build()
                    , HttpStatus.BAD_REQUEST);
            log.error(e.getMessage(), e);
        }
        return responseEntity;
    }

    /**
     * Get user login history
     *
     * @return a paged response with status 200 and the resultant entity collection.
     * In case of bad query parameter, 400. In case of bad credentials, 401
     */
    @ApiOperation(value = "Get user login history", tags = LABEL)
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @GetMapping("/login-history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getUserLoginHistory(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "order", required = false) final String[] order,
            @RequestParam(name = "fields", required = false) String fields,
            @RequestParam(name = "page", required = false, defaultValue = "-1") final int page,
            @RequestParam(name = "size", required = false, defaultValue = "-1") final int size
    ) {
        ResponseEntity responseEntity;
        final int requestPage = page < 0 ? constants.getPaginatorPage() : page;
        final int requestSize = size <= 0 ? constants.getPaginatorSize() : size;
        final Pageable pageable = PageRequest.of(requestPage, requestSize);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (!SecurityUtility.hasRole(auth, Role.ROLE_ADMIN)) {
                stringBuilder.append("userId:").append(auth.getName());
                if (!StringUtils.isEmpty(q)) {
                    stringBuilder.append(" AND ").append(q);
                }
            }
            final PaginatedResponse<UserLogin> userLoginHistory = userLoginService.getAll(
                    stringBuilder.toString(),
                    Optional.ofNullable(order),
                    Optional.ofNullable(fields),
                    pageable
            );
            responseEntity = new ResponseEntity(userLoginHistory, HttpStatus.OK);
        } catch (QueryException e) {
            responseEntity = new ResponseEntity(
                    Response.builder().
                            status(HttpStatus.BAD_REQUEST.value()).
                            message(messagesUtility.getMessage("bad.request")).build()
                    , HttpStatus.BAD_REQUEST);
            log.error(e.getMessage(), e);
        }
        return responseEntity;
    }

    /**
     * Get user by id
     *
     * @param userId
     * @return a response with status 200 and the resultant entity. In case wrong user id, 404. In case of bad credentials, 401;
     */
    @ApiOperation(value = "Get user", tags = LABEL)
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @GetMapping("/{userId:.+}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity getUser(@PathVariable(name = "userId") final String userId) {
        ResponseEntity responseEntity;
        final User user = userService.getById(userId);
        if (user != null) {
            responseEntity = new ResponseEntity(user, HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity(
                    Response.builder().
                            status(HttpStatus.NOT_FOUND.value()).
                            message(messagesUtility.getMessage("not.found")).build()
                    , HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * Create user data
     *
     * @param user
     * @return a response with status 201 and the resultant entity. In case of bad credentials, 401. In case of duplicate user id, 409;
     */
    @ApiOperation(value = "Create user", tags = LABEL)
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity createUser(@RequestBody User user) {
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
        if (!StringUtils.isEmpty(user.getId())) {
            User existingUser = userService.getById(user.getId());
            if (existingUser == null) {
                user.setVersion(UUID.randomUUID().toString());
                responseEntity = new ResponseEntity(userService.create(user, user.getPass()), HttpStatus.CREATED);
            } else {
                responseEntity = new ResponseEntity(
                        Response.builder().
                                status(HttpStatus.CONFLICT.value()).
                                message(messagesUtility.getMessage("duplicate.resource")).build()
                        , HttpStatus.CONFLICT);
            }
        }
        return responseEntity;
    }

    /**
     * Change the password for an user
     *
     * @param userId
     * @param userChangePassword
     * @return a response with status 200 and the resultant entity. In case of different user id, 403. In case of bad credentials, 401;
     */
    @ApiOperation(value = "Change user password", tags = LABEL)
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @PatchMapping("/{userId:.+}/changePassword")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity changeUserPassword(
            @PathVariable(name = "userId") final String userId,
            @RequestBody UserChangePassword userChangePassword
    ) {
        ResponseEntity responseEntity;
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String username = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
        if (username.equals(userChangePassword.getUserId()) && username.equals(userId)) {
            final User user = userService.getById(username);
            responseEntity = new ResponseEntity(userService.changeUserPassword(user, userChangePassword), HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity(Response.builder().
                    status(HttpStatus.FORBIDDEN.value()).
                    message(messagesUtility.getMessage("wrong.input.data")).build()
                    , HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }

    /**
     * Update user information
     *
     * @return a response with status 200 and the resultant entity. In case wrong user id, 404. In case of bad credentials, 401;
     */
    @ApiOperation(value = "Update user", tags = LABEL)
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @PutMapping("/{userId:.+}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity updateUser(@PathVariable(name = "userId") final String userId,
                                     @RequestBody User user) {
        ResponseEntity responseEntity;
        final User current = userService.getByIdAllStatus(userId);
        if (current != null) {
            user.setAdmin(current.isAdmin());
            user.setSource(current.getSource());
            user.setId(current.getId());
            user.setVersion(current.getVersion());
            responseEntity = new ResponseEntity(userService.update(user, user.getPass(), current.getPass()), HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity(
                    Response.builder().
                            status(HttpStatus.NOT_FOUND.value()).
                            message(messagesUtility.getMessage("wrong.input.data")).build()
                    , HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * Update authenticated user information
     *
     * @return a response with status 200 and the resultant entity.
     * In case wrong user id, 404. In case of different user id, 403.
     * In case of bad credentials, 401;
     */
    @ApiOperation(value = "Update own user info", tags = LABEL)
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity updateOwnUserInfo(@RequestBody User user) {
        ResponseEntity responseEntity;

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getName().equals(user.getId())) {
            final User current = userService.getByIdAllStatus(user.getId());
            if (current != null) {
                user.setAdmin(current.isAdmin());
                user.setSource(current.getSource());
                user.setVersion(current.getVersion());
                responseEntity = new ResponseEntity(userService.update(user, user.getPass(), current.getPass()), HttpStatus.OK);
            } else {
                responseEntity = new ResponseEntity(
                        Response.builder().
                                status(HttpStatus.NOT_FOUND.value()).
                                message(messagesUtility.getMessage("wrong.input.data")).build()
                        , HttpStatus.NOT_FOUND);
            }
        } else {
            responseEntity = new ResponseEntity(Response.builder().
                    status(HttpStatus.FORBIDDEN.value()).
                    message(messagesUtility.getMessage("wrong.input.data")).build()
                    , HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }

    /**
     * Invalidate user sessions, all the existing token for this user will expire
     *
     * @return a response with status 200 and the resultant entity.
     * In case wrong user id, 404. In case of bad credentials, 401;
     */
    @ApiOperation(value = "Invalidate user sessions", tags = LABEL)
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @PutMapping("/{userId:.+}/invalidateSessions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity invalidateUserSessions(
            @PathVariable(name = "userId") final String userId,
            @RequestParam(name = "lock",
                    required = false,
                    defaultValue = "false"
            ) final boolean lock) {
        ResponseEntity responseEntity = null;
        final User user = userService.getById(userId);
        if (user != null) {
            user.setVersion(UUID.randomUUID().toString());
            user.setEnabled(!lock);
            responseEntity = new ResponseEntity(userService.update(user, null, user.getPass()), HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity(
                    Response.builder().
                            status(HttpStatus.NOT_FOUND.value()).
                            message(messagesUtility.getMessage("wrong.input.data")).build()
                    , HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * Remove user from the database
     *
     * @return a response with status 204. In case of bad credentials, 401 and 404 for no results in case of device entity
     */
    @ApiOperation(value = "Delete user", tags = LABEL)
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @DeleteMapping("/{userId:.+}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity deleteUser(@PathVariable(name = "userId") final String userId) {
        ResponseEntity responseEntity;
        final User user = userService.getById(userId);
        if (user != null) {
            userService.delete(user);
            responseEntity = new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            responseEntity = new ResponseEntity(
                    Response.builder().
                            status(HttpStatus.NOT_FOUND.value()).
                            message(messagesUtility.getMessage("wrong.input.data")).build()
                    , HttpStatus.NOT_FOUND);
        }

        return responseEntity;
    }

    /**
     * User login function.
     *
     * @return a response with status 200 and the resultant entity.
     * In case of bad credentials, 401;
     */
    @ApiOperation(value = "Login", tags = LABEL)
    @PostMapping("/login")
    public ResponseEntity authenticateUser(@RequestBody User user) {
        ResponseEntity responseEntity;
        user.setSource(UserSource.INTERNAL.getValue());
        final UserInfo login = userService.login(user);
        if (login != null) {
            userLoginService.create(
                    login.getEmail(),
                    UserLogin.builder().
                            userId(user.getId()).
                            fingerPrint(user.getFingerPrint()).
                            build(),
                    !userLoginService.
                            loginPresent(
                                    user.getId(),
                                    user.getFingerPrint()
                            )
            );
            login.setProperties(Optional.ofNullable(login.getProperties()).orElse(new HashMap<>()));
            login.getProperties().putAll(userLoginService.generateLoginInformationProperties(user.getId()));
            responseEntity = new ResponseEntity(login, HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity(
                    Response.builder().
                            status(HttpStatus.UNAUTHORIZED.value()).
                            message(messagesUtility.getMessage("wrong.input.data")).build()
                    , HttpStatus.UNAUTHORIZED);
        }
        return responseEntity;
    }

    /**
     * Update token expiration
     *
     * @return a response with status 200 and the resultant entity. In case of bad credentials, 401;
     */
    @ApiOperation(value = "Refresh token", tags = LABEL)
    @PostMapping("/refresh-token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity authenticateUser(HttpServletRequest request) {
        return new ResponseEntity(userService.refreshToken(request), HttpStatus.OK);
    }

    /**
     * Verify id the given email is in use
     *
     * @return a response with status 200 and the resultant entity. In case of bad credentials, 401;
     */
    @ApiOperation(value = "Check email availability", tags = LABEL)
    @GetMapping("/checkEmailAvailability")
    public ResponseEntity checkEmailAvailability(@RequestParam(name = "email") String email) {
        return new ResponseEntity(userService.checkEmailAvailability(email), HttpStatus.OK);
    }

    /**
     * Send proper email with the steps to get the new password.
     *
     * @return a response with status 200 and the resultant entity. In case of bad credentials, 401;
     */
    @ApiOperation(value = "Forgot password", tags = LABEL)
    @PostMapping("/password/forgot")
    public ResponseEntity forgotPassword(@RequestBody User user) {
        String email = user.getEmail();
        email = email != null ? email.toLowerCase() : null;
        final User storedUser = userService.getByIdAndSourceAllStatus(email, UserSource.INTERNAL.getValue());
        if (storedUser != null && !StringUtils.isEmpty(storedUser.getName())) {
            final String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(token, email, PASS);
            verificationToken = verificationTokenService.create(verificationToken);
            try {
                final List<String> to = new ArrayList<>();
                to.add(email);
                final Map<String, Object> content = new HashMap<>();
                content.put("content", "");
                content.put("verificationToken", verificationToken);
                final Message message = new Message(templateUserPasswordChange,
                        messagesUtility.getMessage("password.change.request"), to, content, null);
                emailService.sendMessage(message);
            } catch (TemplateException | IOException | MessagingException e) {
                log.error(e.getMessage(), e);
            }
        }
        return new ResponseEntity(new UserIdentityAvailability(true), HttpStatus.OK);
    }

    /**
     * Check if the token is valid for the password request
     *
     * @return a response with status 200 and the resultant entity. In case of bad credentials, 401;
     */
    @ApiOperation(value = "Password token validation", tags = LABEL)
    @PostMapping("/password/token/{token:.+}")
    public ResponseEntity passwordTokenValidation(@PathVariable(name = "token") String token) {
        final VerificationToken verificationToken = verificationTokenService.getValidTokenByIdAndUserAndType(token, PASS);
        return new ResponseEntity(new UserIdentityAvailability(verificationToken != null), HttpStatus.OK);
    }

    /**
     * Check if the token is valid for the new password request
     *
     * @return a response with status 200 and the resultant entity. In case of bad credentials, 401;
     */
    @ApiOperation(value = "Password change validation", tags = LABEL)
    @PostMapping("/password/new/{token:.+}")
    public ResponseEntity passwordChange(@PathVariable(name = "token") String token, @RequestBody User user) {
        VerificationToken verificationToken = verificationTokenService.getValidTokenByIdAndUserAndType(token, PASS);
        boolean available = verificationToken != null;
        if (available) {
            User storedUser = userService.getByIdAllStatus(verificationToken.getUserId());
            if (storedUser != null) {
                storedUser.setEnabled(true);
                userService.update(storedUser, user.getPassword(), storedUser.getPassword());
            } else {
                available = false;
            }
        }
        return new ResponseEntity(new UserIdentityAvailability(available), HttpStatus.OK);
    }

    /**
     * Get filters operation for the users
     *
     * @return a response with status 200 and the resultant entity. In case of bad credentials, 401;
     */
    @ApiOperation(value = "Get filters", tags = LABEL)
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @RequestMapping(value = "/filters", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getFilters() {
        return new ResponseEntity(userService.getFilters(), HttpStatus.OK);
    }
}
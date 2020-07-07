package com.dept.video.server.service;

import com.dept.video.server.common.Constants;
import com.dept.video.server.common.QueryUtil;
import com.dept.video.server.dto.*;
import com.dept.video.server.enums.UserSource;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.model.User;
import com.dept.video.server.repository.UserRepository;
import com.dept.video.server.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private final MongoOperations mongoOperations;

    private final QueryUtil queryUtil;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    private final Constants constants;

    @Autowired
    public UserService(MongoOperations mongoOperations, QueryUtil queryUtil, UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, Constants constants) {
        this.mongoOperations = mongoOperations;
        this.queryUtil = queryUtil;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.constants = constants;
    }

    public User getById(String id) {
        return userRepository.findByIdAndEnabledIsTrue(id);
    }

    public User getByIdAllStatus(String id) {
        return userRepository.findOneById(id);
    }


    public User getByIdAndSourceAllStatus(String id, String source) {
        return userRepository.findOneByIdAndSource(id, source);
    }

    public User create(User user, String password) {
        user.setAdmin(false);
        return createUnchecked(user, password);
    }

    public User createUnchecked(User user, String password) {
        if (!StringUtils.isEmpty(password)) {
            user.setPass(bCryptPasswordEncoder.encode(password));
        } else {
            user.setPass(null);
        }
        if (StringUtils.isEmpty(user.getEmail())) {
            user.setEmail(user.getId());
        }
        return userRepository.save(user);
    }

    public User update(User user, String newPassword, String currentPassword) {
        if (!StringUtils.isEmpty(newPassword)) {
            user.setPass(bCryptPasswordEncoder.encode(newPassword));
        } else {
            user.setPass(currentPassword);
        }
        return userRepository.save(user);
    }

    public User delete(User user) {
        userRepository.delete(user);
        return user;
    }

    public User changeUserPassword(User user, UserChangePassword userChangePassword) {
        String oldPass = userChangePassword.getOldPass();
        if (!StringUtils.isEmpty(oldPass)) {
            user.setPass(oldPass);
            if (login(user) != null) {
                return update(user, userChangePassword.getPass(), oldPass);
            }
        }
        throw new UsernameNotFoundException(user.getEmail());
    }

    public PaginatedResponse<User> getAll(String q, Optional<String[]> orders, Optional<String> fields, Pageable pageable) throws QueryException {
        Query query = queryUtil.buildQuery(q, null, pageable, orders, fields);

        List<User> content = mongoOperations.find(query, User.class);
        long count = mongoOperations.count(query, User.class);

        return queryUtil.getPaginatedResponse(content, count, pageable);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getById(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        List<GrantedAuthority> authorities = new ArrayList<>(user.getAuthorities());
        return new org.springframework.security.core.userdetails.User(user.getId(), user.getPass() != null ? user.getPass() : "", authorities);
    }

    public UserDetails loadUserByUsernameAndVersion(String username, String version) throws UsernameNotFoundException {
        User user = getById(username);
        if (user == null || !String.valueOf(user.getVersion()).equals(String.valueOf(version))) {
            throw new UsernameNotFoundException(username);
        }
        List<GrantedAuthority> authorities = new ArrayList<>(user.getAuthorities());
        return new org.springframework.security.core.userdetails.User(user.getId(), user.getPass() != null ? user.getPass() : "", authorities);
    }

    public String refreshToken(HttpServletRequest req) {
        String token = jwtTokenProvider.resolveToken(req);
        String username = jwtTokenProvider.getUsername(token);
        String userVersion = jwtTokenProvider.getUserVersion(token);
        return constants.getJwtTokePrefix() + jwtTokenProvider.createToken(username, userVersion);
    }

    public UserInfo login(User user) {
        UserInfo.UserInfoBuilder userInfoBuilder = UserInfo.builder();
        User current = getById(user.getId());
        if (current != null) {
            userInfoBuilder.id(current.getId());
            userInfoBuilder.name(current.getName());
            userInfoBuilder.email(current.getEmail());
            userInfoBuilder.source(current.getSource());
            userInfoBuilder.lang(current.getLang());
            userInfoBuilder.properties(current.getProperties());
            userInfoBuilder.role(current.getRole().name());

            if (!UserSource.EXTERNAL.getValue().equals(user.getSource())) {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getId(), user.getPass()));
            }
            userInfoBuilder.token(constants.getJwtTokePrefix() + jwtTokenProvider.createToken(user.getId(), current.getVersion()));
            return userInfoBuilder.build();
        } else {
            return null;
        }
    }

    public UserIdentityAvailability checkEmailAvailability(String email) {
        return new UserIdentityAvailability(userRepository.findByIdAndEnabledIsTrueOrNameNotNull(email) == null);
    }

    public Map<String, List<FilterItem>> getFilters() {
      return new HashMap();
    }

}
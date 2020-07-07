package com.dept.video.server.security;

import com.dept.video.server.common.Constants;
import com.dept.video.server.model.User;
import com.dept.video.server.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

public class JwtTokenProviderTest {

    private static final String USER_NAME = "userName";
    private static final String VERSION = "version";
    private JwtTokenProvider jwtTokenProvider;

    private UserService userService;
    private Constants constants;

    @Before
    public void init() {
        userService = Mockito.mock(UserService.class);
        constants = Mockito.mock(Constants.class);
        Mockito.when(constants.getJwtSecret()).thenReturn("secret");
        Mockito.when(constants.getJwtExpirationTime()).thenReturn(259200000L);
        jwtTokenProvider = new JwtTokenProvider(constants);
        jwtTokenProvider.setUserService(userService);
    }

    @Test
    public void whenValidUserNameAndPasswordThenReturnsToken() {
        String token = jwtTokenProvider.createToken(USER_NAME, VERSION);

        Assert.assertNotNull(token);
    }

    @Test
    public void whenValidUserNameAndPasswordThenReturnsAuthenticationWithProperValues() {
        String token = jwtTokenProvider.createToken(USER_NAME, VERSION);
        User user = new User();
        user.setName("User name");
        Mockito.when(userService.loadUserByUsernameAndVersion(USER_NAME, VERSION)).thenReturn(user);

        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        Assert.assertEquals(((User) authentication.getPrincipal()).getName(), user.getName());
    }
}
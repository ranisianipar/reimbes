package com.reimbes;

import com.reimbes.authentication.rest.RESTAuthenticationEntryPoint;
import com.reimbes.authentication.rest.RESTAuthenticationFailureHandler;
import com.reimbes.authentication.rest.RESTAuthenticationSuccessHandler;
import com.reimbes.configuration.WebSecurity;
import com.reimbes.implementation.AdminServiceImpl;
import com.reimbes.implementation.AuthServiceImpl;
import com.reimbes.implementation.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AdminController.class)
@Import(WebSecurity.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AdminServiceImpl adminService;

    @MockBean
    private AuthServiceImpl authService;

    @MockBean
    private RESTAuthenticationEntryPoint authenticationEntryPoint;

    @MockBean
    private RESTAuthenticationFailureHandler authenticationFailureHandler;

    @MockBean
    private RESTAuthenticationSuccessHandler authenticationSuccessHandler;

    @MockBean
    private UserServiceImpl userService;

    @Test
    public void givenListOfUsers_whenGetAllUsers() throws Exception {
        ReimsUser user = new ReimsUser();
        user.setId(0);
        user.setPassword("!@#qwe");
        user.setUsername("zzz");
        user.setRole(ReimsUser.Role.ADMIN);

        mvc.perform(get("/api/admin/users")
                .contentType(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data", hasSize(1)))
                .andExpect(jsonPath("data[0].username", is(user.getUsername())));
    }




}

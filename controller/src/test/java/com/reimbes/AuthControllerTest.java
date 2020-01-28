package com.reimbes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reimbes.response.UserResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.reimbes.constant.SecurityConstants.HEADER_STRING;
import static com.reimbes.constant.SecurityConstants.TOKEN_PREFIX;
import static com.reimbes.constant.UrlConstants.API_PREFIX;
import static com.reimbes.constant.UrlConstants.LOGIN_URL;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class HttpRequestTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Test
//    public void greetingShouldReturnDefaultMessage() throws Exception {
//        assertTrue((this.restTemplate.getForObject("http://localhost:" + port + "/",
//                String.class)).contains("Hello, World"));
//    }
//}

@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.reimbes"})
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReimsUserRepository userRepository;

    @Test
    public void doLogin() throws Exception {
        ReimsUser userLoginRequest = ReimsUser.ReimsUserBuilder()
                .username("chrisevan").password("chrisevan123").build();

        ObjectMapper mapper = new ObjectMapper();

        ReimsUser realUser = userRepository.findByUsername(userLoginRequest.getUsername());
        UserResponse expectedResult = new UserResponse();
        expectedResult.setUsername(realUser.getUsername());
        expectedResult.setRole(realUser.getRole());
        expectedResult.setId(realUser.getId());

        this.mockMvc.perform(post(API_PREFIX + LOGIN_URL)
                .content(mapper.writeValueAsString(userLoginRequest)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(header().string(HEADER_STRING, containsString(TOKEN_PREFIX)))
                .andExpect(content().string(mapper.writeValueAsString(expectedResult)));
    }

//    @Test
//    public void doTest() throws Exception {
//
//
//        this.mockMvc.perform(get("/api/test").contentType(APPLICATION_JSON_UTF8)
//                .content(""))
//                .andDo(print()).andExpect(status().isOk())
//                .andExpect(content().string(containsString("Hello, World")));
//    }
}

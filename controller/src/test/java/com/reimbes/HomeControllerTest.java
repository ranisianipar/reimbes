package com.reimbes;

import com.reimbes.implementation.AuthServiceImpl;
import com.reimbes.response.BaseResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.reimbes.constant.SecurityConstants.HEADER_STRING;
import static com.reimbes.constant.SecurityConstants.TOKEN_PREFIX;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @MockBean
    private AuthServiceImpl authService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void requestWithoutTokenWillHaveUnauthorizedStatus() throws Exception{
        mockMvc.perform(get("/isLogin")).andExpect(status().isUnauthorized());
    }

    @Test
    public void requestWithTokenWillHaveOkStatus() throws Exception{

        String dummyToken = TOKEN_PREFIX + "HAHAHAH";
        when(authService.isLogin(dummyToken)).thenReturn(true);
        mockMvc.perform(get("/isLogin").header(HEADER_STRING, dummyToken))
                .andExpect(status().isOk());
    }
}

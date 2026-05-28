package com.example.ecommerce.controller

import com.example.ecommerce.model.LoginResponse
import com.example.ecommerce.model.LoginRequest
import com.example.ecommerce.service.AuthService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.mockito.kotlin.whenever

@WebMvcTest(AuthController::class)
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var authService: AuthService

    @Test
    fun `login returns token payload`() {
        whenever(authService.login(LoginRequest("alice", "password123"))).thenReturn(LoginResponse("token-123"))

        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "username": "alice",
                      "password": "password123"
                    }
                    """.trimIndent()
                )
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("请求成功"))
            .andExpect(jsonPath("$.data.token").value("token-123"))
    }

    @Test
    fun `register returns success response`() {
        mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "username": "new-user",
                      "email": "new-user@example.com",
                      "password": "secret123"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("注册成功"))
    }
}
package com.example.ecommerce.controller

import com.example.ecommerce.common.PageResponse
import com.example.ecommerce.model.User
import com.example.ecommerce.service.AuthService
import com.example.ecommerce.service.InMemoryStore
import com.example.ecommerce.service.MerchantNotificationService
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@WebMvcTest(ResourceController::class)
class ResourceControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var store: InMemoryStore

    @MockBean
    private lateinit var authService: AuthService

    @MockBean
    private lateinit var merchantNotificationService: MerchantNotificationService

    @Test
    fun `me returns current user payload`() {
        val user = User(
            id = "1001",
            username = "alice",
            email = "alice@example.com",
            createdAt = "2026-05-27 10:00:00",
            updatedAt = "2026-05-27 10:00:00"
        )
        whenever(authService.resolveUserId(any())).thenReturn("1001")
        whenever(authService.currentUser(any())).thenReturn(user)

        mockMvc.perform(
            get("/users/me")
                .header("Authorization", "Bearer token-123")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.id").value("1001"))
            .andExpect(jsonPath("$.data.username").value("alice"))
    }

    @Test
    fun `list users returns page response`() {
        val user = User(
            id = "1001",
            username = "alice",
            email = "alice@example.com",
            createdAt = "2026-05-27 10:00:00",
            updatedAt = "2026-05-27 10:00:00"
        )
        whenever(authService.resolveUserId(any())).thenReturn("1001")
        whenever(
            store.listUsers(1, 10, null, null, null)
        ).thenReturn(
            PageResponse(
                total = 1,
                page = 1,
                size = 10,
                list = listOf(user)
            )
        )

        mockMvc.perform(
            get("/users")
                .param("page", "1")
                .param("size", "10")
                .header("Authorization", "Bearer token-123")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(1))
            .andExpect(jsonPath("$.data.list[0].username").value("alice"))
    }
}
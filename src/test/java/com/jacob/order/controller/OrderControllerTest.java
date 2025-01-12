package com.jacob.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacob.order.entity.OrderCreationVO;
import com.jacob.order.entity.OrderPlaceVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderCreationVO validOrderCreation;
    private OrderCreationVO invalidOrderCreation;

    @BeforeEach
    void setUp() {
        // A valid order has proper coordinate strings
        validOrderCreation = new OrderCreationVO();
        validOrderCreation.setOrigin(List.of("40.748817", "-73.985428"));      // Example: NYC coordinates
        validOrderCreation.setDestination(List.of("34.052235", "-118.243683")); // Example: LA coordinates

        // An invalid order with bad coordinate format
        invalidOrderCreation = new OrderCreationVO();
        invalidOrderCreation.setOrigin(List.of("invalidLat", "invalidLong"));
        invalidOrderCreation.setDestination(List.of("34.052235", "-118.243683"));
    }

    @Test
    void testPlaceOrderSuccess() throws Exception {
        // Convert the valid order object to JSON payload
        String json = objectMapper.writeValueAsString(validOrderCreation);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.distance").exists())
                .andExpect(jsonPath("$.status").value("UNASSIGNED"));
    }

    @Test
    void testPlaceOrderInvalidCoordinates() throws Exception {
        // Convert the invalid order object to JSON payload
        String json = objectMapper.writeValueAsString(invalidOrderCreation);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Coordinates")));
    }

    @Test
    void testTakeOrderSuccess() throws Exception {
        // First, place a valid order
        String createJson = objectMapper.writeValueAsString(validOrderCreation);
        String orderResponse = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the order ID from response (basic JSON parse)
        long orderId = objectMapper.readTree(orderResponse).get("id").asLong();

        // Now take the order
        OrderPlaceVO placeVO = new OrderPlaceVO();
        placeVO.setStatus("TAKEN");
        String takeJson = objectMapper.writeValueAsString(placeVO);

        mockMvc.perform(patch("/orders/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(takeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testTakeOrderAlreadyTakenOrNotExist() throws Exception {
        // Try to take a non-existent order
        OrderPlaceVO placeVO = new OrderPlaceVO();
        placeVO.setStatus("TAKEN");
        String takeJson = objectMapper.writeValueAsString(placeVO);

        mockMvc.perform(patch("/orders/99999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(takeJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("This order has been taken or does not exist."));

        // Place a valid order
        String createJson = objectMapper.writeValueAsString(validOrderCreation);
        String orderResponse = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Take the order
        long orderId = objectMapper.readTree(orderResponse).get("id").asLong();
        mockMvc.perform(patch("/orders/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(takeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        // Try to take the same order again
        mockMvc.perform(patch("/orders/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(takeJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("This order has been taken or does not exist."));
    }

    @Test
    void testListOrders() throws Exception {
        // Place one valid order
        String json = objectMapper.writeValueAsString(validOrderCreation);
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        // List orders with page=1, limit=10
        mockMvc.perform(get("/orders")
                        .param("page", "1")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
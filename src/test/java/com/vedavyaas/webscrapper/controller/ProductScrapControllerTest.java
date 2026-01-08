package com.vedavyaas.webscrapper.controller;

import com.vedavyaas.webscrapper.service.ProductScrapService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductScrapController.class)
public class ProductScrapControllerTest {

    private final String url = "http://localhost:8080/productScrap/";
    private final BigDecimal targetPrice = new BigDecimal("100");
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ProductScrapService productScrapService;

    @Test
    @WithMockUser(username = "test@gmail.com")
    public void getProductScrapTest() throws Exception {
        when(productScrapService.putUrl("test@gmail.com", url, targetPrice)).thenReturn("Success wait for email");

        mockMvc.perform(post("/post/url").with(csrf()).param("url", url).param("targetPrice", String.valueOf(targetPrice))).andExpect(status().isOk()).andExpect(content().string("Success wait for email"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    public void testGetUrl() throws Exception {
        when(productScrapService.getUrl("test@gmail.com")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/get/url")).andExpect(status().isOk()).andExpect(content().string("[]"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    public void testChangeUrl() throws Exception {
        when(productScrapService.changeUrl("test@gmail.com", url, 1L)).thenReturn("Success wait for email");

        mockMvc.perform(put("/change/url").with(csrf()).param("url", url).param("id", "1")).andExpect(status().isOk()).andExpect(content().string("Success wait for email"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    public void testChangePrice() throws Exception {
        when(productScrapService.changeTargetPrice("test@gmail.com", targetPrice, 1L)).thenReturn("Success wait for email");

        mockMvc.perform(put("/change/price").with(csrf()).param("newPrice", String.valueOf(targetPrice)).param("id", String.valueOf(1L))).andExpect(status().isOk()).andExpect(content().string("Success wait for email"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    public void testDeleteProduct() throws Exception {
        when(productScrapService.deleteEntity("test@gmail.com", 1L)).thenReturn("Success wait for email");

        mockMvc.perform(delete("/delete/scrap").with(csrf()).param("id", String.valueOf(1L))).andExpect(status().isOk()).andExpect(content().string("Success wait for email"));
    }
}

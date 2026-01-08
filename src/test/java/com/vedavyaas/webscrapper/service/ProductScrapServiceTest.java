package com.vedavyaas.webscrapper.service;

import com.vedavyaas.webscrapper.repository.ProductScrapEntity;
import com.vedavyaas.webscrapper.repository.ProductScrapRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductScrapServiceTest {
    private final String url = "http://localhost:8080";
    private final String user = "test@gmail.com";
    private final BigDecimal price = new BigDecimal(100);
    @Mock
    private ProductScrapRepository productScrapRepository;
    @InjectMocks
    private ProductScrapService productScrapService;

    @Test
    public void testPutUrl1() {
        when(productScrapRepository.existsByEmailAndUrl(user, url)).thenReturn(true);
        assertEquals("Already exist.", productScrapService.putUrl(user, url, price));
    }

    @Test
    public void testPutUrl2() {
        when(productScrapRepository.existsByEmailAndUrl(user, url)).thenReturn(false);
        assertEquals("Successfully saved wait for the email", productScrapService.putUrl(user, url, price));
    }

    @Test
    public void testChangeTargetPrice1() {
        when(productScrapRepository.findById(1L)).thenReturn(Optional.empty());
        assertEquals("Invalid id.", productScrapService.changeTargetPrice(user, price, 1L));
    }

    @Test
    public void testChangeTargetPrice2() {
        when(productScrapRepository.findById(1L)).thenReturn(Optional.of(new ProductScrapEntity(user, url, price)));
        assertEquals("Product doesnt exist.", productScrapService.changeTargetPrice("user", price, 1L));
    }

    @Test
    public void testChangeTargetPrice3() {
        when(productScrapRepository.findById(1L)).thenReturn(Optional.of(new ProductScrapEntity(user, url, price)));
        assertEquals("Successfully saved wait for the email", productScrapService.changeTargetPrice(user, price, 1L));
    }

    @Test
    public void testChangeUrl1() {
        when(productScrapRepository.findById(1L)).thenReturn(Optional.empty());
        assertEquals("Invalid id.", productScrapService.changeUrl(user, url, 1L));
    }

    @Test
    public void testChangeUrl2() {
        when(productScrapRepository.findById(1L)).thenReturn(Optional.of(new ProductScrapEntity(user, url, price)));
        assertEquals("Product doesnt exist.", productScrapService.changeUrl("user", url, 1L));
    }

    @Test
    public void testChangeUrl3() {
        when(productScrapRepository.findById(1L)).thenReturn(Optional.of(new ProductScrapEntity(user, url, price)));
        assertEquals("Successfully updated.", productScrapService.changeUrl(user, url, 1L));
    }

    @Test
    public void testDeleteProduct1() {
        when(productScrapRepository.findById(1L)).thenReturn(Optional.empty());
        assertEquals("Invalid id.", productScrapService.deleteEntity(user, 1L));
    }

    @Test
    public void testDeleteProduct2() {
        when(productScrapRepository.findById(1L)).thenReturn(Optional.of(new ProductScrapEntity(user, url, price)));
        assertEquals("Product doesnt exist.", productScrapService.deleteEntity("user", 1L));
    }

    @Test
    public void testDeleteProduct3() {
        when(productScrapRepository.findById(1L)).thenReturn(Optional.of(new ProductScrapEntity(user, url, price)));
        assertEquals("Successfully deleted.",  productScrapService.deleteEntity(user, 1L));
    }
}

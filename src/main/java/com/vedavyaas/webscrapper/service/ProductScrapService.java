package com.vedavyaas.webscrapper.service;

import com.vedavyaas.webscrapper.repository.ProductScrapEntity;
import com.vedavyaas.webscrapper.repository.ProductScrapRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductScrapService {

    private final ProductScrapRepository productScrapRepository;

    public ProductScrapService(ProductScrapRepository productScrapRepository) {
        this.productScrapRepository = productScrapRepository;
    }

    public String putUrl(String user, String url, BigDecimal targetPrice) {
        if(!productScrapRepository.existsByEmailAndUrl(user, url)) {
            ProductScrapEntity productScrapEntity = new ProductScrapEntity(user, url, targetPrice);
            productScrapRepository.save(productScrapEntity);
            return "Successfully saved wait for the email";
        }
        return "Already exist.";
    }

    public List<ProductScrapEntity> getUrl(String user) {
        return productScrapRepository.findAllByEmail(user);
    }

    @Transactional
    public String changeUrl(String user, String url, Long id) {
        Optional<ProductScrapEntity> productScrapEntity = productScrapRepository.findById(id);
        if(productScrapEntity.isPresent()) {
            if(productScrapEntity.get().getEmail().equals(user)) {
                productScrapRepository.updateUrlById(url, id);
                return "Successfully updated.";
            }
            return "Product doesnt exist.";
        }
        return "Invalid id.";
    }

    @Transactional
    public String changeTargetPrice(String user, BigDecimal targetPrice, Long id) {
        Optional<ProductScrapEntity> productScrapEntity = productScrapRepository.findById(id);
        if(productScrapEntity.isPresent()) {
            if(productScrapEntity.get().getEmail().equals(user)) {
                productScrapRepository.updateTargetPriceById(targetPrice, id);
                return "Successfully saved wait for the email";
            }
            return "Product doesnt exist.";
        }
        return "Invalid id.";
    }

    public String deleteEntity(String user, Long id) {
        Optional<ProductScrapEntity> productScrapEntity = productScrapRepository.findById(id);
        if(productScrapEntity.isPresent()) {
            if(productScrapEntity.get().getEmail().equals(user)) {
                productScrapRepository.deleteById(id);
                return "Successfully deleted.";
            }
            return "Product doesnt exist.";
        }
        return "Invalid id.";
    }
}

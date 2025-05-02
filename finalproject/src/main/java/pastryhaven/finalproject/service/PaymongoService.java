package pastryhaven.finalproject.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymongoService {

    private final String apiKey;
    private final RestTemplate restTemplate;

    public PaymongoService(@Value("${paymongo.secretKey}") String apiSecretKey) {
        this.apiKey = Base64.getEncoder().encodeToString((apiSecretKey + ":").getBytes());
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> createPaymentLink(BigDecimal amount, String description, String remarks) {
        // Set up headers with authentication
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + this.apiKey);

        // Create request body
        Map<String, Object> requestMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        Map<String, Object> attributesMap = new HashMap<>();

        // Set attributes
        BigDecimal cents = amount.multiply(BigDecimal.valueOf(100));
        attributesMap.put("amount", cents);
        attributesMap.put("description", description);
        attributesMap.put("remarks", remarks);
        attributesMap.put("currency", "PHP");

        dataMap.put("attributes", attributesMap);
        requestMap.put("data", dataMap);

        // Create the request entity with headers and body
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestMap, headers);

        // Make the API call
        Map<String, Object> response = restTemplate.postForObject(
                "https://api.paymongo.com/v1/links",
                request,
                Map.class
        );

        return response;
    }


}

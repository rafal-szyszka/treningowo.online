package com.prodactivv.app.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Component
@PropertySource(value = "classpath:payments.properties")
public class P24Defaults {

    @Value("${url.registerTransaction}")
    private String registerTransactionUrl;
    @Value("${url.paymentMethods}")
    private String paymentMethodsUrl;
    @Value("${url.trnRequest}")
    private String trnRequestUrl;

    @Value("${api.baseUrl}")
    private String baseUrl;

    @Value("${api.authorization}")
    private String authorization;

    @Value("${api.key}")
    private String apiKey;

    @Value("${crc}")
    private String crc;

    @Value("${merchantId}")
    private int merchantId;


    @Value("${country}")
    private String country;

    @Value("${language}")
    private String language;

    @Value("${urlStatus}")
    private String urlStatus;

    @Value("${urlReturn}")
    private String urlReturn;

    @Value("${timeLimit}")
    private int timeLimit;

    @Value("${channel}")
    private int channel;

    @Value("${waitForResult}")
    private boolean waitForResult;

    @Value("${regulationsAccepted}")
    private boolean regulationAccept;

    @Value("${shipping}")
    private int shipping;

    @Value("${encoding}")
    private String encoding;
}
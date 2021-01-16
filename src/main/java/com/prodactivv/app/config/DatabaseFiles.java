package com.prodactivv.app.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Component
@PropertySource(value = "classpath:files.properties")
public class DatabaseFiles {

    @Value("${files.storage.local.path}")
    private String localStoragePath;

    @Value("${files.storage.public.address}")
    private String filePublicAddress;
}

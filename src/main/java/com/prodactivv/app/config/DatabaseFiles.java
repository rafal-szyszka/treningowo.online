package com.prodactivv.app.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Getter
@Component
@PropertySources({
        @PropertySource(value = "classpath:files.properties"),
})
public class DatabaseFiles {

    @Value("${files.storage.local.path}")
    private String localStoragePath;

    @Value("${files.storage.safe.path}")
    private String localSafeStoragePath;

    @Value("${files.storage.public.address}")
    private String filePublicAddress;
}

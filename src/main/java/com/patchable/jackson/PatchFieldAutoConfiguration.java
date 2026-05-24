package com.patchable.jackson;

import com.fasterxml.jackson.databind.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PatchFieldAutoConfiguration {

    @Bean
    public Module patchFieldModule() {
        return new PatchFieldModule();
    }
}

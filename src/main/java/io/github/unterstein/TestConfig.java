package io.github.unterstein;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TestConfig {

    @Bean
    public TradingClient tradingClient(){
        return new TradingClient();
    }
}

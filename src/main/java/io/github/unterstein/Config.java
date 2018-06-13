package io.github.unterstein;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Value("${BASE_CURRENCY}")
    private String baseCurrency;

    @Value("${TRADE_CURRENCY}")
    private String tradeCurrency;

    @Value("${API_KEY:33dlv4RMYPnGDVifHfouwmRPr06AxboXbaMVGFOJClFiOaEEEQyCQ1fHEz2MQJRv}")
    private String apiKey;

    @Value("${API_SECRET:fkIlfKwNV3YX0l3PUBoCyjffnhoa86M5vQ5ZSjhMUou8RI2oQeD1zz9YEfdGpB0y}")
    private String apiSecret;

    @Bean
    public TradingClient tradingClient(){
        return new TradingClient(baseCurrency, tradeCurrency, apiKey, apiSecret);
    }
}

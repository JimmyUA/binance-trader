package io.github.unterstein;

import io.github.unterstein.remoteManagment.RemoteManager;
import io.github.unterstein.statistic.MA.MovingAverage;
import io.github.unterstein.statistic.PricesAccumulator;
import io.github.unterstein.statistic.TrendAnalyzer;
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

    @Bean
    public TrendAnalyzer trendAnalizer(){return new TrendAnalyzer();}

    @Bean(name="testAccumulator")
    public PricesAccumulator pricesAccumulator(){return new PricesAccumulator();
    }

    @Bean
    public MovingAverage movingAverage(){
        return new MovingAverage();
    }

    @Bean
    public RemoteManager remoteManager(){
        return new RemoteManager();
    }
}

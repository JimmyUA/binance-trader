package io.github.unterstein;

import com.giffing.wicket.spring.boot.starter.app.WicketBootWebApplication;
import io.github.unterstein.remoteManagment.RemoteManager;
import io.github.unterstein.statistic.MA.MovingAverage;
import io.github.unterstein.statistic.PricesAccumulator;
import io.github.unterstein.statistic.TrendAnalyzer;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TestConfig {


    @Autowired
    private WicketBootWebApplication wicketBootWebApplication;

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

    @Bean
    public WicketTester tester(){
        return new WicketTester((WebApplication) wicketBootWebApplication);
    }
}

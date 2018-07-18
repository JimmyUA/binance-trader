package io.github.unterstein;

import io.github.unterstein.persistent.repository.TradeRepository;
import io.github.unterstein.remoteManagment.RemoteManager;
import io.github.unterstein.statistic.MA.MovingAverage;
import io.github.unterstein.statistic.PricesAccumulator;
import io.github.unterstein.statistic.TrendAnalyzer;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@Profile("test")
@Configuration
@EnableScheduling
public class TestConfig {


    @Autowired
    public WicketWebApplication wicketBootWebApplication;

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

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

}

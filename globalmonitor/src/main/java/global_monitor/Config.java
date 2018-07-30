package global_monitor;


import io.github.unterstein.TradingClient;
import io.github.unterstein.statistic.MA.MovingAverage;
import io.github.unterstein.statistic.TrendAnalyzer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${API_KEY:8L4iCDQJXAvxE9kbFaAVh01dlw2mBOUtPeyKseEXzdSeRvVG8YwF887w77qtoWV1}")
    private String apiKey;

    @Value("${API_SECRET:uunJlIIW21rEcu16DWIowNc5xQqTKhxS5VquBgcT08cvMNzzDvkFw3aOAicDjCVW}")
    private String apiSecret;
    private String baseCurrency = "ETH";
    private String tradeCurrency = "";

    @Bean
    public TradingClient tradingClient() {
        return new TradingClient(baseCurrency, tradeCurrency, apiKey, apiSecret);
    }

    @Bean
    public TrendAnalyzer trendAnalyzer() {
        return new TrendAnalyzer();
    }

    @Bean
    public MovingAverage movingAverage() {
        return new MovingAverage();
    }
}

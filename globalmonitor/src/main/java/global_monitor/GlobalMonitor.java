package global_monitor;

import global_monitor.coins.Coin;
import global_monitor.coins.CoinsContainer;
import global_monitor.starter.TraderStarter;
import io.github.unterstein.TradingClient;
import io.github.unterstein.statistic.TrendAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GlobalMonitor {

    @Value("${BASE_CURRENCY}")
    private String baseCurrency;

    @Autowired
    private CoinsContainer container;

    @Autowired
    private TradingClient client;

    @Autowired
    private TrendAnalyzer trendAnalyzer;

    @Autowired
    private TraderStarter starter;

    public void reviewCoins() {
        List<Coin> startedCoins = container.getStartedCoins();
        reviewStartedCoins(startedCoins);
    }

    private void reviewStartedCoins(List<Coin> startedCoins) {
        startedCoins.forEach(this::checkDayTrendAndStartIfNeeded);
    }

    private void checkDayTrendAndStartIfNeeded(Coin coin) {
        client.setSymbol(coin.getSymbol() + baseCurrency);
        if(trendAnalyzer.isUpDayTrend()){
            starter.startTrader(coin.getSymbol());
            coin.start();
        }
    }
}

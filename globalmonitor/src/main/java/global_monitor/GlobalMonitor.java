package global_monitor;

import global_monitor.coins.Coin;
import global_monitor.coins.CoinsContainer;
import global_monitor.starter.TraderStarter;
import global_monitor.starter.TraderStopper;
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

    @Autowired
    private TraderStopper stopper;

    public void reviewCoins() {
        List<Coin> startedCoins = container.getStartedCoins();
        reviewStartedCoins(startedCoins);
        List<Coin> stoppedCoins = container.getStoppedCoins();
        reviewStoppedCoins(stoppedCoins);

    }

    private void reviewStartedCoins(List<Coin> coins) {
        coins.forEach(this::checkDayTrendAndStopIfNeeded);
    }

    private void checkDayTrendAndStopIfNeeded(Coin coin) {
        client.setSymbol(coin.getSymbol() + baseCurrency);
        if(!trendAnalyzer.isUpDayTrend()){
            stopper.stopTrader(coin);
            coin.stop();
        }
    }

    private void reviewStoppedCoins(List<Coin> coins) {
        coins.forEach(this::checkDayTrendAndStartIfNeeded);
    }

    private void checkDayTrendAndStartIfNeeded(Coin coin) {
        client.setSymbol(coin.getSymbol() + baseCurrency);
        if(trendAnalyzer.isUpDayTrend()){
            starter.startTrader(coin);
            coin.start();
        }
    }
}

package global_monitor.starter;

import io.github.unterstein.BinanceBotApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

@Component
public class TraderStarter {
    public void startTrader(String symbol) {
        BinanceBotApplication.setProfile(symbol);
        new SpringApplication(BinanceBotApplication.class).run(new String[]{});
    }
}

package global_monitor.starter;

import io.github.unterstein.BinanceBotApplication;
import org.springframework.stereotype.Component;

@Component
public class TraderStarter {
    public static void startTrader(String symbol) {
        BinanceBotApplication.main(new String[]{});
    }
}

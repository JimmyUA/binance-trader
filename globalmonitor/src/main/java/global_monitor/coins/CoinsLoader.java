package global_monitor.coins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CoinsLoader {

    @Autowired
    private CoinsContainer coinsContainer;

    public void loadCoins(Map<String, String> coins) {
        coins.forEach((symbol, port) -> coinsContainer.add(new Coin(symbol, port)));
    }


}

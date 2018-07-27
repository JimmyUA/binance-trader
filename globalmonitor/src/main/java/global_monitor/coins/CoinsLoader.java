package global_monitor.coins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CoinsLoader {

    @Autowired
    private CoinsContainer coinsContainer;

    public void loadCoins(List<String> coins) {
        coins.forEach(coin -> coinsContainer.add(new Coin(coin)));
    }


}

package global_monitor;

import global_monitor.coins.CoinsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@EnableScheduling
@SpringBootApplication
@RestController("/")
public class GlobalMonitorApplication {

    @Value("#{${COINS}}")
    private Map<String, String> coins;

    @Autowired
    CoinsLoader loader;

    @Autowired
    private GlobalMonitor globalMonitor;

    public static void main(String[] args) {
        SpringApplication.run(GlobalMonitorApplication.class);
    }



    @PostConstruct
    public void init() {
        loader.loadCoins(coins);
    }

    @Scheduled(fixedRate = 1000 * 60)
    public void schedule() {
        globalMonitor.reviewCoins();
    }
}

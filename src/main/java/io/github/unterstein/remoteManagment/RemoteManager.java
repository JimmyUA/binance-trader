package io.github.unterstein.remoteManagment;

import io.github.unterstein.TradingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.unterstein.remoteManagment.ManagementConstants.*;

@Component
public class RemoteManager {

    @Autowired
    private TradingClient tradingClient;


    public void stopBot(){
        shutDown = true;
    }
}

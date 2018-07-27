package io.github.unterstein;


import com.binance.api.client.domain.account.AssetBalance;
import io.github.unterstein.botlogic.executor.TradeExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import util.excel.ExcelSaver;

import java.util.List;

import static io.github.unterstein.remoteManagment.ManagementConstants.*;
import static util.Slepper.sleepSeconds;

@Component
public class BinanceTrader {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);



    private TradingClient client;

    private double tradeProfit;
    private int tradeAmount;

    private double lastBid;

    private double antiBurstPercentage;

    @Autowired
    private TradeExecutor tradeExecutor;

    @Autowired
    private ExcelSaver excelSaver;

    private double spreadDifference;

    @Autowired
    BinanceTrader(TradingClient client) {
        this.client = client;

    }


    public void setTradeProfit(double tradeProfit) {
        this.tradeProfit = tradeProfit;
    }

    public void setTradeAmount(int tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public int getTradeAmount() {
        return tradeAmount;
    }

    public void setSpreadDifference(double spredDifference) {
        this.spreadDifference = spredDifference;
    }

    void tick() {


        double lastPrice = 0;

        try {
            checkWaitSomeTime();
            lastPrice = client.lastPrice();
            updateLastBid();
            double lastAsk = getLastAsk();
            double profitablePrice = lastBid + (lastBid * tradeProfit / 100);
            double antiBurstValue = lastAsk - profitablePrice;
            antiBurstPercentage = antiBurstValue / lastAsk * 100.0;


            double burstDetectionDifference = lastAsk - profitablePrice;
            logger.info(String.format("bid:%.8f ask:%.8f price:%.8f profitablePrice:%.8f diff:%.8f\n  ",
                    lastBid, lastAsk, lastPrice, profitablePrice, burstDetectionDifference));
            checkShutDown();

            if (isFall()) {
                logger.info("Fall burst detected");

                tradeExecutor.buyProcess();
                tradeExecutor.sellProcess();

            }
            else {

                logger.info(String.format("No profit detected, difference %.8f %.3f percent\n", antiBurstValue, antiBurstPercentage));
            }

        } catch (Exception e) {
            logger.error("Unable to perform ticker", e);
        }
    }

    private void checkIfBoughtManualy() {
        AssetBalance tradingBalance = client.getTradingBalance();
        String freeTradingBalance = tradingBalance.getFree();
        double freeTradingBalanceValue = Double.parseDouble(freeTradingBalance);
        if (freeTradingBalanceValue > tradeAmount){
            logger.info("Looks like something was bought manually, lets wait");
            sleepSeconds(3600);
        }
    }

    private void checkWaitSomeTime() {
        if (sleepSomeTime){
            sleepSeconds(180);
        }
        sleepSomeTime = false;
    }




    private void updateLastBid() {
        lastBid = getLastBid();
    }

    private double getLastAsk() {
        return client.getLastAsksAverage(tradeAmount, 3);
    }

    private double getLastBid() {
        return client.getLastBidsAverage(tradeAmount, 3);
    }


    private boolean isFall() {
        return antiBurstPercentage < spreadDifference;
    }


    private void checkShutDown() {
        if (shutDown) {
            excelSaver.saveTrades(client.getTradeCurrency());
            excelSaver.saveAmplitudes(client.getTradeCurrency());
            logger.info("\n\nShutting down!\n\n");
            System.exit(0);
        }
    }

    List<AssetBalance> getBalances() {
        return client.getBalances();
    }

}

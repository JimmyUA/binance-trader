package io.github.unterstein.statistic.infoAccumulator;

import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import io.github.unterstein.BinanceTrader;
import io.github.unterstein.TradingClient;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static io.github.unterstein.remoteManagment.ManagementConstants.shutDown;
import static java.lang.Thread.sleep;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;


public class LastPriceVSOrderBook {

    private static Logger logger = LoggerFactory.getLogger(BinanceTrader.class);
    private TradingClient tradingClient;

    private List<Double> prices = new ArrayList<>(3600);
    private List<Double> totalBidded = new ArrayList<>(3600);
    private List<Double> totalAsked = new ArrayList<>(3600);
    private List<Double> averageBidsPrice = new ArrayList<>(3600);
    private List<Double> averageAsksPrice = new ArrayList<>(3600);
    private List<Double> minBidsPrice = new ArrayList<>(3600);
    private List<Double> maxBidsPrice = new ArrayList<>(3600);
    private List<Double> maxAsksPrice = new ArrayList<>(3600);
    private List<Double> minAsksPrice = new ArrayList<>(3600);
    private List<Integer> averageBidsQty = new ArrayList<>(3600);
    private List<Integer> averageAsksQty = new ArrayList<>(3600);
    private List<Integer> maxBidsQty = new ArrayList<>(3600);
    private List<Integer> minBidsQty = new ArrayList<>(3600);
    private List<Integer> maxAsksQty = new ArrayList<>(3600);
    private List<Integer> minAsksQty = new ArrayList<>(3600);
    private String filePath = "/home/jimmy/cryptoBot/statistics.xlsx";

    @Autowired
    public LastPriceVSOrderBook(TradingClient tradingClient) {
        this.tradingClient = tradingClient;
    }

    public void capturePriceAndOrderBookEntries() {
        prices.add(tradingClient.lastPrice());

        OrderBook orderBook = tradingClient.getOrderBook();
        List<OrderBookEntry> currentAsks = orderBook.getAsks();
        List<OrderBookEntry> currentBids = orderBook.getBids();

        handleBids(currentBids);

        handleAsks(currentAsks);

        if(shutDown){
            saveToFile();
            System.exit(0);
        }
    }

    private void handleAsks(List<OrderBookEntry> currentAsks) {
        OrderBookEntry minAsk = currentAsks.get(0);
        minAsksPrice.add(convertToDouble(minAsk.getPrice()));
        minAsksQty.add(convertToInt(minAsk.getQty()));

        OrderBookEntry maxAsk = currentAsks.get(9);
        maxAsksPrice.add(convertToDouble(maxAsk.getPrice()));
        maxAsksQty.add(convertToInt(maxAsk.getQty()));

        Double averagePrice = currentAsks.stream().mapToDouble(ask -> convertToDouble(ask.getPrice())).average().getAsDouble();
        Double averageQtyDouble = currentAsks.stream().mapToInt(ask -> convertToInt(ask.getQty())).average().getAsDouble();

        int averageQty = averageQtyDouble.intValue();
        averageAsksPrice.add(averagePrice);
        averageAsksQty.add(averageQty);

        Double sumPrices = currentAsks.stream().mapToDouble(ask -> convertToDouble(ask.getPrice())).sum();
        int sumQty = currentAsks.stream().mapToInt(ask -> convertToInt(ask.getQty())).sum();

        totalAsked.add(sumPrices*sumQty);
    }

    private void handleBids(List<OrderBookEntry> currentBids) {
        OrderBookEntry minBid = currentBids.get(9);
        minBidsPrice.add(convertToDouble(minBid.getPrice()));
        minBidsQty.add(convertToInt(minBid.getQty()));

        OrderBookEntry maxBid = currentBids.get(0);
        maxBidsPrice.add(convertToDouble(maxBid.getPrice()));
        maxBidsQty.add(convertToInt(maxBid.getQty()));

        Double averagePrice = currentBids.stream().mapToDouble(bid -> convertToDouble(bid.getPrice())).average().getAsDouble();
        Double averageQtyDouble = currentBids.stream().mapToInt(bid -> convertToInt(bid.getQty())).average().getAsDouble();

        int averageQty = averageQtyDouble.intValue();
        averageBidsPrice.add(averagePrice);
        averageBidsQty.add(averageQty);

        Double sumPrices = currentBids.stream().mapToDouble(bid -> convertToDouble(bid.getPrice())).sum();
        int sumQty = currentBids.stream().mapToInt(bid -> convertToInt(bid.getQty())).sum();

        totalBidded.add(sumPrices*sumQty);
    }

    private Double convertToDouble(String price) {
        return Double.parseDouble(price);
    }

    private Integer convertToInt(String qty) {
        Double doubleValue = Double.parseDouble(qty);
        return doubleValue.intValue();
    }

    public void saveToFile() {
            File file = new File(filePath);
            try(FileInputStream sourceSteam = new FileInputStream(file)){
                Workbook workbook = new XSSFWorkbook(sourceSteam);
                Sheet sheet = workbook.getSheetAt(0);
                Row currentRow;
                for (int i = 0; i < prices.size(); i++) {
                    currentRow = sheet.createRow(i);
                    currentRow.createCell(0).setCellValue(prices.get(i));
                    currentRow.createCell(1).setCellValue(minBidsPrice.get(i));
                    currentRow.createCell(2).setCellValue(minBidsQty.get(i));
                    currentRow.createCell(3).setCellValue(maxBidsPrice.get(i));
                    currentRow.createCell(4).setCellValue(maxBidsQty.get(i));
                    currentRow.createCell(5).setCellValue(averageBidsPrice.get(i));
                    currentRow.createCell(6).setCellValue(averageBidsQty.get(i));
                    currentRow.createCell(7).setCellValue(totalBidded.get(i));
                    currentRow.createCell(8).setCellValue(minAsksPrice.get(i));
                    currentRow.createCell(9).setCellValue(minAsksQty.get(i));
                    currentRow.createCell(10).setCellValue(maxAsksPrice.get(i));
                    currentRow.createCell(11).setCellValue(maxAsksQty.get(i));
                    currentRow.createCell(12).setCellValue(averageAsksPrice.get(i));
                    currentRow.createCell(13).setCellValue(averageAsksQty.get(i));
                    currentRow.createCell(14).setCellValue(totalAsked.get(i));
                }

                saveWorkBook(workbook,file);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    private static void saveWorkBook(Workbook workbook, File file) {
        try(FileOutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
            workbook.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "LastPriceVSOrderBook{" +
                "tradingClient=" + tradingClient +
                ", prices=" + prices +
                ", totalBidded=" + totalBidded +
                ", totalAsked=" + totalAsked +
                ", averageBidsPrice=" + averageBidsPrice +
                ", averageAsksPrice=" + averageAsksPrice +
                ", minBidsPrice=" + minBidsPrice +
                ", maxBidsPrice=" + maxBidsPrice +
                ", maxAsksPrice=" + maxAsksPrice +
                ", minAsksPrice=" + minAsksPrice +
                ", averageBidsQty=" + averageBidsQty +
                ", averageAsksQty=" + averageAsksQty +
                ", maxBidsQty=" + maxBidsQty +
                ", minBidsQty=" + minBidsQty +
                ", maxAsksQty=" + maxAsksQty +
                ", minAsksQty=" + minAsksQty +
                '}';
    }

}

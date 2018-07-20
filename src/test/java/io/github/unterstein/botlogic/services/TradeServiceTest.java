package io.github.unterstein.botlogic.services;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import io.github.unterstein.BinanceBotApplication;
import io.github.unterstein.persistent.entity.Trade;
import io.github.unterstein.persistent.repository.TradeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BinanceBotApplication.class})
@TestPropertySource(locations = "classpath:application_test.properties")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection={"datasource"})
@DatabaseSetup("/sql/tests/one_trade.xml")
public class TradeServiceTest {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private TradeService tradeService;

    @DatabaseSetup("/sql/tests/one_trade.xml")
    @Test
    public void tradesShouldHaveOneRecord() throws Exception {
        List<Trade> trades = tradeRepository.findAll();
        assertEquals(1, trades.size());
    }

    @DatabaseSetup("/sql/tests/one_trade.xml")
    @Test
    public void shouldStoreTradeAfterAddingBothBuyAndSell() throws Exception {
        executeTrade();

        List<Trade> trades = tradeRepository.findAll();
        assertEquals(2, trades.size());
    }

    @DatabaseSetup("/sql/tests/one_trade.xml")
    @Test
    public void shouldCalculateProfitCorrectly() throws Exception {
        executeTrade();

        Trade trade = getStoredTrade();

        Double profit = trade.getProfit();
        double fee = 0.3 * 0.0005;
        double expectedProfit = 0.1 - fee;
        System.out.println(trade.getBoughtDate());

        assertEquals(expectedProfit, profit, 0.0001);
    }

    @DatabaseSetup("/sql/tests/one_trade.xml")
    @Test
    public void shouldCalculateProfitPercentCorrectly() throws Exception {
        executeTrade();

        Trade trade = getStoredTrade();

        Double profitPercent = trade.getProfitPercent();
        assertEquals(99.85, profitPercent, 0.0001);
    }

    private void executeTrade() {
        tradeService.addBuyOrder(0.1);
        tradeService.addSellOrder(0.2);
    }

    private Trade getStoredTrade(){
        List<Trade> trades = tradeRepository.findAll();
        Trade trade = trades.get(1);
        return trade;
    }
}
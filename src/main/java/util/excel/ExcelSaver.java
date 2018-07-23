package util.excel;

import io.github.unterstein.TradingClient;
import io.github.unterstein.botlogic.services.AmplitudeService;
import io.github.unterstein.botlogic.services.TradeService;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import util.excel.mappers.AmplitudesMapper;
import util.excel.mappers.DataMapper;
import util.excel.mappers.TradesMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelSaver {

    private static Logger logger = LoggerFactory.getLogger(ExcelSaver.class);


    @Autowired
    private TradeService tradeService;

    @Autowired
    private AmplitudeService amplitudeService;

    private final String filesDirectoryPath = "./statistic";

    private void save(DataMapper dataMapper, List<?> data) {
        createDir();
        File file = new File(filesDirectoryPath + "/" + dataMapper.getFileName());
        createFile(file);

        Workbook workbook = new XSSFWorkbook();
        dataMapper.map(data, workbook);
        saveWorkBook(workbook, file);
    }

    private void createFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void saveWorkBook(Workbook workbook, File file) {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void createDir() {
        File file = new File(filesDirectoryPath);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public void saveTrades(String tradeCurrency) {
        DataMapper tradesMapper = new TradesMapper();
        tradesMapper.initFileName(tradeCurrency);
        save(tradesMapper, tradeService.getAllTrades());
    }

    public void saveAmplitudes(String tradeCurrency) {
        DataMapper amplitudesMapper = new AmplitudesMapper();
        amplitudesMapper.initFileName(tradeCurrency);
        save(amplitudesMapper, amplitudeService.getAmplitudes());
    }
}

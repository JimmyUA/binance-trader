package util.excel.mappers;

import io.github.unterstein.persistent.entity.Trade;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

public class TradesMapper implements DataMapper<Trade> {

    private String fileName = "trades_";
    private int counter;
    private int cellCounter;
    private Workbook workbook;

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void map(List<Trade> data, Workbook workbook) {
        this.workbook = workbook;
        Sheet sheet = workbook.createSheet();
        initHeaders(data,sheet);
        data.forEach(trade -> mapTrade(trade, sheet));
        initFooter(data, sheet);
        counter = 0;
    }

    private void initFooter(List<Trade> data, Sheet sheet) {
        Row currentRow = sheet.createRow(counter++);
        currentRow.createCell(cellCounter--).setCellValue(data.stream().mapToDouble(Trade::getProfitPercent).sum());
        currentRow.createCell(cellCounter).setCellValue(data.stream().mapToDouble(Trade::getProfit).sum());
    }

    private void mapTrade(Trade trade, Sheet sheet) {
        Row currentRow = sheet.createRow(counter++);
        cellCounter = 0;
        currentRow.createCell(cellCounter++).setCellValue(trade.getId());
        currentRow.createCell(cellCounter++).setCellValue(trade.getBoughtPrice());
        Cell boughtDateCell = currentRow.createCell(cellCounter++);
        boughtDateCell.setCellStyle(getDateSellStyle());
        boughtDateCell.setCellValue(trade.getBoughtDate());
        currentRow.createCell(cellCounter++).setCellValue(trade.getSellPrice());
        Cell sellDateCell = currentRow.createCell(cellCounter++);
        sellDateCell.setCellValue(trade.getSellDate());
        sellDateCell.setCellStyle(getDateSellStyle());
        currentRow.createCell(cellCounter++).setCellValue(trade.getSellPrice());
        currentRow.createCell(cellCounter++).setCellValue(trade.getProfit());
        currentRow.createCell(cellCounter).setCellValue(trade.getProfitPercent());
    }


    @Override
    public void initFileName(String tradeCurrency) {
        fileName += tradeCurrency + ".xls";
    }

    private void initHeaders(List<Trade> data, Sheet sheet) {
        cellCounter = 0;
        Row currentRow = sheet.createRow(counter++);
        currentRow.createCell(cellCounter++).setCellValue("Id");
        currentRow.createCell(cellCounter++).setCellValue("Bought Price");
        currentRow.createCell(cellCounter++).setCellValue("Bought Date");
        currentRow.createCell(cellCounter++).setCellValue("Sell Price");
        currentRow.createCell(cellCounter++).setCellValue("Sell Date");
        currentRow.createCell(cellCounter++).setCellValue("Profit");
        currentRow.createCell(cellCounter).setCellValue("Profit Percent");
    }

    private CellStyle getDateSellStyle(){
        CellStyle cellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        short dateFormat = createHelper.createDataFormat().getFormat("yyyy-dd-MM HH:mm:ss");
        cellStyle.setDataFormat(dateFormat);
        return cellStyle;
    }
}

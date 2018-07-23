package util.excel.mappers;

import io.github.unterstein.persistent.entity.Amplitude;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public class AmplitudesMapper implements DataMapper<Amplitude> {

    private String fileName = "amplitudes_";
    private int counter;
    private int cellCounter;
    private Workbook workbook;

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void map(List<Amplitude> data, Workbook workbook) {
        this.workbook = workbook;
        Sheet sheet = workbook.createSheet();
        initHeaders(data, sheet);
        data.forEach(amplitude -> mapAmplitude(amplitude, sheet));
        initFooter(data, sheet);
        counter = 0;
    }

    private void initHeaders(List<Amplitude> data, Sheet sheet) {
        cellCounter = 0;
        Row currentRow = sheet.createRow(counter++);
        currentRow.createCell(cellCounter++).setCellValue("Id");
        currentRow.createCell(cellCounter++).setCellValue("Max");
        currentRow.createCell(cellCounter).setCellValue("Min");
    }

    private void initFooter(List<Amplitude> data, Sheet sheet) {
        Row currentRow = sheet.createRow(counter++);
        currentRow.createCell(cellCounter).setCellValue(data.stream().mapToDouble(Amplitude::getMin).min().orElse(0.0));
        currentRow.createCell(--cellCounter).setCellValue(data.stream().mapToDouble(Amplitude::getMax).max().orElse(0.0));
        currentRow.createCell(--cellCounter).setCellValue("Tops");
        currentRow = sheet.createRow(counter++);
        currentRow.createCell(cellCounter++).setCellValue("Average");
        currentRow.createCell(cellCounter++).setCellValue(data.stream().mapToDouble(Amplitude::getMin).average().orElse(0.0));
        currentRow.createCell(cellCounter).setCellValue(data.stream().mapToDouble(Amplitude::getMax).average().orElse(0.0));
    }

    private void mapAmplitude(Amplitude amplitude, Sheet sheet) {
        Row currentRow = sheet.createRow(counter++);
        cellCounter = 0;
        currentRow.createCell(cellCounter++).setCellValue(amplitude.getId());
        currentRow.createCell(cellCounter++).setCellValue(amplitude.getMax());
        currentRow.createCell(cellCounter).setCellValue(amplitude.getMin());
    }

    @Override
    public void initFileName(String tradeCurrency) {
        fileName += tradeCurrency + ".xls";
    }
}

package util.excel.mappers;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface DataMapper<T> {

    String getFileName();

    void map(List<T> data, Workbook workbook);

    void initFileName(String tradeCurrency);
}

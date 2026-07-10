package util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvDataReader {

    /**
     * Reads CSV file and returns a list of maps where each map represents a row
     * @param csvFilePath - path to the CSV file
     * @return List of Maps with column headers as keys and cell values as values
     */
    public static List<Map<String, String>> readCsvFile(String csvFilePath) {
        List<Map<String, String>> data = new ArrayList<>();

        try (CSVParser csvParser = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .parse(Files.newBufferedReader(Paths.get(csvFilePath), StandardCharsets.UTF_8))) {

            for (CSVRecord record : csvParser) {
                Map<String, String> row = new HashMap<>();
                for (String header : csvParser.getHeaderNames()) {
                    row.put(header, record.get(header));
                }
                data.add(row);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV file: " + csvFilePath, e);
        }

        return data;
    }

    /**
     * Reads CSV file and extracts a specific column as a list
     * @param csvFilePath - path to the CSV file
     * @param columnName - name of the column to extract
     * @return List of values from the specified column
     */
    public static List<String> readCsvColumn(String csvFilePath, String columnName) {
        List<String> columnData = new ArrayList<>();

        try (CSVParser csvParser = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .parse(Files.newBufferedReader(Paths.get(csvFilePath), StandardCharsets.UTF_8))) {

            for (CSVRecord record : csvParser) {
                columnData.add(record.get(columnName));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV column: " + columnName + " from file: " + csvFilePath, e);
        }

        return columnData;
    }
}


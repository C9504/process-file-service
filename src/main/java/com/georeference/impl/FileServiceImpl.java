package com.georeference.impl;

import com.georeference.services.FileService;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@Service
public class FileServiceImpl implements FileService {

    private final Map<String, String> mimeToExtension = Map.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx", "text/csv", "csv");

    @Override
    public byte[] decodeBase64FileFromString(String base64File) {
        String base64 = base64File.split(",")[1];
        return Base64.getDecoder().decode(base64);
    }

    @Override
    public String getExtensionFileName(String base64File) {
        String mimeType = base64File.split(";")[0].split(":")[1];
        return mimeToExtension.getOrDefault(mimeType, "");
    }

    @Override
    public void saveFile(String fileName, byte[] fileContent) {
        // TODO document why this method is empty
    }

    @Override
    public String convertXLSXToString(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            for (Row row : sheet) {
                for (Cell cell : row) {
                    CellType cellType = cell.getCellType();
                    if (Objects.requireNonNull(cellType) == CellType.STRING) {
                        sb.append(cell.getStringCellValue()).append(" ");
                    } else if (cellType == CellType.NUMERIC) {
                        sb.append(cell.getNumericCellValue()).append(" ");
                    }
                }
                sb.append("\n"); // Nueva línea al final de cada fila
            }
        }
        workbook.close();
        return sb.toString();
    }

    @Override
    public byte[] convertXLSXToCSV(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);

            for (Row row : sheet) {
                StringBuilder sb = new StringBuilder();

                for (Cell cell : row) {
                    switch (cell.getCellType()) {
                        case STRING:
                            sb.append('"').append(cell.getStringCellValue()).append('"').append(',');
                            break;
                        case NUMERIC:
                            sb.append(cell.getNumericCellValue()).append(',');
                            break;
                        case BOOLEAN:
                            sb.append(cell.getBooleanCellValue()).append(',');
                            break;
                        case BLANK:
                            sb.append(',');
                            break;
                        default:
                            sb.append(cell).append(',');
                    }
                }

                sb.deleteCharAt(sb.length() - 1); // Elimina la última coma
                sb.append('\n'); // Nueva línea al final de cada fila
                outputStream.write(sb.toString().getBytes());
            }
        }

        workbook.close();
        return outputStream.toByteArray();
    }

    @Override
    public void convertXLSXToCSVAndWrite(byte[] fileXlsx, String outputPath) throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(fileXlsx);
             Workbook workbook = new XSSFWorkbook(inputStream);
             OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(fileWriter)
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                StringBuilder rowString = new StringBuilder();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String cellValue = getCellValue(cell);
                    rowString.append(cellValue).append(",");
                }
                // Remove the last comma
                if (!rowString.isEmpty()) {
                    rowString.setLength(rowString.length() - 1);
                }
                writer.write(rowString.toString());
                writer.newLine();
            }
            writer.flush();
        }
    }

    @Override
    public String convertCSVToString(String filePath) throws IOException {
        File file = new File(filePath);
        try(FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileContent = IOUtils.toByteArray(fileInputStream);
            return Base64.getEncoder().encodeToString(fileContent);
        }
    }

    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}

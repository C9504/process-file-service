package com.georeference.impl;

import com.georeference.services.FileService;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

    private final Map<String, String> mimeToExtension = Map.of("application/vnd.ms-excel", "xls", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"/*, "text/csv", "csv"*/);

    @Override
    public byte[] decodeBase64FileFromString(String base64File) {
        String base64 = base64File.split(",")[1];
        return Base64.getDecoder().decode(base64);
    }

    @Override
    public String getExtensionFileName(String base64File) {
        /*String mimeType = base64File.split(";")[0].split(":")[1];
        return mimeToExtension.getOrDefault(mimeType, "");*/
        if (base64File == null || !base64File.contains(",")) {
            return null;
        }

        // Extraer el prefijo data
        String[] parts = base64File.split(",", 2);
        String dataUrlPrefix = parts[0];

        // Validar prefijo
        if (!dataUrlPrefix.startsWith("data:")) {
            return null;
        }

        // Extraer el tipo MIME
        String[] prefixParts = dataUrlPrefix.split(";");
        if (prefixParts.length == 0 || !prefixParts[0].startsWith("data:")) {
            return null;
        }
        String mimeType = prefixParts[0].substring(5);

        // Verificar si el tipo MIME está en el mapa
        return mimeToExtension.get(mimeType);
    }

    @Override
    public boolean isValidExtensionFileName(String base64File) {
        // Validar formato de base64
        if (base64File == null || !base64File.contains(",")) {
            return false;
        }

        // Extraer el prefijo data
        String[] parts = base64File.split(",", 2);
        String dataUrlPrefix = parts[0];

        // Validar prefijo
        if (!dataUrlPrefix.startsWith("data:")) {
            return false;
        }

        // Extraer el MIME type
        String[] prefixParts = dataUrlPrefix.split(";");
        if (prefixParts.length == 0 || !prefixParts[0].startsWith("data:")) {
            return false;
        }
        String mimeType = prefixParts[0].substring(5);

        // Verificar si el MIME type está en el mapa
        return mimeToExtension.containsKey(mimeType);
    }

    @Override
    public void saveFile(String fileName, byte[] fileContent) {
        /**
         * TODO
         */
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
    public void convertXLSXToCSVAndWrite(byte[] fileXlsx, String outputPath, String base64File) throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(fileXlsx);
             //XSSFWorkbook es para .xlsx
             //HSSFWorkbook es para .xls
             Workbook workbook = loadWorkbook(inputStream, base64File);
             OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(fileWriter)
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            int lastColumnNum = sheet.getRow(0).getLastCellNum();
            int rowNum = 1;
            for (int cn = lastColumnNum; cn >= 0; cn--) {
                sheet.shiftColumns(cn, cn + 1, 1);
            }
            for (Row row : sheet) {
                Cell newCell;
                if (row.getRowNum() == 0) {
                    newCell = row.createCell(0, CellType.STRING);
                    newCell.setCellValue("id");
                } else {
                    newCell = row.createCell(0, CellType.NUMERIC);
                    newCell.setCellValue(String.valueOf(rowNum).replace(".0", ""));
                }
                rowNum++;
            }

            for (Row row : sheet) {
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
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
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

    private Workbook loadWorkbook(InputStream inputStream, String base64File) throws IOException {
        Workbook workbook = null;
        if (this.getExtensionFileName(base64File).equals("xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (this.getExtensionFileName(base64File).equals("xls")) {
            workbook = new HSSFWorkbook(inputStream);
        }
        return workbook;
    }

    @Override
    public int countRecords(String base64File) throws IOException {
        byte[] decodedBytes = decodeBase64FileFromString(base64File);
        Workbook workbook;
        int recordCount = 0;
        if (this.getExtensionFileName(base64File).equals("xlsx")) {
            workbook = new XSSFWorkbook(new ByteArrayInputStream(decodedBytes));
            Sheet sheet = workbook.getSheetAt(0);
            recordCount = sheet.getPhysicalNumberOfRows();
        }
        if (this.getExtensionFileName(base64File).equals("xls")) {
            workbook = new HSSFWorkbook(new ByteArrayInputStream(decodedBytes));
            Sheet sheet = workbook.getSheetAt(0);
            recordCount = sheet.getPhysicalNumberOfRows();
        }
        return recordCount;
    }
}

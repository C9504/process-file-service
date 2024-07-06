package com.georeference.impl;

import com.georeference.services.FileService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@Service
public class FileServiceImpl implements FileService {

    private final Map<String, String> mimeToExtension = Map.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx", "text/csv", "csv");

    @Override
    public byte[] decodeBase64FileFromString(String base64File) {
        String base64Data = base64File.split(",")[1];
        return Base64.getDecoder().decode(base64Data);
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
    public String convertXLSXToCSVAndWrite(MultipartFile file, String outputPath) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                for (Row row : sheet) {
                    StringBuilder line = new StringBuilder();
                    for (Cell cell : row) {
                        switch (cell.getCellType()) {
                            case STRING:
                                line.append('"').append(cell.getStringCellValue()).append('"').append(',');
                                break;
                            case NUMERIC:
                                line.append(cell.getNumericCellValue()).append(',');
                                break;
                            case BOOLEAN:
                                line.append(cell.getBooleanCellValue()).append(',');
                                break;
                            case BLANK:
                                line.append(',');
                                break;
                            default:
                                line.append(cell).append(',');
                        }
                    }
                    line.deleteCharAt(line.length() - 1); // Elimina la última coma
                    writer.write(line.toString());
                    writer.newLine(); // Nueva línea al final de cada fila
                }
            } catch (IOException e) {
                throw new IOException("Error al escribir el archivo CSV", e);
            }
        }

        workbook.close();
        return "Archivo CSV escrito correctamente en " + outputPath;
    }
}

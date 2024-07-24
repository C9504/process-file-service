package com.georeference.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    byte[] decodeBase64FileFromString(String base64File);
    String getExtensionFileName(String base64File);
    void saveFile(String fileName, byte[] fileContent);
    String convertXLSXToString(MultipartFile xlsxFile) throws IOException;
    byte[] convertXLSXToCSV(MultipartFile xlsxFile) throws IOException;
    void convertXLSXToCSVAndWrite(byte[] xlsxFile, String path) throws IOException;
    String convertCSVToString(String filePath) throws IOException;
}
package com.georeference.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    public byte[] decodeBase64FileFromString(String base64File);
    public String getExtensionFileName(String base64File);
    public void saveFile(String fileName, byte[] fileContent);
    String convertXLSXToString(MultipartFile xlsxFile) throws IOException;
    byte[] convertXLSXToCSV(MultipartFile xlsxFile) throws IOException;
    String convertXLSXToCSVAndWrite(MultipartFile xlsxFile, String path) throws IOException;
}

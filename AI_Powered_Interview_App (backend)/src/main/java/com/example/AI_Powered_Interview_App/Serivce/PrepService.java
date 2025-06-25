package com.example.AI_Powered_Interview_App.Serivce;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class PrepService {

    public String LoadData(MultipartFile file){

        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to extract text from PDF";
        }

    }
}

package com.skillgap.ai.parser;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PdfResumeParser {

    public String parse(byte[] fileBytes) throws IOException {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("File content is empty");
        }

        try (PDDocument document = Loader.loadPDF(fileBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("No text could be extracted. The PDF may be scanned, image-only, or corrupted.");
            }

            return text.trim();
        } catch (IOException e) {
            throw new IOException("Failed to parse PDF resume: " + e.getMessage(), e);
        }
    }
}

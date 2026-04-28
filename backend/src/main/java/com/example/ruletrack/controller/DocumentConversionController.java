package com.example.ruletrack.controller;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/documents")
public class DocumentConversionController {

    @PostMapping(
            value = "/convert",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> convertToMarkdown(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("No se proporcionó ningún archivo.");
        }

        String originalName = file.getOriginalFilename() != null
                ? file.getOriginalFilename() : "document";
        String ext = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase()
                : "";
        String baseName = originalName.contains(".")
                ? originalName.substring(0, originalName.lastIndexOf('.'))
                : originalName;

        String markdown;
        if ("pdf".equals(ext)) {
            byte[] bytes = file.getBytes();
            try (PDDocument doc = Loader.loadPDF(new RandomAccessReadBuffer(bytes))) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(doc);
                markdown = formatAsMarkdown(text, baseName);
            }
        } else {
            String text = new String(file.getBytes(), StandardCharsets.UTF_8);
            markdown = "md".equals(ext) ? text : formatAsMarkdown(text, baseName);
        }

        return ResponseEntity.ok(markdown);
    }

    private String formatAsMarkdown(String text, String title) {
        String[] lines = text.split("\\r?\\n");
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(title).append("\n\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            if (trimmed.length() < 80
                    && trimmed.matches("^[A-ZÁÉÍÓÚÑ\\d].*")
                    && !trimmed.endsWith(",")) {
                sb.append("## ").append(trimmed).append("\n\n");
            } else {
                sb.append(trimmed).append("\n\n");
            }
        }
        return sb.toString();
    }
}

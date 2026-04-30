package com.example.ruletrack.controller;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

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
        } else if ("docx".equals(ext)) {
            byte[] bytes = file.getBytes();
            try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(bytes))) {
                StringBuilder sb = new StringBuilder();
                // Extract paragraphs
                for (XWPFParagraph paragraph : doc.getParagraphs()) {
                    String text = paragraph.getText();
                    if (text != null && !text.isBlank()) {
                        sb.append(text).append("\n");
                    } else {
                        sb.append("\n"); // preserve blank lines between blocks
                    }
                }
                // Extract tables
                for (XWPFTable table : doc.getTables()) {
                    sb.append("\n");
                    for (XWPFTableRow row : table.getRows()) {
                        String rowText = row.getTableCells().stream()
                                .map(cell -> cell.getText().trim())
                                .filter(t -> !t.isEmpty())
                                .collect(Collectors.joining(" | "));
                        if (!rowText.isEmpty()) {
                            sb.append(rowText).append("\n");
                        }
                    }
                    sb.append("\n");
                }
                markdown = formatAsMarkdown(sb.toString(), baseName);
            }
        } else if ("md".equals(ext)) {
            markdown = new String(file.getBytes(), StandardCharsets.UTF_8);
        } else {
            String text = new String(file.getBytes(), StandardCharsets.UTF_8);
            markdown = formatAsMarkdown(text, baseName);
        }

        return ResponseEntity.ok(markdown);
    }

    private String formatAsMarkdown(String text, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(title).append("\n\n");

        // Split into blocks separated by blank lines, join lines within each block
        String[] blocks = text.split("\\r?\\n(\\s*\\r?\\n)+");
        for (String block : blocks) {
            String joined = Arrays.stream(block.split("\\r?\\n"))
                    .map(String::trim)
                    .filter(l -> !l.isEmpty())
                    .collect(Collectors.joining(" "));

            if (joined.isEmpty()) continue;

            if (isHeading(joined)) {
                sb.append("## ").append(joined).append("\n\n");
            } else {
                sb.append(joined).append("\n\n");
            }
        }

        return sb.toString();
    }

    private boolean isHeading(String line) {
        if (line.length() > 80) return false;
        if (line.matches("(?i)^(Art[ií]culo|Cap[ií]tulo|Secci[oó]n|T[ií]tulo|Anexo|Parte)\\b.*")) return true;
        if (line.equals(line.toUpperCase()) && line.length() > 2 && line.matches(".*[A-ZÁÉÍÓÚÑ].*")) return true;
        if (line.length() < 50 && !line.endsWith(".") && !line.endsWith(",") && !line.endsWith(";")) return true;
        return false;
    }
}

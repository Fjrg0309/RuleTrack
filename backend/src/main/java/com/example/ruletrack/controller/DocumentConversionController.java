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
                stripper.setSortByPosition(true);
                String text = stripper.getText(doc);
                markdown = pdfToMarkdown(text, baseName);
            }
        } else if ("docx".equals(ext)) {
            byte[] bytes = file.getBytes();
            try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(bytes))) {
                markdown = docxToMarkdown(doc, baseName);
            }
        } else if ("md".equals(ext)) {
            markdown = new String(file.getBytes(), StandardCharsets.UTF_8);
        } else {
            String text = new String(file.getBytes(), StandardCharsets.UTF_8);
            markdown = pdfToMarkdown(text, baseName);
        }

        return ResponseEntity.ok(markdown);
    }

    /**
     * Converts raw text extracted from a PDF (or plain text file) to Markdown.
     * Preserves numbered lists, bullet points, headings and paragraph structure
     * by processing line by line instead of joining everything into one block.
     */
    private String pdfToMarkdown(String text, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(title).append("\n\n");

        String[] lines = text.split("\\r?\\n");
        int i = 0;

        while (i < lines.length) {
            String line = lines[i].trim();

            if (line.isEmpty()) {
                i++;
                continue;
            }

            // Numbered list item: "1. text" or "1) text"
            if (line.matches("^\\d+[.)]\\s+.+")) {
                StringBuilder item = new StringBuilder(line);
                i++;
                // Accumulate wrapped continuation lines belonging to this item
                while (i < lines.length) {
                    String next = lines[i].trim();
                    if (next.isEmpty()) break;
                    if (next.matches("^\\d+[.)]\\s+.*")) break;
                    if (next.matches("^[•*]\\s+.*") || next.matches("^-\\s+\\S.*")) break;
                    if (isHeadingLine(next)) break;
                    item.append(" ").append(next);
                    i++;
                }
                sb.append(item).append("\n");
                continue;
            }

            // Bullet item: "• text", "* text", "- text" (dash + non-space)
            if (line.matches("^[•*]\\s+.+") || line.matches("^-\\s+\\S.*")) {
                String content = line.replaceFirst("^[•*\\-]\\s+", "");
                sb.append("- ").append(content).append("\n");
                i++;
                continue;
            }

            // Heading line
            if (isHeadingLine(line)) {
                StringBuilder heading = new StringBuilder(line);
                // Join next line if this heading ends with hyphen (title split across PDF lines)
                if (line.endsWith("-") && i + 1 < lines.length) {
                    String next = lines[i + 1].trim();
                    if (!next.isEmpty() && isHeadingLine(next) && !next.matches("^\\d+[.)]\\s+.*")) {
                        heading.append(" ").append(next);
                        i++;
                    }
                }
                sb.append("\n## ").append(heading).append("\n\n");
                i++;
                continue;
            }

            // Regular paragraph: accumulate until blank line or new structural element
            StringBuilder para = new StringBuilder(line);
            i++;
            while (i < lines.length) {
                String next = lines[i].trim();
                if (next.isEmpty()) break;
                if (next.matches("^\\d+[.)]\\s+.*")) break;
                if (next.matches("^[•*]\\s+.*") || next.matches("^-\\s+\\S.*")) break;
                if (isHeadingLine(next)) break;
                para.append(" ").append(next);
                i++;
            }
            sb.append(para).append("\n\n");
        }

        return sb.toString();
    }

    /**
     * Converts a DOCX document to Markdown using paragraph style metadata
     * (heading levels, list paragraphs) for accurate structure detection.
     */
    private String docxToMarkdown(XWPFDocument doc, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(title).append("\n\n");

        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            String text = paragraph.getText();
            if (text == null || text.isBlank()) continue;

            String style = paragraph.getStyle() != null ? paragraph.getStyle().toLowerCase() : "";

            // Heading styles: Heading1, Heading2, Título1, Ttulo1, etc.
            if (style.matches("heading\\d+|t[ií]tulo\\d+|ttulo\\d+")) {
                int level = extractHeadingLevel(style);
                sb.append("#".repeat(Math.min(level + 1, 6))).append(" ").append(text).append("\n\n");
                continue;
            }

            // List items: ListParagraph style or numID-based numbering
            boolean isList = "listparagraph".equals(style) || paragraph.getNumID() != null;
            if (isList) {
                if (text.matches("^\\d+[.)]\\s+.+")) {
                    sb.append(text).append("\n");
                } else {
                    String content = text.replaceFirst("^[•*\\-]\\s+", "");
                    sb.append("- ").append(content).append("\n");
                }
                continue;
            }

            // Numbered item by text pattern (fallback for non-styled lists)
            if (text.matches("^\\d+[.)]\\s+.+")) {
                sb.append(text).append("\n");
                continue;
            }

            // Bullet item by text pattern (fallback)
            if (text.matches("^[•*]\\s+.+") || text.matches("^-\\s+\\S.*")) {
                String content = text.replaceFirst("^[•*\\-]\\s+", "");
                sb.append("- ").append(content).append("\n");
                continue;
            }

            // Heading by content heuristic (no explicit style)
            if (isHeadingLine(text)) {
                sb.append("\n## ").append(text).append("\n\n");
                continue;
            }

            // Regular paragraph
            sb.append(text).append("\n\n");
        }

        // Tables with header separator
        for (XWPFTable table : doc.getTables()) {
            sb.append("\n");
            boolean firstRow = true;
            for (XWPFTableRow row : table.getRows()) {
                String rowText = row.getTableCells().stream()
                        .map(cell -> cell.getText().trim())
                        .filter(t -> !t.isEmpty())
                        .collect(Collectors.joining(" | "));
                if (!rowText.isEmpty()) {
                    sb.append("| ").append(rowText).append(" |\n");
                    if (firstRow) {
                        long cols = row.getTableCells().stream()
                                .filter(c -> !c.getText().trim().isEmpty()).count();
                        sb.append("|").append(" --- |".repeat((int) cols)).append("\n");
                        firstRow = false;
                    }
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private int extractHeadingLevel(String style) {
        String digits = style.replaceAll("\\D", "");
        if (!digits.isEmpty()) {
            try {
                return Integer.parseInt(digits);
            } catch (NumberFormatException ignored) { }
        }
        return 1;
    }

    private boolean isHeadingLine(String line) {
        if (line.isEmpty() || line.length() > 100) return false;
        // Exclude list items
        if (line.matches("^\\d+[.)]\\s+.*")) return false;
        if (line.matches("^[•*\\-]\\s+.*")) return false;
        // Explicit section keywords
        if (line.matches("(?i)^(Art[ií]culo|Cap[ií]tulo|Secci[oó]n|T[ií]tulo|Anexo|Parte)\\b.*")) return true;
        // All-uppercase line (document title or section label)
        if (line.equals(line.toUpperCase()) && line.length() > 2 && line.matches(".*[A-ZÁÉÍÓÚÑ].*")) return true;
        // Short line not ending with sentence-closing punctuation
        if (line.length() <= 70 && !line.endsWith(".") && !line.endsWith(",")
                && !line.endsWith(";") && !line.endsWith(":")) return true;
        return false;
    }
}

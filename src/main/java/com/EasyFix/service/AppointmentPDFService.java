package com.EasyFix.service;

import com.EasyFix.model.Appointment;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import static java.awt.Color.DARK_GRAY;
import static java.awt.Color.LIGHT_GRAY;

@Service
public class AppointmentPDFService {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public byte[] generateAppointmentPdf(Appointment appointment) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            // --- Header Title ---
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, DARK_GRAY);
            Paragraph title = new Paragraph("SERVICE APPOINTMENT RECEIPT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // --- Metadata Overview ---
            document.add(new Paragraph("Appointment ID: #" + appointment.getId()));
            document.add(new Paragraph("Status: " + appointment.getStatus()));
            document.add(new Paragraph("Date of Service: " + appointment.getStartTime().format(dateFormatter)));
            document.add(new Paragraph("Time Window: " + appointment.getStartTime().format(timeFormatter) +
                    " - " + appointment.getEndTime().format(timeFormatter) + " (2 Hours)"));
            document.add(new Paragraph(" "));

            // --- Breakdown Table ---
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4.0f, 4.0f, 2.0f});

            addTableHeader(table, "Customer (Homeowner)");
            addTableHeader(table, "Service Professional");
            addTableHeader(table, "Problem Description");

            // Add Details Rows
            table.addCell(appointment.getCustomer().getUsername() + "\n" + appointment.getCustomer().getEmail());
            table.addCell(appointment.getProvider().getProviderDetails().getBusinessName() + "\n" + appointment.getProvider().getEmail());
            table.addCell(appointment.getProblemDescription() != null ? appointment.getProblemDescription() : "Standard Maintenance Request");

            document.add(table);
            document.add(new Paragraph(" "));

            // --- Cost Segment ---
            Paragraph total = new Paragraph("Total Estimated Cost: " + appointment.getTotalPrice() + " BHD",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            // --- Footer Notes ---
            document.add(new Paragraph(" "));
            Paragraph footerNote = new Paragraph("Thank you for choosing EasyFix. Please contact support if you need to reschedule.",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, DARK_GRAY));
            footerNote.setAlignment(Element.ALIGN_CENTER);
            document.add(footerNote);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate service appointment PDF confirmation", e);
        }
    }

    private void addTableHeader(PdfPTable table, String columnTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(LIGHT_GRAY);
        header.setPhrase(new Phrase(columnTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        header.setPadding(6);
        table.addCell(header);
    }
}
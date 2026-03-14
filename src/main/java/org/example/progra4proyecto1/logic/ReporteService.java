package org.example.progra4proyecto1.logic;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.una.bolsaempleo.data.PuestoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service("reporteService")
public class ReporteService {

    @Autowired private PuestoRepository puestoRepository;

    public byte[] reportePuestosPorMes(int mes, int anio) throws DocumentException {
        List<Puesto> puestos = puestoRepository.findByMesYAnio(mes, anio);
        Document doc = new Document(PageSize.A4, 36, 36, 54, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 9);
        String[] meses = {"","Enero","Febrero","Marzo","Abril","Mayo","Junio",
                "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};

        Paragraph titulo = new Paragraph("Puestos publicados - " + meses[mes] + " " + anio, titleFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        doc.add(titulo);
        doc.add(new Paragraph("Total: " + puestos.size(), cellFont));
        doc.add(Chunk.NEWLINE);

        PdfPTable tabla = new PdfPTable(new float[]{1f, 3f, 2f, 1.5f, 1.5f, 1f});
        tabla.setWidthPercentage(100);
        for (String h : new String[]{"ID","Descripción","Empresa","Salario","Moneda","Tipo"}) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new BaseColor(41, 128, 185));
            cell.setPadding(6);
            tabla.addCell(cell);
        }
        for (Puesto p : puestos) {
            tabla.addCell(new PdfPCell(new Phrase(p.getId().toString(), cellFont)));
            String desc = p.getDescripcion().length() > 50 ? p.getDescripcion().substring(0,50)+"..." : p.getDescripcion();
            tabla.addCell(new PdfPCell(new Phrase(desc, cellFont)));
            tabla.addCell(new PdfPCell(new Phrase(p.getEmpresa().getNombre(), cellFont)));
            tabla.addCell(new PdfPCell(new Phrase(p.getSalario().toPlainString(), cellFont)));
            tabla.addCell(new PdfPCell(new Phrase(p.getMoneda().getCodigo(), cellFont)));
            tabla.addCell(new PdfPCell(new Phrase(p.getTipo().name(), cellFont)));
        }
        doc.add(tabla);
        doc.close();
        return out.toByteArray();
    }

    public byte[] reporteCoincidencias(List<Puesto> puestos, List<List<CandidatoResult>> candidatosPorPuesto) throws DocumentException {
        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font subFont  = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 9);

        Paragraph titulo = new Paragraph("Reporte de Coincidencias Candidatos/Puestos", titleFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(15);
        doc.add(titulo);

        for (int i = 0; i < puestos.size(); i++) {
            Puesto p = puestos.get(i);
            List<CandidatoResult> cands = candidatosPorPuesto.get(i);
            Paragraph sub = new Paragraph("Puesto #" + p.getId() + ": " + p.getDescripcion().substring(0, Math.min(50, p.getDescripcion().length())), subFont);
            sub.setSpacingBefore(10); sub.setSpacingAfter(5);
            doc.add(sub);
            if (cands.isEmpty()) { doc.add(new Paragraph("  Sin candidatos.", cellFont)); continue; }
            PdfPTable t = new PdfPTable(new float[]{3f, 1.5f, 1.5f, 2f});
            t.setWidthPercentage(100);
            for (String h : new String[]{"Oferente","Req. Cumplidos","Total Req.","% Coincidencia"}) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new BaseColor(39, 174, 96));
                cell.setPadding(5); t.addCell(cell);
            }
            for (CandidatoResult cr : cands) {
                t.addCell(new PdfPCell(new Phrase(cr.getOferente().getNombre() + " " + cr.getOferente().getPrimerApellido(), cellFont)));
                t.addCell(new PdfPCell(new Phrase(String.valueOf(cr.getRequisitosCumplidos()), cellFont)));
                t.addCell(new PdfPCell(new Phrase(String.valueOf(cr.getRequisitosTotal()), cellFont)));
                t.addCell(new PdfPCell(new Phrase(String.format("%.2f%%", cr.getPorcentajeCoincidencia()), cellFont)));
            }
            doc.add(t);
        }
        doc.close();
        return out.toByteArray();
    }
}

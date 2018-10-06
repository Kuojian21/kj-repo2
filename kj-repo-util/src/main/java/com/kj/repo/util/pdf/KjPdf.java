package com.kj.repo.util.pdf;

import java.io.InputStream;
import java.io.OutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.kj.repo.util.close.KjClose;

public class KjPdf {

    public static void fromHtml(InputStream htmlIs, InputStream cssIs, OutputStream os) {
        Document document = null;
        try {
            document = new Document(PageSize.A4);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, os);
            document.open();
            XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
            worker.parseXHtml(pdfWriter, document, htmlIs, cssIs/*, new XMLWorkerFontProvider() {
                @Override
                public Font getFont(final String fontname, final String encoding, final boolean embedded,
                                    final float size, final int style, final BaseColor color) {
                    BaseFont bf = null;
                    try {
                        bf = BaseFont.createFont("Helvetica", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Font font = new Font(bf, size, style, color);
                    font.setColor(color);
                    return font;
                }
            }*/);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KjClose.close(htmlIs, cssIs);
            KjClose.close("close", document);
        }
    }
}
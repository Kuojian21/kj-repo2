//package com.kj.repo.util.pdf;
//
//import java.io.BufferedReader;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import com.itextpdf.text.BaseColor;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.PageSize;
//import com.itextpdf.text.pdf.BaseFont;
//import com.itextpdf.text.pdf.PdfWriter;
//import com.itextpdf.tool.xml.XMLWorkerFontProvider;
//import com.itextpdf.tool.xml.XMLWorkerHelper;
//
//public class HtmlToPDF {
//
//    public static void fromHtml(InputStream htmlIs, InputStream cssIs, OutputStream os) {
//        try {
//            Document document =
//                    new Document(PageSize.A4); //A4纸大小 可以选择
//            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(path + "/" + name));
//            document.open();
//            FileReader reader = new FileReader(txt);
//            BufferedReader br = new BufferedReader(reader);
//            String temStr = null;
//            String inputStr = "";
//            while ((temStr = br.readLine()) != null) {
//                inputStr += temStr;
//            }
//            XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
//            worker.parseXHtml(pdfWriter, document, htmlIs, cssIs, new MyFontProvidesr(ttf));
//            br.close();
//            reader.close();
//            document.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            System.out.println("转换完成");
//        }
//    }
//}
//
//class MyFontProvidesr extends XMLWorkerFontProvider {
//    private String ttf;
//
//    //字体文件
//    public MyFontProvidesr(String ttf) {
//        super();
//        this.ttf = ttf;
//    }
//
//    public Font getFont(final String fontname, final String encoding, final boolean embedded, final float size, final int style, final BaseColor color) {
//        BaseFont bf = null;
//        try {
//            bf = BaseFont.createFont(ttf, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Font font = new Font(bf, size, style, color);
//        font.setColor(color);
//        return font;
//    }
//}
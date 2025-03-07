package pos.presentation.Clientes;

import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import pos.logic.Cliente;
import pos.logic.Service;
import pos.Application;

public class Controller {
    private View view;
    private Model model;

    public Controller(View view, Model model) {
        model.init(Service.instance().search(new Cliente()));
        this.view = view;
        this.model = model;
        view.setController(this);
        view.setModel(model);
    }

    public void search(Cliente filter) throws Exception {
        model.setFilter(filter);
        model.setMode(Application.MODE_CREATE);
        model.setCurrent(new Cliente());
        model.setList(Service.instance().search(model.getFilter()));
    }

    public void save(Cliente e) throws Exception {
        switch (model.getMode()) {
            case Application.MODE_CREATE:
                Service.instance().create(e);
                break;
            case Application.MODE_EDIT:
                Service.instance().update(e);
                break;
        }
        model.setFilter(new Cliente());
        search(model.getFilter());
    }

    public void edit(int row) {
        Cliente e = model.getList().get(row);
        try {
            model.setMode(Application.MODE_EDIT);
            model.setCurrent(Service.instance().read(e));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void delete() throws Exception {
        Service.instance().delete(model.getCurrent());
        search(model.getFilter());
    }

    public void clear() {
        model.setMode(Application.MODE_CREATE);
        model.setCurrent(new Cliente());
    }

    public void print() throws Exception {
        String dest = "clientes.pdf";
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        try (PdfWriter writer = new PdfWriter(dest);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.setMargins(20, 20, 20, 20);

            Table header = new Table(1);
            header.setWidth(550);
            header.setHorizontalAlignment(HorizontalAlignment.CENTER);
            header.addCell(getCell(new Paragraph("Listado de Clientes")
                    .setFont(font)
                    .setBold()
                    .setFontSize(22f), TextAlignment.CENTER, false));
            document.add(header);

            document.add(new Paragraph(""));
            document.add(new Paragraph(""));

            Color headerBkg = ColorConstants.DARK_GRAY;
            Color headerFrg = ColorConstants.WHITE;
            Color rowBkg1 = ColorConstants.LIGHT_GRAY;
            Color rowBkg2 = ColorConstants.WHITE;

            Table body = new Table(new float[]{2, 4, 4, 3});
            body.setWidth(550);
            body.setHorizontalAlignment(HorizontalAlignment.CENTER);

            body.addCell(getStyledHeaderCell("Id", headerBkg, headerFrg));
            body.addCell(getStyledHeaderCell("Nombre", headerBkg, headerFrg));
            body.addCell(getStyledHeaderCell("Email", headerBkg, headerFrg));
            body.addCell(getStyledHeaderCell("Teléfono", headerBkg, headerFrg));

            boolean isOddRow = true;

            for (Cliente e : model.getList()) {
                Color rowBkg = isOddRow ? rowBkg1 : rowBkg2;
                body.addCell(getStyledBodyCell(e.getId(), rowBkg));
                body.addCell(getStyledBodyCell(e.getNombre(), rowBkg));
                body.addCell(getStyledBodyCell(e.getEmail(), rowBkg));
                body.addCell(getStyledBodyCell(e.getTelefono(), rowBkg));
                isOddRow = !isOddRow;
            }
            document.add(body);
        }
    }

    private Cell getStyledHeaderCell(String content, Color bkgColor, Color frgColor) {
        return getCell(new Paragraph(content)
                .setBold()
                .setFontSize(12f)
                .setBackgroundColor(bkgColor)
                .setFontColor(frgColor)
                .setPadding(5), TextAlignment.CENTER, true);
    }

    private Cell getStyledBodyCell(String content, Color bkgColor) {
        return getCell(new Paragraph(content)
                .setFontSize(10f)
                .setPadding(5)
                .setBackgroundColor(bkgColor), TextAlignment.CENTER, true);
    }

    private Cell getCell(Paragraph content, TextAlignment alignment, boolean border) {
        Cell cell = new Cell().add(content);
        cell.setTextAlignment(alignment);
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        if (!border) {
            cell.setBorder(Border.NO_BORDER);
        }
        return cell;
    }
}
package org.esfr.BazarBEG.controladores;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.element.Table;
import jakarta.servlet.http.HttpServletResponse;
import org.esfr.BazarBEG.modelos.DetallePedido;
import org.esfr.BazarBEG.modelos.Pedido;
import org.esfr.BazarBEG.servicios.interfaces.IPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private IPedidoService pedidoService;

    // -------------------- LISTADO --------------------
    @GetMapping
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<Pedido> pedidos = pedidoService.buscarTodosPaginados(pageable);
        model.addAttribute("pedidos", pedidos);

        int totalPages = pedidos.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "pedido/index";
    }

    // -------------------- DETALLE --------------------
    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model) {
        Pedido pedido = pedidoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));
        model.addAttribute("pedido", pedido);
        return "pedido/details";
    }

    // -------------------- ELIMINA  --------------------
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirect) {
        pedidoService.eliminarPorId(id);
        redirect.addFlashAttribute("msg", "Pedido eliminado exitosamente");
        return "redirect:/pedidos";
    }

    // -------------------- CONFIRMACIÓN DE ELIMINACIÓN --------------------
    @GetMapping("/delete-confirm/{id}")
    public String showDeleteConfirmation(@PathVariable("id") Integer id, Model model) {
        Pedido pedido = pedidoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));
        model.addAttribute("pedido", pedido);
        return "pedido/delete";
    }

    // ------- FACTURA PDF ------
    @GetMapping("/factura/{id}")
    public void generarFacturaPDF(@PathVariable("id") Integer id, HttpServletResponse response) throws IOException {
        // 1. Obtener el pedido con todos los datos necesarios
        //    Ahora este método del servicio cargará los detalles y el usuario de forma segura
        Pedido pedido = pedidoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));

        // 2. Configurar la respuesta HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=factura_" + id + ".pdf");

        // 3. Crear el documento PDF
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // 4. Construir el contenido de la factura
        // Título
        document.add(new Paragraph("FACTURA DE PEDIDO")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Información del cliente y del pedido
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        document.add(new Paragraph("Número de Pedido: " + pedido.getId()));
        document.add(new Paragraph("Cliente: " + pedido.getUsuario().getNombre()));
        document.add(new Paragraph("Fecha: " + sdf.format(pedido.getFechaPedido())));
        document.add(new Paragraph("Estado: " + pedido.getEstado()));
        document.add(new Paragraph("\n"));

        // Tabla de detalles del pedido
        float[] columnWidths = {200F, 50F, 50F, 70F};
        Table table = new Table(columnWidths);


        table.addCell(new Cell().add(new Paragraph("Producto").setBold()));
        table.addCell(new Cell().add(new Paragraph("Cantidad").setBold()));
        table.addCell(new Cell().add(new Paragraph("Precio U.").setBold()));
        table.addCell(new Cell().add(new Paragraph("Subtotal").setBold()));

        for (DetallePedido detalle : pedido.getDetalles()) {
            table.addCell(new Cell().add(new Paragraph(detalle.getProducto().getNombre())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(detalle.getCantidad()))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", detalle.getPrecioUnitario()))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", detalle.getCantidad() * detalle.getPrecioUnitario()))));
        }

        document.add(table);

        // Total del pedido
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("TOTAL: $" + String.format("%.2f", pedido.getTotal()))
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT));

        // 5. Cerrar el documento
        document.close();
    }
}
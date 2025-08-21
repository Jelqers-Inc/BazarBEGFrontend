package org.esfr.BazarBEG.controladores;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import jakarta.servlet.http.HttpServletResponse;
import org.esfr.BazarBEG.modelos.Catalogo;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.servicios.interfaces.ICatalogoService;
import org.esfr.BazarBEG.servicios.interfaces.ICategoriaService;
import org.esfr.BazarBEG.servicios.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/catalogos")
public class CatalogoController {

    @Autowired
    private ICatalogoService catalogoService;

    @Autowired
    private ICategoriaService categoriaService;

    @Autowired
    private IProductoService productoService;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/catalogos/";

    // -------------------- LISTADO --------------------
    @GetMapping
    public String index(Model model,@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1) - 1;  // Pageable es 0-based
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Catalogo> catalogos = catalogoService.buscarTodosPaginados(pageable);

        model.addAttribute("catalogos", catalogos);

        int totalPages = catalogos.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "catalogo/index";
    }

    // -------------------- CREAR --------------------
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("catalogo", new Catalogo());
        model.addAttribute("categorias", categoriaService.obtenerTodos());
        model.addAttribute("productos", new ArrayList<Producto>());
        return "catalogo/create";
    }

    // -------------------- AJAX: productos por categoría --------------------
    @GetMapping("/productosPorCategoria/{categoriaId}")
    @ResponseBody
    public List<Map<String, Object>> productosPorCategoria(@PathVariable Integer categoriaId) {
        return productoService.findByCategoriaId(categoriaId)
                .stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("nombre", p.getNombre());
                    map.put("precio", p.getPrecio());
                    return map;
                })
                .collect(Collectors.toList());
    }

    // -------------------- GUARDAR --------------------
    @PostMapping("/save")
    public String save(@ModelAttribute Catalogo catalogo,
                       @RequestParam(value = "portadaFile", required = false) MultipartFile portadaFile,
                       @RequestParam(value = "productosIds", required = false) List<Integer> productosIds,
                       RedirectAttributes redirect) {

        try {
            // Resolver categoría seleccionada
            if (catalogo.getCategoria() != null && catalogo.getCategoria().getId() != null) {
                catalogo.setCategoria(categoriaService.buscarPorId(catalogo.getCategoria().getId()).orElse(null));
            }

            // Guardar portada
            if (portadaFile != null && !portadaFile.isEmpty()) {
                Path uploadPath = Paths.get("C:/bazar/uploads/catalogos/"); // ruta absoluta segura
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = "portada_" + System.currentTimeMillis() + "_" + portadaFile.getOriginalFilename();
                Files.copy(portadaFile.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                catalogo.setPortadaImagen("/uploads/catalogos/" + fileName);
            }

            // Guardar productos seleccionados
            if (productosIds != null && !productosIds.isEmpty()) {
                catalogo.setProductos(productoService.buscarPorIds(productosIds));
            } else {
                catalogo.setProductos(new ArrayList<>());
            }

            // Guardar catálogo en BD
            catalogoService.crearOEditar(catalogo);

            redirect.addFlashAttribute("msg", "Catálogo guardado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("error", "Error al guardar el catálogo: " + e.getMessage());
            return "redirect:/catalogos/create";
        }

        return "redirect:/catalogos";
    }



    // -------------------- DETALLES --------------------
    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") int id, Model model) {
        Catalogo catalogo = catalogoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Catálogo no encontrado"));
        model.addAttribute("catalogo", catalogo);
        return "catalogo/details";
    }

    // -------------------- EDITAR --------------------
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        Catalogo catalogo = catalogoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Catálogo no encontrado"));
        model.addAttribute("catalogo", catalogo);
        model.addAttribute("categorias", categoriaService.obtenerTodos());
        model.addAttribute("productos", catalogo.getCategoria() != null ?
                productoService.findByCategoriaId(catalogo.getCategoria().getId()) : new ArrayList<>());
        return "catalogo/edit";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") int id,
                         @Valid @ModelAttribute Catalogo catalogo,
                         BindingResult result,
                         @RequestParam("portadaFile") MultipartFile portadaFile,
                         @RequestParam("productosIds") List<Integer> productosIds,
                         RedirectAttributes redirect) throws IOException {

        if (result.hasErrors()) return "catalogo/edit";

        Catalogo catalogoExistente = catalogoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Catálogo no encontrado"));

        catalogoExistente.setNombre(catalogo.getNombre());
        catalogoExistente.setDescripcion(catalogo.getDescripcion());
        catalogoExistente.setFechaInicio(catalogo.getFechaInicio());
        catalogoExistente.setFechaFin(catalogo.getFechaFin());
        catalogoExistente.setCategoria(catalogo.getCategoria());

        if (!portadaFile.isEmpty()) {
            String portadaName = "portada_" + System.currentTimeMillis() + "_" + portadaFile.getOriginalFilename();
            portadaFile.transferTo(new File(UPLOAD_DIR + portadaName));
            catalogoExistente.setPortadaImagen("/uploads/catalogos/" + portadaName);
        }

        catalogoExistente.setProductos(productoService.buscarPorIds(productosIds));
        catalogoExistente.setPdfPath(generarPDF(catalogoExistente));
        catalogoService.crearOEditar(catalogoExistente);

        redirect.addFlashAttribute("success", "Catálogo actualizado correctamente");
        return "redirect:/catalogos";
    }




    // -------------------- ELIMINAR --------------------
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes redirect) {
        catalogoService.eliminarPorId(id);
        redirect.addFlashAttribute("success", "Catálogo eliminado correctamente");
        return "redirect:/catalogos";
    }

    // -------------------- MÉTODO GENERAR PDF --------------------
    private String generarPDF(Catalogo catalogo) throws IOException {
        String pdfFileName = "catalogo_" + System.currentTimeMillis() + ".pdf";
        String pdfPath = UPLOAD_DIR + pdfFileName;

        PdfWriter writer = new PdfWriter(pdfPath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Catálogo: " + catalogo.getNombre()));
        document.add(new Paragraph("Descripción: " + catalogo.getDescripcion()));
        document.add(new Paragraph("Fecha inicio: " + catalogo.getFechaInicio()));
        document.add(new Paragraph("Fecha fin: " + catalogo.getFechaFin()));
        if (catalogo.getCategoria() != null)
            document.add(new Paragraph("Categoría: " + catalogo.getCategoria().getNombre()));

        document.add(new Paragraph("Productos:"));
        for (Producto p : catalogo.getProductos()) {
            document.add(new Paragraph("- " + p.getNombre() + " $" + p.getPrecio()));
        }

        document.close();
        return "/uploads/catalogos/" + pdfFileName;
    }

    // -------------------- VISTA PREVIA PDF --------------------
    @PostMapping("/preview-pdf")
    public void previewPDF(@ModelAttribute Catalogo catalogo,
                           @RequestParam(value = "portadaFile", required = false) MultipartFile portadaFile,
                           @RequestParam(value = "productosIds", required = false) List<Integer> productosIds,
                           HttpServletResponse response) throws IOException {

        // Resolver categoría
        if (catalogo.getCategoria() != null && catalogo.getCategoria().getId() != null) {
            catalogo.setCategoria(
                    categoriaService.buscarPorId(catalogo.getCategoria().getId()).orElse(null)
            );
        }

        // Resolver productos seleccionados
        if (productosIds != null && !productosIds.isEmpty()) {
            catalogo.setProductos(productoService.buscarPorIds(productosIds));
        } else catalogo.setProductos(new ArrayList<>());

        // Preparar PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=catalogo_preview.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Título y detalles
        document.add(new Paragraph("Catálogo: " + catalogo.getNombre()));
        document.add(new Paragraph("Descripción: " + catalogo.getDescripcion()));
        document.add(new Paragraph("Fecha inicio: " + catalogo.getFechaInicio()));
        document.add(new Paragraph("Fecha fin: " + catalogo.getFechaFin()));

        if (catalogo.getCategoria() != null)
            document.add(new Paragraph("Categoría: " + catalogo.getCategoria().getNombre()));

        // Imagen de portada desde el archivo recibido
        if (portadaFile != null && !portadaFile.isEmpty()) {
            byte[] bytes = portadaFile.getBytes();
            com.itextpdf.layout.element.Image img = new com.itextpdf.layout.element.Image(
                    com.itextpdf.io.image.ImageDataFactory.create(bytes)
            );
            img.setAutoScale(true);
            document.add(img);
        }

        // Productos
        document.add(new Paragraph("Productos:"));
        for (Producto p : catalogo.getProductos()) {
            document.add(new Paragraph("- " + p.getNombre() + " ($" + p.getPrecio() + ")"));
        }

        document.close();
    }


    // -------------------- REPORTE PDF (descarga o vista previa) --------------------
    @GetMapping("/reporte/{visualizacion}")
    public void generarReportePDF(@PathVariable("visualizacion") String visualizacion,
                                  @RequestParam("id") int catalogoId,
                                  HttpServletResponse response) throws IOException {

        Catalogo catalogo = catalogoService.buscarPorId(catalogoId)
                .orElseThrow(() -> new IllegalArgumentException("Catálogo no encontrado"));

        String contentDisposition = visualizacion.equalsIgnoreCase("attachment") ?
                "attachment; filename=catalogo.pdf" :
                "inline; filename=catalogo.pdf";

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", contentDisposition);

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Catálogo: " + catalogo.getNombre()));
        document.add(new Paragraph("Descripción: " + catalogo.getDescripcion()));
        document.add(new Paragraph("Fecha inicio: " + catalogo.getFechaInicio()));
        document.add(new Paragraph("Fecha fin: " + catalogo.getFechaFin()));
        if (catalogo.getCategoria() != null)
            document.add(new Paragraph("Categoría: " + catalogo.getCategoria().getNombre()));

        document.add(new Paragraph("Productos:"));
        for (Producto p : catalogo.getProductos()) {
            document.add(new Paragraph("- " + p.getNombre() + " $" + p.getPrecio()));
        }

        document.close();
    }

}

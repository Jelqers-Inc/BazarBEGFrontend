package org.esfr.BazarBEG.controladores;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import jakarta.servlet.http.HttpServletResponse;
import org.esfr.BazarBEG.modelos.Catalogo;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.modelos.dtos.productos.Product;
import org.esfr.BazarBEG.servicios.interfaces.ICatalogoService;
import org.esfr.BazarBEG.servicios.interfaces.ICategoriaService;
import org.esfr.BazarBEG.servicios.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.time.format.DateTimeFormatter;
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
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size,@RequestParam("q") Optional<String> query) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5); // Ajusta el tamaño de página según tu necesidad
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<Catalogo> catalogos;
        if (query.isPresent() && !query.get().isBlank()) {
            catalogos = catalogoService.buscarPorNombrePaginado(query.get(), pageable);
            model.addAttribute("query", query.get());
        } else {
            catalogos = catalogoService.buscarTodosPaginados(pageable);
        }

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

        List<Product> prods =  productoService.obtenerTodos();
        for (Product producto : prods){
            producto.setImagen(productoService.obtenerImagen(producto.getId()));
        }

        return prods
                .stream()
                .filter(p -> p.getCategoriaId().equals(categoriaId))
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("nombre", p.getNombre());
                    map.put("precio", p.getPrecio());
                    map.put("imagen", p.getImagen());
                    return map;
                })
                .collect(Collectors.toList());
    }

    // -------------------- GUARDAR --------------------
//    @PostMapping("/save")
//    public String save(@ModelAttribute Catalogo catalogo,
//                       @RequestParam(value = "portadaFile", required = false) MultipartFile portadaFile,
//                       @RequestParam(value = "productosIds", required = false) List<Integer> productosIds,
//                       RedirectAttributes redirect) {
//
//        try {
//            if (catalogo.getCategoria() != null && catalogo.getCategoria().getId() != null) {
//                catalogo.setCategoria(categoriaService.buscarPorId(catalogo.getCategoria().getId()).orElse(null));
//            }
//
//            if (portadaFile != null && !portadaFile.isEmpty()) {
//                Path uploadPath = Paths.get(UPLOAD_DIR);
//                if (!Files.exists(uploadPath)) {
//                    Files.createDirectories(uploadPath);
//                }
//                String fileName = "portada_" + System.currentTimeMillis() + "_" + portadaFile.getOriginalFilename();
//                Files.copy(portadaFile.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
//                catalogo.setPortadaImagen("/uploads/catalogos/" + fileName);
//            }
//
//            if (productosIds != null && !productosIds.isEmpty()) {
//                catalogo.setProductos(productoService.buscarPorIds(productosIds));
//            } else {
//                catalogo.setProductos(new ArrayList<>());
//            }
//
//            catalogoService.crearOEditar(catalogo);
//
//            redirect.addFlashAttribute("msg", "Catálogo guardado correctamente");
//        } catch (Exception e) {
//            e.printStackTrace();
//            redirect.addFlashAttribute("error", "Error al guardar el catálogo: " + e.getMessage());
//            return "redirect:/catalogos/create";
//        }
//
//        return "redirect:/catalogos";
//    }



//     -------------------- DETALLES --------------------
//    @GetMapping("/details/{id}")
//    public String details(@PathVariable("id") int id, Model model) {
//        Catalogo catalogo = catalogoService.buscarPorId(id)
//                .orElseThrow(() -> new IllegalArgumentException("Catálogo no encontrado"));
//        model.addAttribute("catalogo", catalogo);
//        return "catalogo/details";
//    }

    // -------------------- EDITAR --------------------
//    @GetMapping("/edit/{id}")
//    public String edit(@PathVariable("id") int id, Model model) {
//        Catalogo catalogo = catalogoService.buscarPorId(id)
//                .orElseThrow(() -> new IllegalArgumentException("Catálogo no encontrado"));
//
//         Obtener la lista de productos de la categoría del catálogo.
//        List<Producto> productosDeCategoria = new ArrayList<>();
//        if (catalogo.getCategoria() != null) {
//            productosDeCategoria = productoService.findByCategoriaId(catalogo.getCategoria().getId());
//        }
//
//         Convertir la lista de objetos Producto a una lista de enteros (IDs).
//        List<Integer> productosIdsSeleccionados = catalogo.getProductos().stream()
//                .map(Producto::getId)
//                .collect(Collectors.toList());
//
//        model.addAttribute("catalogo", catalogo);
//        model.addAttribute("categorias", categoriaService.obtenerTodos());
//        model.addAttribute("productosDeCategoria", productosDeCategoria); // Se envía al modelo
//        model.addAttribute("productosIdsSeleccionados", productosIdsSeleccionados); // <--- ¡Esto es nuevo y crucial!
//
//        return "catalogo/edit";
//    }

//    @PostMapping("/update/{id}")
//    public String update(@PathVariable("id") int id,
//                         @Valid @ModelAttribute Catalogo catalogo,
//                         BindingResult result,
//                         @RequestParam(value = "portadaFile", required = false) MultipartFile portadaFile,
//                         @RequestParam(value = "productosIds", required = false) List<Integer> productosIds,
//                         RedirectAttributes redirect) throws IOException {
//
//        if (result.hasErrors()) {
//            return "catalogo/edit";
//        }
//
//        Catalogo catalogoExistente = catalogoService.buscarPorId(id)
//                .orElseThrow(() -> new IllegalArgumentException("Catálogo no encontrado con ID: " + id));
//
//        catalogoExistente.setNombre(catalogo.getNombre());
//        catalogoExistente.setDescripcion(catalogo.getDescripcion());
//        catalogoExistente.setFechaInicio(catalogo.getFechaInicio());
//        catalogoExistente.setFechaFin(catalogo.getFechaFin());
//        catalogoExistente.setCategoria(catalogo.getCategoria());
//
//        if (portadaFile != null && !portadaFile.isEmpty()) {
//            String portadaName = "portada_" + System.currentTimeMillis() + "_" + portadaFile.getOriginalFilename();
//            Path uploadPath = Paths.get(UPLOAD_DIR);
//
//            if (!Files.exists(uploadPath)) {
//                Files.createDirectories(uploadPath);
//            }
//
//            if (catalogoExistente.getPortadaImagen() != null && !catalogoExistente.getPortadaImagen().isEmpty()) {
//                Path oldImagePath = Paths.get(UPLOAD_DIR, Paths.get(catalogoExistente.getPortadaImagen()).getFileName().toString());
//                Files.deleteIfExists(oldImagePath);
//            }
//
//            Files.copy(portadaFile.getInputStream(), uploadPath.resolve(portadaName), StandardCopyOption.REPLACE_EXISTING);
//            catalogoExistente.setPortadaImagen("/uploads/catalogos/" + portadaName);
//        } else if (catalogo.getPortadaImagen() != null) {
//             Mantener la imagen si no se subió una nueva
//            catalogoExistente.setPortadaImagen(catalogo.getPortadaImagen());
//        }
//
//        catalogoExistente.setProductos(productoService.buscarPorIds(productosIds));
//        catalogoExistente.setPdfPath(generarPDF(catalogoExistente));
//        catalogoService.crearOEditar(catalogoExistente);
//
//        redirect.addFlashAttribute("success", "Catálogo actualizado correctamente");
//        return "redirect:/catalogos";
//    }


    // -------------------- ELIMINAR --------------------
//    @PostMapping("/delete/{id}")
//    public String delete(@PathVariable("id") int id, RedirectAttributes redirect) {
//        catalogoService.eliminarPorId(id);
//        redirect.addFlashAttribute("success", "Catálogo eliminado correctamente");
//        return "redirect:/catalogos";
//    }

    // -------------------- CONFIRMAR ELIMINAR --------------------
//    @GetMapping("/delete-confirm/{id}")
//    public String showDeleteConfirmation(@PathVariable("id") int id, Model model) {
//        Catalogo catalogo = catalogoService.buscarPorId(id)
//                .orElseThrow(() -> new IllegalArgumentException("Catálogo no encontrado"));
//        model.addAttribute("catalogo", catalogo);
//        return "catalogo/delete";
//    }

    // --------------- METODO PARA DISEÑO DEL PDF -----------
//    private void buildCatalogoPDF(Document document, Catalogo catalogo, byte[] portadaBytes) throws IOException {
//        document.setMargins(50, 50, 50, 50);
//
//         Encabezado del Catálogo
//        if (portadaBytes != null) {
//            com.itextpdf.layout.element.Image portada = new com.itextpdf.layout.element.Image(
//                    com.itextpdf.io.image.ImageDataFactory.create(portadaBytes)
//            ).setAutoScale(true).setMarginBottom(30);
//            document.add(portada);
//        }
//
//        document.add(new Paragraph("CATÁLOGO")
//                .setFontSize(28).setBold().setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
//                .setMarginBottom(10));
//        document.add(new Paragraph(catalogo.getNombre())
//                .setFontSize(20).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
//                .setMarginBottom(20));
//
//         Información del catálogo
//        document.add(new Paragraph("Productos de " + (catalogo.getCategoria() != null ? catalogo.getCategoria().getNombre() : "varias categorías"))
//                .setFontSize(16).setBold().setUnderline().setMarginBottom(15));
//        document.add(new Paragraph("Válido del " + catalogo.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " al " + catalogo.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
//                .setFontSize(12).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
//        document.add(new LineSeparator(new SolidLine()).setMarginTop(10).setMarginBottom(30));
//
//         Cuadrícula de Productos usando una tabla
//        com.itextpdf.layout.element.Table productTable = new com.itextpdf.layout.element.Table(3).useAllAvailableWidth();
//        productTable.setMarginBottom(20);
//
//        for (Producto p : catalogo.getProductos()) {
//            com.itextpdf.layout.element.Cell productCell = new com.itextpdf.layout.element.Cell();
//            productCell.setBorder(new SolidBorder(1));
//            productCell.setPadding(10);
//            productCell.setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
//
//             Imagen del producto
//            if (p.getImagen() != null && !p.getImagen().isEmpty()) {
//                File imgFile = new File("src/main/resources/static" + p.getImagen());
//                if (imgFile.exists()) {
//                    byte[] imgBytes = Files.readAllBytes(imgFile.toPath());
//                    com.itextpdf.layout.element.Image img = new com.itextpdf.layout.element.Image(
//                            com.itextpdf.io.image.ImageDataFactory.create(imgBytes)
//                    ).setAutoScale(true).setWidth(120).setMarginBottom(5);
//                    productCell.add(img);
//                }
//            }
//
//             Nombre del producto
//            productCell.add(new Paragraph(p.getNombre())
//                    .setBold().setFontSize(14).setMarginBottom(5));
//
//             Precio
//            productCell.add(new Paragraph("$" + p.getPrecio())
//                    .setBold().setFontSize(16).setFontColor(new DeviceRgb(128, 0, 128)));
//
//             Agregar la celda a la tabla
////            productTable.addCell(productCell);
//        }
//
//        document.add(productTable);
//        document.close();
//    }
//
    // -------------------- METODO PARA GENERAR PDF --------------------
//    private String generarPDF(Catalogo catalogo) throws IOException {
//        String pdfFileName = "catalogo_" + System.currentTimeMillis() + ".pdf";
//        String pdfPath = UPLOAD_DIR + pdfFileName;
//
//        PdfWriter writer = new PdfWriter(pdfPath);
//        PdfDocument pdfDoc = new PdfDocument(writer);
//        Document document = new Document(pdfDoc);
//
//        byte[] portadaBytes = null;
//        if (catalogo.getPortadaImagen() != null && !catalogo.getPortadaImagen().isEmpty()) {
//            File portadaFile = new File("src/main/resources/static" + catalogo.getPortadaImagen());
//            if (portadaFile.exists()) {
//                portadaBytes = Files.readAllBytes(portadaFile.toPath());
//            }
//        }
//
//        buildCatalogoPDF(document, catalogo, portadaBytes);
//        document.close();
//        return "/uploads/catalogos/" + pdfFileName;
//    }

    // -------------------- VISTA PREVIA PDF --------------------
//    @PostMapping("/preview-pdf")
//    public void previewPDF(@ModelAttribute Catalogo catalogo,
//                           @RequestParam(value = "portadaFile", required = false) MultipartFile portadaFile,
//                           @RequestParam(value = "productosIds", required = false) List<Integer> productosIds,
//                           HttpServletResponse response) throws IOException {
//
//        if (catalogo.getCategoria() != null && catalogo.getCategoria().getId() != null) {
//            catalogo.setCategoria(categoriaService.buscarPorId(catalogo.getCategoria().getId()).orElse(null));
//        }
//        catalogo.setProductos(productosIds != null && !productosIds.isEmpty() ?
//                productoService.buscarPorIds(productosIds) : new ArrayList<>());
//
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "inline; filename=catalogo_preview.pdf");
//
//        PdfWriter writer = new PdfWriter(response.getOutputStream());
//        PdfDocument pdfDoc = new PdfDocument(writer);
//        Document document = new Document(pdfDoc);
//
//        byte[] portadaBytes;
//        if (portadaFile != null && !portadaFile.isEmpty()) {
//            portadaBytes = portadaFile.getBytes();
//        } else if (catalogo.getPortadaImagen() != null && !catalogo.getPortadaImagen().isEmpty()) {
//            File file = new File("src/main/resources/static" + catalogo.getPortadaImagen());
//            portadaBytes = file.exists() ? Files.readAllBytes(file.toPath()) : null;
//        } else {
//            portadaBytes = null;
//        }
//
//        buildCatalogoPDF(document, catalogo, portadaBytes);
//        document.close();
//    }


    // -------------------- REPORTE PDF (descarga o vista previa) --------------------
//    @GetMapping("/reporte/{visualizacion}")
//    public void generarReportePDF(@PathVariable("visualizacion") String visualizacion,
//                                  @RequestParam("id") int catalogoId,
//                                  HttpServletResponse response) throws IOException {
//
//        Catalogo catalogo = catalogoService.buscarPorId(catalogoId)
//                .orElseThrow(() -> new IllegalArgumentException("Catálogo no encontrado"));
//
//        String contentDisposition = visualizacion.equalsIgnoreCase("attachment") ?
//                "attachment; filename=catalogo.pdf" :
//                "inline; filename=catalogo.pdf";
//
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", contentDisposition);
//
//        PdfWriter writer = new PdfWriter(response.getOutputStream());
//        PdfDocument pdfDoc = new PdfDocument(writer);
//        Document document = new Document(pdfDoc);
//
//        byte[] portadaBytes = null;
//        if (catalogo.getPortadaImagen() != null && !catalogo.getPortadaImagen().isEmpty()) {
//            File portadaFile = new File("src/main/resources/static" + catalogo.getPortadaImagen());
//            if (portadaFile.exists()) {
//                portadaBytes = Files.readAllBytes(portadaFile.toPath());
//            }
//        }
//
//        buildCatalogoPDF(document, catalogo, portadaBytes);
//        document.close();
//    }

//    @GetMapping("/imagen/{id}")
//    public ResponseEntity<Resource> obtenerPortada(@PathVariable("id") Integer id) {
//        Optional<Catalogo> catalogoOpt = catalogoService.buscarPorId(id);
//        if (catalogoOpt.isPresent() && catalogoOpt.get().getPortadaImagen() != null) {
//            try {
//                 Obtiene la ruta de la imagen desde la base de datos
//                String rutaImagen = catalogoOpt.get().getPortadaImagen();
//                 Extrae el nombre del archivo de la ruta
//                String nombreArchivo = Paths.get(rutaImagen).getFileName().toString();
//                 Construye la ruta completa y segura al archivo en el sistema de archivos
//                Path filePath = Paths.get(UPLOAD_DIR).resolve(nombreArchivo).normalize();
//                Resource resource = new UrlResource(filePath.toUri());
//
//                if (resource.exists() && resource.isReadable()) {
//                     Detecta el tipo de contenido del archivo para servirlo correctamente
//                    String contentType = Files.probeContentType(filePath);
//                    if (contentType == null) {
//                        contentType = "application/octet-stream";
//                    }
//                    return ResponseEntity.ok()
//                            .contentType(MediaType.parseMediaType(contentType))
//                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
//                            .body(resource);
//                }
//            } catch (IOException e) {
//                 En caso de error, muestra un mensaje en la consola y devuelve un 404
//                e.printStackTrace();
//            }
//        }
//         Devuelve 404 si el catálogo o la imagen no existen
//        return ResponseEntity.notFound().build();
//    }



}

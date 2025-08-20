package org.esfr.BazarBEG.controladores;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
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
    public String createCatalogo(Model model) {
        Catalogo catalogo = new Catalogo();
        model.addAttribute("catalogo", catalogo);
        model.addAttribute("categorias", categoriaService.obtenerTodos());
        return "catalogo/create";
    }

    // -------------------- AJAX: productos por categoría --------------------
    @GetMapping("/productosPorCategoria/{categoriaId}")
    @ResponseBody
    public List<Map<String, Object>> productosPorCategoria(@PathVariable Integer categoriaId) {
        List<Producto> productos = productoService.findByCategoriaId(categoriaId);

        return productos.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("nombre", p.getNombre());
            map.put("precio", p.getPrecio());
            return map;
        }).collect(Collectors.toList());
    }

    // -------------------- GUARDAR --------------------
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Catalogo catalogo,
                       BindingResult result,
                       @RequestParam(value = "portadaFile", required = false) MultipartFile portadaFile,
                       @RequestParam(value = "productosIds", required = false) List<Integer> productosIds,
                       RedirectAttributes redirect) throws IOException {

        if (result.hasErrors()) {
            return "catalogo/create";
        }

        // -------------------- Guardar portada si existe --------------------
        if (portadaFile != null && !portadaFile.isEmpty()) {
            String fileName = "portada_" + System.currentTimeMillis() + "_" + portadaFile.getOriginalFilename();
            String portadaPath = UPLOAD_DIR + fileName;
            portadaFile.transferTo(new File(portadaPath));
            catalogo.setPortadaImagen("/uploads/catalogos/" + fileName);
        }

        // -------------------- Asociar productos si se seleccionaron --------------------
        if (productosIds != null && !productosIds.isEmpty()) {
            List<Producto> productosSeleccionados = productoService.buscarPorIds(productosIds);
            catalogo.setProductos(productosSeleccionados);
        } else {
            catalogo.setProductos(new ArrayList<>());
        }

        // -------------------- Generar PDF --------------------
        String pdfPath = generarPDF(catalogo);
        catalogo.setPdfPath(pdfPath);

        // -------------------- Guardar catálogo --------------------
        catalogoService.crearOEditar(catalogo);

        redirect.addFlashAttribute("msg", "Catálogo guardado correctamente");
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

        List<Producto> productos;
        if (catalogo.getCategoria() != null) {
            productos = productoService.findByCategoriaId(catalogo.getCategoria().getId());
        } else {
            productos = new ArrayList<>();
        }
        model.addAttribute("productos", productos);

        return "catalogo/edit";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") int id,
                         @Valid @ModelAttribute Catalogo catalogo,
                         BindingResult result,
                         @RequestParam("portadaFile") MultipartFile portadaFile,
                         @RequestParam("productosIds") List<Integer> productosIds,
                         RedirectAttributes redirect) throws IOException {

        if (result.hasErrors()) {
            return "catalogo/edit";
        }

        Catalogo catalogoExistente = catalogoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Catálogo no encontrado"));

        catalogoExistente.setNombre(catalogo.getNombre());
        catalogoExistente.setDescripcion(catalogo.getDescripcion());
        catalogoExistente.setFechaInicio(catalogo.getFechaInicio());
        catalogoExistente.setFechaFin(catalogo.getFechaFin());
        catalogoExistente.setCategoria(catalogo.getCategoria());

        if (!portadaFile.isEmpty()) {
            String portadaPath = UPLOAD_DIR + "portada_" + System.currentTimeMillis() + "_" + portadaFile.getOriginalFilename();
            portadaFile.transferTo(new File(portadaPath));
            catalogoExistente.setPortadaImagen("/uploads/catalogos/" + "portada_" + System.currentTimeMillis() + "_" + portadaFile.getOriginalFilename());
        }

        List<Producto> productosSeleccionados = productoService.buscarPorIds(productosIds);
        catalogoExistente.setProductos(productosSeleccionados);

        // Regenerar PDF
        String pdfPath = generarPDF(catalogoExistente);
        catalogoExistente.setPdfPath(pdfPath);

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

        return "/uploads/catalogos/" + pdfFileName; // ruta relativa para vista previa
    }
}

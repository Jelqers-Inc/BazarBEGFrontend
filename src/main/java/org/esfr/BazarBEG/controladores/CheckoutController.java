package org.esfr.BazarBEG.controladores;

import com.stripe.exception.StripeException;
import org.esfr.BazarBEG.modelos.Carrito;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.modelos.dtos.usuarios.User;
import org.esfr.BazarBEG.servicios.implementaciones.StripeService;
import org.esfr.BazarBEG.servicios.interfaces.IProductoService;
import org.esfr.BazarBEG.servicios.interfaces.IPedidoService;
import org.esfr.BazarBEG.servicios.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private Carrito carrito;

    @Autowired
    private IProductoService productoService;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private IPedidoService pedidoService;

    @Autowired
    private IUsuarioService usuarioService;

    @Value("${stripe.secret.key}")
    private String publishableKey;

    @GetMapping
    public String verCheckout(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        if (carrito.getItems().isEmpty()) {
            return "redirect:/carrito";
        }

        Map<Long, Integer> itemsDelCarrito = carrito.getItems();
        Map<Producto, Integer> productosParaVista = new HashMap<>();
        double total = 0.0;

        for (Map.Entry<Long, Integer> entry : itemsDelCarrito.entrySet()) {
            Long productoId = entry.getKey();
            Integer cantidad = entry.getValue();

            Optional<Producto> productoOpt = productoService.obtenerProductoActivoPorId(productoId);

            if (productoOpt.isPresent()) {
                Producto producto = productoOpt.get();
                productosParaVista.put(producto, cantidad);
                total += producto.getPrecio() * cantidad;
            }
        }

        try {
            String clientSecret = stripeService.createPaymentIntent(total, "usd"); // Asegúrate de que la divisa coincida con tu cuenta de Stripe
            model.addAttribute("productosEnCarrito", productosParaVista);
            model.addAttribute("totalCarrito", total);
            model.addAttribute("clientSecret", clientSecret);
            model.addAttribute("publishableKey", publishableKey);

            return "cliente/checkout";
        } catch (StripeException e) {
            model.addAttribute("error", "Error al procesar el pago: " + e.getMessage());
            return "carrito/carrito";
        }
    }

    @GetMapping("/confirmacion-pago")
    public String confirmacionPago(Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        // 1. Obtener el usuario actual por su email
        String email = principal.getName();
        User usuario = usuarioService.obtenerPorEmail(email);

        // 2. Obtener los productos y calcular el total desde el carrito
        Map<Producto, Integer> productosPedido = new HashMap<>();
        double total = 0.0;
        for (Map.Entry<Long, Integer> entry : carrito.getItems().entrySet()) {
            Long productoId = entry.getKey();
            Integer cantidad = entry.getValue();
            Optional<Producto> productoOpt = productoService.obtenerProductoActivoPorId(productoId);
            if (productoOpt.isPresent()) {
                Producto producto = productoOpt.get();
                productosPedido.put(producto, cantidad);
                total += producto.getPrecio() * cantidad;
            }
        }

        // 3. Crear el pedido en la base de datos
       // pedidoService.crearPedido(usuario, productosPedido, total);

        // 4. Vaciar el carrito del usuario para una nueva compra
        carrito.getItems().clear();

        // 5. Redirigir a la vista de confirmación
        return "cliente/confirmacion-pago";
    }
}
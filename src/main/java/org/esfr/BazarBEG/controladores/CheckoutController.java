package org.esfr.BazarBEG.controladores;

import com.stripe.exception.StripeException;
import org.esfr.BazarBEG.modelos.Carrito;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.modelos.dtos.carrito.CarritoItemDTO;
import org.esfr.BazarBEG.modelos.dtos.checkout.CheckoutDto;
import org.esfr.BazarBEG.modelos.dtos.checkout.PayResponse;
import org.esfr.BazarBEG.modelos.dtos.checkout.ProductCheckout;
import org.esfr.BazarBEG.modelos.dtos.productos.Product;
import org.esfr.BazarBEG.modelos.dtos.usuarios.User;
import org.esfr.BazarBEG.servicios.implementaciones.StripeService;
import org.esfr.BazarBEG.servicios.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private ICarritoService carritoService;

    @Autowired
    private IProductoService productoService;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private ICheckoutService checkoutService;

    @Autowired
    private IUsuarioService usuarioService;

    @Value("${stripe.publisheable.key}")
    private String publishableKey;

    @GetMapping
    public String verCheckout(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        List<CarritoItemDTO> carrito = carritoService.obtenerTodo();

        if (carrito.isEmpty()) {
            return "redirect:/carrito";
        }

        Map<Integer, Integer> itemsDelCarrito = new HashMap<>();
        for(CarritoItemDTO item : carrito){
            itemsDelCarrito.put(item.getId_producto(), item.getCantidad());
        }

        Map<Product, Integer> productosParaVista = new HashMap<>();
        double total = 0.0;

        for (Map.Entry<Integer, Integer> entry : itemsDelCarrito.entrySet()) {
            Integer productoId = entry.getKey();
            Integer cantidad = entry.getValue();

            Product product = productoService.buscarPorId(productoId);

            if (product.getId() != null) {
                productosParaVista.put(product, cantidad);
                total += product.getPrecio() * cantidad;
            }
        }

        List<ProductCheckout> productos = new ArrayList<ProductCheckout>();
        for (CarritoItemDTO item : carrito){
            ProductCheckout checkout = new ProductCheckout();
            checkout.setId_producto(item.getId_producto());
            checkout.setCantidad(item.getCantidad());
            checkout.setPrecio(item.getPrecio());
            productos.add(checkout);
        }

        CheckoutDto order = new CheckoutDto();
        order.setProductos(productos);

        PayResponse response = checkoutService.crearPedido(order);
        String[] parts = response.getClientSecret().split("_secret");

        model.addAttribute("datosEnvioPago", response);
        model.addAttribute("secret", parts[0]);

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

        return "cliente/confirmacion-pago";
    }
}
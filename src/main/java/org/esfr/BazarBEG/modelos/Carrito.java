package org.esfr.BazarBEG.modelos;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Carrito implements Serializable {

    private Map<Long, Integer> items = new HashMap<>();

    public void agregarProducto(Long productoId, int cantidad) {
        this.items.merge(productoId, cantidad, Integer::sum);
    }

    public void eliminarProducto(Long productoId) {
        this.items.remove(productoId);
    }

    public void actualizarCantidad(Long productoId, int nuevaCantidad) {
        if (nuevaCantidad > 0) {
            this.items.put(productoId, nuevaCantidad);
        } else {
            this.items.remove(productoId);
        }
    }

    public Map<Long, Integer> getItems() {
        return items;
    }
}
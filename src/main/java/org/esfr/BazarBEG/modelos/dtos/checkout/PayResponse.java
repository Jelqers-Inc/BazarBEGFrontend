package org.esfr.BazarBEG.modelos.dtos.checkout;

public class PayResponse {
    private String clientSecret;
    private Integer id_pedido;
    private String urlFactura;

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Integer getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(Integer id_pedido) {
        this.id_pedido = id_pedido;
    }

    public String getUrlFactura() {
        return urlFactura;
    }

    public void setUrlFactura(String urlFactura) {
        this.urlFactura = urlFactura;
    }
}

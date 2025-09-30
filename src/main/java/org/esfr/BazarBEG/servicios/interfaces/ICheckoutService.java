package org.esfr.BazarBEG.servicios.interfaces;

import org.esfr.BazarBEG.modelos.dtos.checkout.CheckoutDto;
import org.esfr.BazarBEG.modelos.dtos.checkout.PayResponse;

public interface ICheckoutService {
    PayResponse crearPedido(CheckoutDto checkoutDto);
    void realizarPago(String paymentIntentId);
}

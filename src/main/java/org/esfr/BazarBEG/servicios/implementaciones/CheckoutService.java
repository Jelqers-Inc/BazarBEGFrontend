package org.esfr.BazarBEG.servicios.implementaciones;

import org.esfr.BazarBEG.modelos.dtos.checkout.CheckoutDto;
import org.esfr.BazarBEG.modelos.dtos.checkout.PayResponse;
import org.esfr.BazarBEG.repositorios.CheckoutRepository;
import org.esfr.BazarBEG.servicios.interfaces.ICheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService implements ICheckoutService {

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Override
    public PayResponse crearPedido(CheckoutDto checkoutDto) {
        return checkoutRepository.crearPedido(checkoutDto);
    }

    @Override
    public void realizarPago(String paymentIntentId) {
        checkoutRepository.realizarPago(paymentIntentId);
    }
}

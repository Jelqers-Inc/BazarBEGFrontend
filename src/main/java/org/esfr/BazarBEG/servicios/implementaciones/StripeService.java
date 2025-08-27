package org.esfr.BazarBEG.servicios.implementaciones;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")
    private String secretKey;

    public String createPaymentIntent(double amount, String currency) throws StripeException {
        Stripe.apiKey = secretKey;

        // Convertir el monto a céntimos, ya que Stripe maneja los montos en la unidad más pequeña de la divisa
        long centsAmount = (long) (amount * 100);

        PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                .setAmount(centsAmount)
                .setCurrency(currency)
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(createParams);
        return paymentIntent.getClientSecret();
    }
}
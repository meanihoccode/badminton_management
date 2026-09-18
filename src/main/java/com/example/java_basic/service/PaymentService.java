package com.example.java_basic.service;

import vn.payos.model.webhooks.Webhook;

public interface PaymentService {
    String createPaymentLink(Long userId, int amount, String returnUrl);
    void processWebhook(Webhook webhookBody);
}
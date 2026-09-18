package com.example.java_basic.service.impl;

import com.example.java_basic.entity.Transaction;
import com.example.java_basic.enums.TransactionType;
import com.example.java_basic.entity.User;
import com.example.java_basic.repository.TransactionRepository;
import com.example.java_basic.repository.UserRepository;
import com.example.java_basic.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final PayOS payOS;

    @Override
    public String createPaymentLink(Long userId, int amount, String returnUrl) {
        try {
            // Tao ma don hang duy nhat
            long orderCode = Long.parseLong(userId + "" + (System.currentTimeMillis() % 1000000000));
            
            PaymentLinkItem item = PaymentLinkItem.builder()
                .name("Nap tien vao tai khoan " + userId)
                .quantity(1)
                .price((long) amount)
                .build();

            CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount((long) amount)
                .description("NAP " + userId)
                .returnUrl(returnUrl)
                .cancelUrl(returnUrl)
                .items(List.of(item))
                .build();

            CreatePaymentLinkResponse data = payOS.paymentRequests().create(request);
            return data.getCheckoutUrl();
        } catch (Exception e) {
            log.error("Loi tao payment link", e);
            throw new RuntimeException("Khong the tao link thanh toan");
        }
    }

    @Override
    @Transactional
    public void processWebhook(Webhook webhookBody) {
        try {
            // Giai ma va xac thuc chu ky webhook
            WebhookData data = payOS.webhooks().verify(webhookBody);
            
            log.info("Nhan webhook thanh toan tu PayOS: {}", data);
            
            // Neu khong phai trang thai thanh cong thi bo qua
            if (!"00".equals(data.getCode())) {
                return;
            }

            // Kiem tra chong trung lap
            String externalRef = String.valueOf(data.getOrderCode());
            if (transactionRepository.existsByExternalRef(externalRef)) {
                log.info("Giao dich {} da duoc xu ly truoc do.", externalRef);
                return;
            }

            // Phan tich description de tim userId
            String description = data.getDescription();
            Long userId = null;
            
            if (description != null) {
                // Thu trich xuat userId tu chuoi "NAP "
                String[] parts = description.split("NAP\\s+");
                if (parts.length > 1) {
                    try {
                        String idStr = parts[1].trim().split("\\s+")[0];
                        userId = Long.parseLong(idStr);
                    } catch (Exception e) {
                        log.warn("Khong parse duoc userId tu description: {}", description);
                    }
                }
            }

            if (userId != null) {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    // Cong tien
                    user.setBalance(user.getBalance().add(java.math.BigDecimal.valueOf(data.getAmount())));
                    userRepository.save(user);

                    // Luu lich su
                    Transaction transaction = new Transaction();
                    transaction.setUser(user);
                    transaction.setAmount(java.math.BigDecimal.valueOf(data.getAmount()));
                    transaction.setTransactionType(TransactionType.DEPOSIT);
                    transaction.setDescription("Nạp tiền từ PayOS (OrderCode: " + externalRef + ")");
                    transaction.setExternalRef(externalRef);
                    transactionRepository.save(transaction);

                    log.info("Da cong {} VND cho user {} thanh cong tu webhook PayOS.", data.getAmount(), user.getUsername());
                } else {
                    log.warn("Khong tim thay user voi id {}", userId);
                }
            }
        } catch (Exception e) {
            log.error("Loi xu ly webhook PayOS", e);
            throw new RuntimeException("Loi xu ly webhook");
        }
    }
}
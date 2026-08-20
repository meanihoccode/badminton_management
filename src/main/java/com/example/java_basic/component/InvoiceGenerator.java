package com.example.java_basic.component;

import com.example.java_basic.repository.TransactionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Minh họa StringBuilder và Synchronization.
 */
@Component
public class InvoiceGenerator {

    // Bộ đếm dùng để sinh mã hóa đơn, cần được bảo vệ trong môi trường đa luồng
    private int counter = 1000;

    private final TransactionRepository transactionRepository;

    public InvoiceGenerator(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @PostConstruct
    public void initCounter() {
        long count = transactionRepository.count();
        counter += (int) count;
    }

    /**
     * Minh họa Synchronization: Đảm bảo chỉ 1 thread được gọi hàm này tại một thời điểm.
     * Tránh lỗi Duplicate Invoice ID nếu có 2 luồng đồng thời gọi.
     */
    public synchronized String generateNextInvoiceId() {
        counter++;
        return "INV-" + counter;
    }

    /**
     * Minh họa StringBuilder: Nối chuỗi để tạo hóa đơn.
     * Dùng StringBuilder tốt hơn toán tử '+' vì nó không tạo ra các object String thừa trong String Pool.
     */
    public String generateReceipt(String username, BigDecimal amount, String note) {
        String invoiceId = generateNextInvoiceId();
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        StringBuilder sb = new StringBuilder();
        sb.append("====================================\n");
        sb.append("       HÓA ĐƠN THANH TOÁN (RECEIPT)\n");
        sb.append("====================================\n");
        sb.append("Mã hóa đơn : ").append(invoiceId).append("\n");
        sb.append("Ngày giờ   : ").append(dateStr).append("\n");
        sb.append("Khách hàng : ").append(username).append("\n");
        sb.append("Số tiền    : ").append(amount).append(" VND\n");
        if (note != null && !note.isEmpty()) {
            sb.append("Ghi chú    : ").append(note).append("\n");
        }
        sb.append("====================================\n");
        sb.append("Cảm ơn bạn đã đóng quỹ sân!\n");

        return sb.toString();
    }
}

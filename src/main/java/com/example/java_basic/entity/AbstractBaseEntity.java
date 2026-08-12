package com.example.java_basic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Lớp trừu tượng (Abstract Class) đại diện cho các trường chung của mọi Entity.
 * @MappedSuperclass báo cho Hibernate biết không tạo bảng cho class này,
 * mà mang các trường này xuống các class con để tạo cột tương ứng.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class AbstractBaseEntity {

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

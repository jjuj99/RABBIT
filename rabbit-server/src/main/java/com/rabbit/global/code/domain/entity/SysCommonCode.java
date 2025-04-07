package com.rabbit.global.code.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "sys_common_codes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"code_type", "code"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysCommonCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id", nullable = false)
    private Long codeId;

    @Column(name = "code_type", nullable = false, length = 50)
    private String codeType;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "code_name", nullable = false, length = 100)
    private String codeName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "active_flag", nullable = false)
    private boolean activeFlag;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now(ZoneId.systemDefault());
        this.updatedAt = ZonedDateTime.now(ZoneId.systemDefault());
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = ZonedDateTime.now(ZoneId.systemDefault());
    }
}
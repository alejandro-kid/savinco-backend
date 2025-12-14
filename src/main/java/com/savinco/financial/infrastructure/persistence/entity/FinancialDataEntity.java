package com.savinco.financial.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_data", uniqueConstraints = {
    @UniqueConstraint(columnNames = "country_code")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "capital_saved", nullable = false, precision = 19, scale = 2)
    private BigDecimal capitalSaved;

    @Column(name = "capital_loaned", nullable = false, precision = 19, scale = 2)
    private BigDecimal capitalLoaned;

    @Column(name = "profits_generated", nullable = false, precision = 19, scale = 2)
    private BigDecimal profitsGenerated;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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

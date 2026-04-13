package com.familybudget_BE.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TransactionRequestDTO {

	@NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private String type;

    @NotNull
    private Long categoryId;

    @NotNull
    private LocalDate date;

    @NotBlank
    private String description;

    @NotNull
    private Long familyId;
}

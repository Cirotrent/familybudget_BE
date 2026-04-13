package com.familybudget_BE.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TransactionResponseDTO {

	 	private Long id;
	    private BigDecimal amount;
	    private String type;
	    private LocalDate date;
	    private String description;
	    private String categoryName;
	    private String username;
}

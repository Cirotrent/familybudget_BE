package com.familybudget_BE.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MonthlyReportDTO {
		private String month;
	    private BigDecimal totalIncome;
	    private BigDecimal totalExpense;
	    private BigDecimal balance;
}

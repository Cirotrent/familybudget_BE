package com.familybudget_BE.dto;

import com.familybudget_BE.entity.Category;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CategoryRequestDTO {

	 private String name;
	 private Category.Type type;
	 private Long familyId;
}

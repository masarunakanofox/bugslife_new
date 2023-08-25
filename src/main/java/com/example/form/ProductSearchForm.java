package com.example.form;

import java.util.List;
import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
public class ProductSearchForm {
	private String name;
	private String code;
	private List<Long> categories;
	private List<String> categoryNames; // 追加するフィールド
	private Integer weight1;
	private Integer weight2;
	private Integer height1;
	private Integer height2;
	private Double price1;
	private Double price2;

	public ProductSearchForm(String name, String code, List<Long> categories, List<String> categoryNames,
			Integer weight1, Integer weight2, Integer height1, Integer height2, Double price1, Double price2) {
		this.setName(name);
		this.setCode(code);
		this.setCategories(categories);
		this.setCategoryNames(categoryNames); // 新しく追加されたフィールド
		this.setWeight1(weight1);
		this.setWeight2(weight2);
		this.setHeight1(height1);
		this.setHeight2(height2);
		this.setPrice1(price1);
		this.setPrice2(price2);
	}
}

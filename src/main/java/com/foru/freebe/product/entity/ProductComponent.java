package com.foru.freebe.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductComponent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_component_id")
	private Long id;

	@NotNull
	private String title;

	@NotNull
	private String content;

	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	private ProductComponent(String title, String content, String description, Product product) {
		this.title = title;
		this.content = content;
		this.description = description;
		this.product = product;
	}

	public static ProductComponent createProductComponentWithoutDesc(String title, String content, Product product) {
		return new ProductComponent(title, content, null, product);
	}

	public static ProductComponent createProductComponent(String title, String content, String description,
		Product product) {
		return new ProductComponent(title, content, description, product);
	}
}

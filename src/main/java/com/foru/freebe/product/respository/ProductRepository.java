package com.foru.freebe.product.respository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<List<Product>> findByMember(Member member);

    Boolean existsByTitleAndMember(String title, Member member);

    Product findByTitleAndMember(String title, Member member);

    Optional<Product> findByIdAndMember(Long productId, Member member);
}

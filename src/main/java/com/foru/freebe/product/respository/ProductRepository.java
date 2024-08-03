package com.foru.freebe.product.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByMember(Member member);
}

package com.foru.freebe.product.respository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.product.entity.ActiveStatus;
import com.foru.freebe.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByMember(Member member);

    List<Product> findByMemberAndActiveStatus(Member member, ActiveStatus activeStatus);

    Boolean existsByMemberAndTitle(Member member, String title);

    Optional<Product> findByIdAndMember(Long productId, Member member);

    Optional<Product> findByTitle(String title);

    Boolean existsByTitle(String title);
}

package com.nimuairy.auth.repository;

import com.nimuairy.auth.domain.Authority;
import com.nimuairy.auth.domain.EAuthority;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Optional<Authority> findByName(EAuthority name);
}
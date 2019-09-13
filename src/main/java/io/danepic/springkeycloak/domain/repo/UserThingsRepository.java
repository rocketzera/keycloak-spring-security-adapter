package io.danepic.springkeycloak.domain.repo;

import io.danepic.springkeycloak.domain.entity.UserThings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserThingsRepository extends JpaRepository<UserThings, Long> {
}
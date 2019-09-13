package io.danepic.springkeycloak.domain.repo;

import io.danepic.springkeycloak.domain.entity.AdminThings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminThingsRepository extends JpaRepository<AdminThings, Long> {
}

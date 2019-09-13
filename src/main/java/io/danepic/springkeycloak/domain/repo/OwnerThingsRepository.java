package io.danepic.springkeycloak.domain.repo;

import io.danepic.springkeycloak.domain.entity.AdminThings;
import io.danepic.springkeycloak.domain.entity.OwnerThings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerThingsRepository extends JpaRepository<OwnerThings, Long> {
}

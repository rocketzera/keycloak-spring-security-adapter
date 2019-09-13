package io.danepic.springkeycloak.domain.repo;

import io.danepic.springkeycloak.domain.entity.AdminThings;
import io.danepic.springkeycloak.domain.entity.GuestThings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestThingsRepository extends JpaRepository<GuestThings, Long> {
}

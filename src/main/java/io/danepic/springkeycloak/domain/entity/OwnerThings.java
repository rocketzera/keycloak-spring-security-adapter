package io.danepic.springkeycloak.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class OwnerThings {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private String document;

    private BigDecimal total;
}

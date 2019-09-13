package io.danepic.springkeycloak.resource.hateoas;

import io.danepic.springkeycloak.domain.dto.OwnerThingsDTO;
import io.danepic.springkeycloak.domain.entity.OwnerThings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OwnerThingsHateoas {

    @Autowired
    private EntityLinks entityLinks;

    private static final String UPDATE = "update";
    private static final String DELETE = "delete";

    public OwnerThingsDTO toResource(OwnerThings owner) {
        OwnerThingsDTO resource = OwnerThingsDTO
                .builder()
                .seq(owner.getId())
                .document(owner.getDocument())
                .name(owner.getName())
                .total(owner.getTotal())
                .build();

        final Link selfLink = entityLinks.linkToSingleResource(Long.class, owner.getId());
        resource.add(selfLink.withSelfRel());
        resource.add(selfLink.withRel(UPDATE));
        resource.add(selfLink.withRel(DELETE));
        return resource;
    }

    public List<OwnerThingsDTO> toResource(List<OwnerThings> domainObjects) {
        return domainObjects.stream()
                .map(this::toResource)
                .collect(Collectors.toList());
    }

    public OwnerThings toEntity(OwnerThingsDTO owner) {
        return OwnerThings
                .builder()
                .id(owner.getSeq())
                .document(owner.getDocument())
                .name(owner.getName())
                .total(owner.getTotal())
                .build();
    }

    public List<OwnerThings> toEntity(List<OwnerThingsDTO> domainObjects) {
        return domainObjects.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public OwnerThings merge(OwnerThings oldEntity, OwnerThings newEntity) {
        oldEntity.setId(newEntity.getId());
        oldEntity.setDocument(newEntity.getDocument());
        oldEntity.setName(newEntity.getName());
        oldEntity.setTotal(newEntity.getTotal());
        return oldEntity;
    }
}

package io.danepic.springkeycloak.resource.hateoas;

import io.danepic.springkeycloak.domain.dto.AdminThingsDTO;
import io.danepic.springkeycloak.domain.entity.AdminThings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminThingsHateoas {

    @Autowired
    private EntityLinks entityLinks;

    private static final String UPDATE = "update";
    private static final String DELETE = "delete";

    public AdminThingsDTO toResource(AdminThings admin) {
        AdminThingsDTO resource = AdminThingsDTO
                .builder()
                .seq(admin.getId())
                .document(admin.getDocument())
                .name(admin.getName())
                .total(admin.getTotal())
                .build();

        final Link selfLink = entityLinks.linkToSingleResource(Long.class, admin.getId());
        resource.add(selfLink.withSelfRel());
        resource.add(selfLink.withRel(UPDATE));
        resource.add(selfLink.withRel(DELETE));
        return resource;
    }

    public List<AdminThingsDTO> toResource(List<AdminThings> domainObjects) {
        return domainObjects.stream()
                .map(this::toResource)
                .collect(Collectors.toList());
    }

    public AdminThings toEntity(AdminThingsDTO admin) {
        return AdminThings
                .builder()
                .id(admin.getSeq())
                .document(admin.getDocument())
                .name(admin.getName())
                .total(admin.getTotal())
                .build();
    }

    public List<AdminThings> toEntity(List<AdminThingsDTO> domainObjects) {
        return domainObjects.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public AdminThings merge(AdminThings oldEntity, AdminThings newEntity){
        oldEntity.setId(newEntity.getId());
        oldEntity.setDocument(newEntity.getDocument());
        oldEntity.setName(newEntity.getName());
        oldEntity.setTotal(newEntity.getTotal());
        return oldEntity;
    }
}

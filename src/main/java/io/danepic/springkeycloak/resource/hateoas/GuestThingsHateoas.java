package io.danepic.springkeycloak.resource.hateoas;

import io.danepic.springkeycloak.domain.dto.GuestThingsDTO;
import io.danepic.springkeycloak.domain.entity.GuestThings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GuestThingsHateoas {

    @Autowired
    private EntityLinks entityLinks;

    private static final String UPDATE = "update";
    private static final String DELETE = "delete";

    public GuestThingsDTO toResource(GuestThings guest) {
        GuestThingsDTO resource = GuestThingsDTO
                .builder()
                .seq(guest.getId())
                .name(guest.getName())
                .build();

        final Link selfLink = entityLinks.linkToSingleResource(Long.class, guest.getId());
        resource.add(selfLink.withSelfRel());
        resource.add(selfLink.withRel(UPDATE));
        resource.add(selfLink.withRel(DELETE));
        return resource;
    }

    public List<GuestThingsDTO> toResource(List<GuestThings> domainObjects) {
        return domainObjects.stream()
                .map(this::toResource)
                .collect(Collectors.toList());
    }

    public GuestThings toEntity(GuestThingsDTO guest) {
        return GuestThings
                .builder()
                .id(guest.getSeq())
                .name(guest.getName())
                .build();
    }

    public List<GuestThings> toEntity(List<GuestThingsDTO> domainObjects) {
        return domainObjects.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public GuestThings merge(GuestThings oldEntity, GuestThings newEntity) {
        oldEntity.setId(newEntity.getId());
        oldEntity.setName(newEntity.getName());
        return oldEntity;
    }
}

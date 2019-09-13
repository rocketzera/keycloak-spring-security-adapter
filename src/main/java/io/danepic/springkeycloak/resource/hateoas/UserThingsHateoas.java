package io.danepic.springkeycloak.resource.hateoas;

import io.danepic.springkeycloak.domain.dto.UserThingsDTO;
import io.danepic.springkeycloak.domain.entity.UserThings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserThingsHateoas {

    @Autowired
    private EntityLinks entityLinks;

    private static final String UPDATE = "update";
    private static final String DELETE = "delete";

    public UserThingsDTO toResource(UserThings user) {
        UserThingsDTO resource = UserThingsDTO
                .builder()
                .seq(user.getId())
                .document(user.getDocument())
                .name(user.getName())
                .total(user.getTotal())
                .build();

        final Link selfLink = entityLinks.linkToSingleResource(Long.class, user.getId());
        resource.add(selfLink.withSelfRel());
        resource.add(selfLink.withRel(UPDATE));
        resource.add(selfLink.withRel(DELETE));
        return resource;
    }

    public List<UserThingsDTO> toResource(List<UserThings> domainObjects) {
        return domainObjects.stream()
                .map(this::toResource)
                .collect(Collectors.toList());
    }

    public UserThings toEntity(UserThingsDTO user) {
        return UserThings
                .builder()
                .id(user.getSeq())
                .document(user.getDocument())
                .name(user.getName())
                .total(user.getTotal())
                .build();
    }

    public List<UserThings> toEntity(List<UserThingsDTO> domainObjects) {
        return domainObjects.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public UserThings merge(UserThings oldEntity, UserThings newEntity){
        oldEntity.setId(newEntity.getId());
        oldEntity.setDocument(newEntity.getDocument());
        oldEntity.setName(newEntity.getName());
        oldEntity.setTotal(newEntity.getTotal());
        return oldEntity;
    }
}

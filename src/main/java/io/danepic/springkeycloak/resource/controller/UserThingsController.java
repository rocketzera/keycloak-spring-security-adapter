package io.danepic.springkeycloak.resource.controller;

import io.danepic.springkeycloak.domain.dto.UserThingsDTO;
import io.danepic.springkeycloak.domain.entity.UserThings;
import io.danepic.springkeycloak.domain.repo.UserThingsRepository;
import io.danepic.springkeycloak.resource.hateoas.UserThingsHateoas;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
@ExposesResourceFor(Long.class)
@ApiOperation(value = "User API", produces = "JSON", consumes = "JSON")
@Api("user")
public class UserThingsController {

    @Autowired
    private UserThingsRepository repository;

    @Autowired
    private UserThingsHateoas mapper;

    @PostMapping
    @ApiOperation(value = "Create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public UserThingsDTO create(@RequestBody UserThingsDTO dto) {
        UserThings UserThings = mapper.toEntity(dto);
        UserThings save = repository.save(UserThings);
        return mapper.toResource(save);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('USER')")
    @ApiOperation(value = "Read")
    public UserThingsDTO read(@PathVariable Long id) {
        UserThings UserThings = repository.findById(id).orElseThrow(() -> new RuntimeException("Not found."));
        return mapper.toResource(UserThings);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('USER')")
    @ApiOperation(value = "Read")
    public List<UserThingsDTO> read() {
        return mapper.toResource(repository.findAll());
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @ApiOperation(value = "Update")
    public UserThingsDTO update(@PathVariable Long id, @RequestBody UserThingsDTO dto) {
        UserThings UserThings = repository.findById(id).orElseThrow(() -> new RuntimeException("Not found."));
        UserThings merge = mapper.merge(UserThings, mapper.toEntity(dto));
        return mapper.toResource(repository.save(merge));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @ApiOperation(value = "Delete")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}

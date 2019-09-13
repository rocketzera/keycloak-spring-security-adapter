package io.danepic.springkeycloak.resource.controller;

import io.danepic.springkeycloak.domain.dto.OwnerThingsDTO;
import io.danepic.springkeycloak.domain.entity.OwnerThings;
import io.danepic.springkeycloak.domain.repo.OwnerThingsRepository;
import io.danepic.springkeycloak.resource.hateoas.OwnerThingsHateoas;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("owner")
@ExposesResourceFor(Long.class)
@ApiOperation(value = "Owner API", produces = "JSON", consumes = "JSON")
@Api("owner")
public class OwnerThingsController {

    @Autowired
    private OwnerThingsRepository repository;

    @Autowired
    private OwnerThingsHateoas mapper;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @ApiOperation(value = "Create")
    public OwnerThingsDTO create(@RequestBody OwnerThingsDTO dto) {
        OwnerThings OwnerThings = mapper.toEntity(dto);
        OwnerThings save = repository.save(OwnerThings);
        return mapper.toResource(save);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('OWNER')")
    @ApiOperation(value = "Read")
    public OwnerThingsDTO read(@PathVariable Long id) {
        OwnerThings OwnerThings = repository.findById(id).orElseThrow(() -> new RuntimeException("Not found."));
        return mapper.toResource(OwnerThings);
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    @ApiOperation(value = "Read")
    public List<OwnerThingsDTO> read() {
        return mapper.toResource(repository.findAll());
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('OWNER')")
    @ApiOperation(value = "Update")
    public OwnerThingsDTO update(@PathVariable Long id, @RequestBody OwnerThingsDTO dto) {
        OwnerThings OwnerThings = repository.findById(id).orElseThrow(() -> new RuntimeException("Not found."));
        OwnerThings merge = mapper.merge(OwnerThings, mapper.toEntity(dto));
        return mapper.toResource(repository.save(merge));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('OWNER')")
    @ApiOperation(value = "Delete")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}

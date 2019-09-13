package io.danepic.springkeycloak.resource.controller;

import io.danepic.springkeycloak.domain.dto.AdminThingsDTO;
import io.danepic.springkeycloak.domain.entity.AdminThings;
import io.danepic.springkeycloak.domain.repo.AdminThingsRepository;
import io.danepic.springkeycloak.resource.hateoas.AdminThingsHateoas;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin")
@ExposesResourceFor(Long.class)
@ApiOperation(value = "Admin API", produces = "JSON", consumes = "JSON")
@Api("admin")
public class AdminThingsController {

    @Autowired
    private AdminThingsRepository repository;

    @Autowired
    private AdminThingsHateoas mapper;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasRole('OWNER')")
    @ApiOperation(value = "Create")
    public AdminThingsDTO create(@RequestBody AdminThingsDTO dto) {
        AdminThings adminThings = mapper.toEntity(dto);
        AdminThings save = repository.save(adminThings);
        return mapper.toResource(save);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @ApiOperation(value = "Read")
    public AdminThingsDTO read(@PathVariable Long id) {
        AdminThings adminThings = repository.findById(id).orElseThrow(() -> new RuntimeException("Not found."));
        return mapper.toResource(adminThings);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @ApiOperation(value = "Read")
    public List<AdminThingsDTO> read() {
        return mapper.toResource(repository.findAll());
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('OWNER')")
    @ApiOperation(value = "Update")
    public AdminThingsDTO update(@PathVariable Long id, @RequestBody AdminThingsDTO dto) {
        AdminThings adminThings = repository.findById(id).orElseThrow(() -> new RuntimeException("Not found."));
        AdminThings merge = mapper.merge(adminThings, mapper.toEntity(dto));
        return mapper.toResource(repository.save(merge));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('OWNER')")
    @ApiOperation(value = "Delete")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}

package io.danepic.springkeycloak.resource.controller;

import io.danepic.springkeycloak.domain.dto.GuestThingsDTO;
import io.danepic.springkeycloak.domain.entity.GuestThings;
import io.danepic.springkeycloak.domain.repo.GuestThingsRepository;
import io.danepic.springkeycloak.resource.hateoas.GuestThingsHateoas;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("guest")
@ExposesResourceFor(Long.class)
@ApiOperation(value = "Guest API", produces = "JSON", consumes = "JSON")
@Api("guest")
public class GuestThingsController {

    @Autowired
    private GuestThingsRepository repository;

    @Autowired
    private GuestThingsHateoas mapper;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiOperation(value = "Create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('USER')")
    public GuestThingsDTO create(@RequestBody GuestThingsDTO dto) {
        GuestThings GuestThings = mapper.toEntity(dto);
        GuestThings save = repository.save(GuestThings);
        return mapper.toResource(save);
    }

    @GetMapping("{id}")
    @ApiOperation(value = "Read")
    public GuestThingsDTO read(@PathVariable Long id) {
        GuestThings GuestThings = repository.findById(id).orElseThrow(() -> new RuntimeException("Not found."));
        return mapper.toResource(GuestThings);
    }

    @GetMapping
    @ApiOperation(value = "Read")
    public List<GuestThingsDTO> read() {
        return mapper.toResource(repository.findAll());
    }

    @PutMapping("{id}")
    @ApiOperation(value = "Update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('USER')")
    public GuestThingsDTO update(@PathVariable Long id, @RequestBody GuestThingsDTO dto) {
        GuestThings GuestThings = repository.findById(id).orElseThrow(() -> new RuntimeException("Not found."));
        GuestThings merge = mapper.merge(GuestThings, mapper.toEntity(dto));
        return mapper.toResource(repository.save(merge));
    }

    @DeleteMapping("{id}")
    @ApiOperation(value = "Delete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('USER')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}

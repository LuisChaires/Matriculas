package org.matriculas.controller;

import org.matriculas.model.Estudiante;
import org.matriculas.pagination.PageSupport;
import org.matriculas.service.IEstudianteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/estudiantes")
public class EstudianteController {

    @Autowired
    private IEstudianteService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Estudiante>>> findAll() {
        Flux<Estudiante> fx = service.findAll();
        return Mono.just(ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fx))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Estudiante>> findById(@PathVariable("id") String id) {
        return service.findById(id) //Mono<Estudiante> | Mono.empty
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Estudiante>> save(@Valid @RequestBody Estudiante client, final ServerHttpRequest req) {
        return service.save(client)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Estudiante>> update(@Valid @PathVariable("id") String id, @RequestBody Estudiante client) {
        client.setId(id);

        Mono<Estudiante> monoBody = Mono.just(client);
        Mono<Estudiante> monoDB = service.findById(id);

        return monoDB.zipWith(monoBody, (db, c) -> {
                    db.setId(id);
                    db.setNombres(c.getNombres());
                    db.setApellidos(c.getApellidos());
                    db.setEdad(c.getEdad());
                    db.setDni(c.getDni());
                    return db;
                })
                .flatMap(service::update)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id) {
        return service.findById(id)
                .flatMap(e -> service.delete(e.getId())
                        //.then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                        .thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    //pageable
    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<Estudiante>>> getPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "2") int size
    ){
        return service.getPage(PageRequest.of(page, size))
                .map(pag -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(pag)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

}


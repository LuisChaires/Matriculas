package org.matriculas.controller;

import org.matriculas.model.Curso;
import org.matriculas.pagination.PageSupport;
import org.matriculas.service.ICursoService;
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
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private ICursoService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Curso>>> findAll() {
        Flux<Curso> fx = service.findAll();
        return Mono.just(ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fx))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Curso>> findById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Curso>> save(@Valid @RequestBody Curso client, final ServerHttpRequest req) {
        return service.save(client)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Curso>> update(@Valid @PathVariable("id") String id, @RequestBody Curso client) {
        client.setId(id);

        Mono<Curso> monoBody = Mono.just(client);
        Mono<Curso> monoDB = service.findById(id);

        return monoDB.zipWith(monoBody, (db, c) -> {
                    db.setId(id);
                    db.setNombre(c.getNombre());
                    db.setSiglas(c.getSiglas());
                    db.setEstado(c.isEstado());
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
    public Mono<ResponseEntity<PageSupport<Curso>>> getPage(
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

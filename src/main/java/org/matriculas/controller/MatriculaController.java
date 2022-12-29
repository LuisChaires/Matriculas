package org.matriculas.controller;

import org.matriculas.model.Estudiante;
import org.matriculas.model.Matricula;
import org.matriculas.pagination.PageSupport;
import org.matriculas.service.IMatriculaService;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/matriculas")
public class MatriculaController {
    @Autowired
    private IMatriculaService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Matricula>>> findAll() {
        Flux<Matricula> fx = service.findAll();
        return Mono.just(ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fx))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @GetMapping("/order/{order}")
    public Mono<ResponseEntity<Flux<Matricula>>> findByIdOrder(@PathVariable("order") String order) {
        Flux<Matricula> fx = service.findAll();

        switch (order){
            //0 para orden DESC
            case "0": /*fx = fx.collectSortedList().flatMapIterable(list -> {
                            Collections.reverse(list, Comparator.comparing(Matricula::getEstudiante));
                            return list;
                        });*/
                break;
            //1 para orden ASC
            case "1": fx = fx.sort(Comparator.comparing(e -> e.getEstudiante().getEdad()));
                break;

            default:
        }

        return Mono.just(ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fx))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Matricula>> findById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Matricula>> save(@Valid @RequestBody Matricula client, final ServerHttpRequest req) {
        return service.save(client)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Matricula>> update(@Valid @PathVariable("id") String id, @RequestBody Matricula client) {
        client.setId(id);

        Mono<Matricula> monoBody = Mono.just(client);
        Mono<Matricula> monoDB = service.findById(id);

        return monoDB.zipWith(monoBody, (db, c) -> {
                    db.setId(id);
                    db.setFecha(c.getFecha());
                    db.setEstudiante(c.getEstudiante());
                    db.setCursos(c.getCursos());
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
    public Mono<ResponseEntity<PageSupport<Matricula>>> getPage(
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

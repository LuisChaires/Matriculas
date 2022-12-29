package org.matriculas.handler;

import org.matriculas.model.Curso;
import org.matriculas.service.ICursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;
@Component
public class CursoHandler {

    @Autowired
    private ICursoService service;

    public Mono<ServerResponse> findAll(ServerRequest req){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Curso.class);
    }

    public Mono<ServerResponse> findById(ServerRequest req){
        String id = req.pathVariable("id");
        return service.findById(id)
                .flatMap(client -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(client))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest req){
        Mono<Curso> monoCurso = req.bodyToMono(Curso.class);

        return monoCurso
                .flatMap(service::save)
                .flatMap(client -> ServerResponse
                        .created(URI.create(req.uri().toString().concat("/").concat(client.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(client))
                );
    }

    public Mono<ServerResponse> update(ServerRequest req){
        String id = req.pathVariable("id");

        Mono<Curso> monoCurso = req.bodyToMono(Curso.class);
        Mono<Curso> monoDB = service.findById(id);

        return monoDB
                .zipWith(monoCurso, (db, cl)-> {
                    db.setId(id);
                    db.setNombre(cl.getNombre());
                    db.setSiglas(cl.getSiglas());
                    db.setEstado(cl.isEstado());
                    return db;
                })
                .flatMap(service::update)
                .flatMap(client -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(client))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest req){
        String id = req.pathVariable("id");

        return service.findById(id)
                .flatMap(client -> service.delete(client.getId())
                        .then(ServerResponse.noContent().build())
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}

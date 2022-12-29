package org.matriculas.handler;

import org.matriculas.model.Estudiante;
import org.matriculas.service.IEstudianteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;
@Component
public class EstudianteHandler {
    @Autowired
    private IEstudianteService service;

    public Mono<ServerResponse> findAll(ServerRequest req){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Estudiante.class);
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
        Mono<Estudiante> monoEstudiante = req.bodyToMono(Estudiante.class);

        return monoEstudiante
                .flatMap(service::save)
                .flatMap(client -> ServerResponse
                        .created(URI.create(req.uri().toString().concat("/").concat(client.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(client))
                );
    }

    public Mono<ServerResponse> update(ServerRequest req){
        String id = req.pathVariable("id");

        Mono<Estudiante> monoEstudiante = req.bodyToMono(Estudiante.class);
        Mono<Estudiante> monoDB = service.findById(id);

        return monoDB
                .zipWith(monoEstudiante, (db, cl)-> {
                    db.setId(id);
                    db.setNombres(cl.getNombres());
                    db.setApellidos(cl.getApellidos());
                    db.setEdad(cl.getEdad());
                    db.setDni(cl.getDni());
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

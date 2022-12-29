package org.matriculas.handler;

import org.matriculas.model.Matricula;
import org.matriculas.service.IMatriculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Comparator;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class MatriculaHandler {

    @Autowired
    private IMatriculaService service;

    public Mono<ServerResponse> findAll(ServerRequest req){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Matricula.class);
    }

    public Mono<ServerResponse> findAllOrder(ServerRequest req){
        String order = req.pathVariable("order");

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

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx, Matricula.class);
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
        Mono<Matricula> monoMatricula = req.bodyToMono(Matricula.class);

        return monoMatricula
                .flatMap(service::save)
                .flatMap(client -> ServerResponse
                        .created(URI.create(req.uri().toString().concat("/").concat(client.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(client))
                );
    }

    public Mono<ServerResponse> update(ServerRequest req){
        String id = req.pathVariable("id");

        Mono<Matricula> monoMatricula = req.bodyToMono(Matricula.class);
        Mono<Matricula> monoDB = service.findById(id);

        return monoDB
                .zipWith(monoMatricula, (db, cl)-> {
                    db.setId(id);
                    db.setFecha(cl.getFecha());
                    db.setEstudiante(cl.getEstudiante());
                    db.setCursos(cl.getCursos());
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

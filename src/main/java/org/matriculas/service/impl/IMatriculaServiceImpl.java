package org.matriculas.service.impl;

import org.matriculas.model.Matricula;
import org.matriculas.repo.ICursoRepo;
import org.matriculas.repo.IEstudianteRepo;
import org.matriculas.repo.IGenericRepo;
import org.matriculas.repo.IMatriculaRepo;
import org.matriculas.service.IMatriculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class IMatriculaServiceImpl extends CRUDImpl<Matricula, String> implements IMatriculaService {

    @Autowired
    private IMatriculaRepo repo;

    @Autowired
    private IEstudianteRepo estudianteRepo;

    @Autowired
    private ICursoRepo cursoRepo;

    @Override
    protected IGenericRepo<Matricula, String> getRepo() {
        return repo;
    }

    @Override
    public Flux<Matricula> findAll() {

        return getRepo().findAll().flatMap(
                mat -> Mono.just(mat)
                        .zipWith(estudianteRepo.findById(mat.getEstudiante().getId()), (in, cl) -> {
                            in.setEstudiante(cl);
                            return in;
                        })).flatMap( mat -> {
            return Flux.fromIterable(mat.getCursos())
                    .flatMap(item -> {
                        return cursoRepo.findById(item.getId())
                                .map(d -> {
                                    item.setId(d.getId());
                                    item.setNombre(d.getNombre());
                                    item.setSiglas(d.getSiglas());
                                    item.setEstado(d.isEstado());
                                    return item;
                                });
                    }).collectList().flatMap(list -> {
                        mat.setCursos(list);
                        return Mono.just(mat);
                    });
        });
    }



}

package org.matriculas.service.impl;

import org.matriculas.model.Curso;
import org.matriculas.repo.ICursoRepo;
import org.matriculas.repo.IGenericRepo;
import org.matriculas.service.ICursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CursoServiceImpl extends CRUDImpl<Curso, String> implements ICursoService {

    @Autowired
    private ICursoRepo repo;

    @Override
    protected IGenericRepo<Curso, String> getRepo() {
        return repo;
    }
}

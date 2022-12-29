package org.matriculas.service.impl;

import org.matriculas.model.Estudiante;
import org.matriculas.repo.IEstudianteRepo;
import org.matriculas.repo.IGenericRepo;
import org.matriculas.service.IEstudianteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstudianteServiceImpl extends CRUDImpl<Estudiante, String> implements IEstudianteService {

    @Autowired
    private IEstudianteRepo repo;

    @Override
    protected IGenericRepo<Estudiante, String> getRepo() {
        return repo;
    }
}

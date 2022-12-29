package org.matriculas.repo;


import org.matriculas.model.User;
import reactor.core.publisher.Mono;

public interface IUserRepo extends IGenericRepo<User, String>{
    Mono<User> findOneByUsername(String username);
}

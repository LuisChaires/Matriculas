package org.matriculas.service;


import org.matriculas.model.User;
import reactor.core.publisher.Mono;

public interface IUserService extends ICRUD<User, String>{

    Mono<User> saveHash(User user);
    Mono<org.matriculas.security.User> searchByUser(String username);
}

package org.matriculas.config;


import org.matriculas.handler.CursoHandler;
import org.matriculas.handler.EstudianteHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;

@Configuration
public class RouterConfig {

    //Functional Endpoints
    @Bean
    public RouterFunction<ServerResponse> routesEstudiantes(EstudianteHandler handler){
        return route(GET("/v2/estudiantes"), handler::findAll)
                .andRoute(GET("/v2/estudiantes/{id}"), handler::findById)
                .andRoute(POST("/v2/estudiantes"), handler::create)
                .andRoute(PUT("/v2/estudiantes/{id}"), handler::update)
                .andRoute(DELETE("/v2/estudiantes/{id}"), handler::delete);
    }

    @Bean
    public RouterFunction<ServerResponse> routesCursos(CursoHandler handler){
        return route(GET("/v2/cursos"), handler::findAll)
                .andRoute(GET("/v2/cursos/{id}"), handler::findById)
                .andRoute(POST("/v2/cursos"), handler::create)
                .andRoute(PUT("/v2/cursos/{id}"), handler::update)
                .andRoute(DELETE("/v2/cursos/{id}"), handler::delete);
    }
}

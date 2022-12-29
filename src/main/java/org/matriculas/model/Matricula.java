package org.matriculas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "matricula")
public class Matricula {
    @EqualsAndHashCode.Include
    @Id
    private String id;

    @Field
    private LocalDate fecha;
    @Field
    private Estudiante estudiante;
    @Field
    private List<Curso> cursos;
    @Field
    private boolean estado;
}

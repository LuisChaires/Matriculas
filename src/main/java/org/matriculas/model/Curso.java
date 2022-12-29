package org.matriculas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "cursos")
public class Curso {
    @EqualsAndHashCode.Include
    @Id
    private String id;

    @Field
    private String nombre;
    @Field
    private String siglas;
    @Field
    private boolean estado;
}

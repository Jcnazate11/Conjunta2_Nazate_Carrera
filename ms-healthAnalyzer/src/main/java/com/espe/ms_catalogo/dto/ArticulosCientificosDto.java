package com.espe.ms_catalogo.dto;

import lombok.*;

import java.util.Date;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArticulosCientificosDto {
    private String titulo;
    private int anioPublicacion;
    private String resumen;
    private String editorial;
    private String isbn;
    private String orcid;
    private Date fechaPublicacion;
    private String revista;
    private String areaInvestigacion;
    private Long idAutor;

}

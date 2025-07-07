package com.espe.ms_catalogo.dto;

import lombok.Data;

@Data
public class CatalogoDto {
    private String tipo;

    private LibroDto libro;
    private ArticulosCientificosDto articulo;
}

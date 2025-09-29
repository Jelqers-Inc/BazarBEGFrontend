package org.esfr.BazarBEG.servicios.interfaces;

import org.esfr.BazarBEG.modelos.dtos.categorias.Categoriadto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
// 💡 Se eliminó 'import java.lang.ScopedValue;'

public interface ICategoriaService {

    // 1. CRUD adaptado a DTOs y API

    // Listado paginado (USA DTO)
    Page<Categoriadto> obtenerTodosPaginados(Pageable pageable);

    // Búsqueda por ID (USA DTO)
    Categoriadto obtenerPorId(Integer id);

    // Crear (USA DTO)
    Categoriadto crear(Categoriadto categoriaDto);

    // Editar (USA DTO)
    Categoriadto editar(Categoriadto categoriaDto);

    // Eliminar
    void eliminar(Integer id);

    byte[] obtenerImagen(Integer id);

    // Búsqueda con filtro (USA DTO)
    Page<Categoriadto> buscarPorTermino(String termino, Pageable pageable);


    // 2. Métodos de la estructura antigua (Si se usan en CatalogoController)
    // Se mantienen con el tipo 'Categoria' solo si son estrictamente necesarios
    // por otras partes del código (como CatalogoController) y serán implementados
    // con un mapeo manual o eliminados si ya no se usan.

    // Si este método es necesario por CatalogoController.java:
    List<org.esfr.BazarBEG.modelos.Categoria> obtenerTodos();

    // Si este método es necesario para compatibilidad (debe retornar null/excepción en el Service):
    Optional<org.esfr.BazarBEG.modelos.Categoria> buscarPorId(Integer id);

    // Si este método es necesario para compatibilidad (debe retornar null/excepción en el Service):
    Page<org.esfr.BazarBEG.modelos.Categoria> buscarPorNombrePaginado(String s, Pageable pageable);
}
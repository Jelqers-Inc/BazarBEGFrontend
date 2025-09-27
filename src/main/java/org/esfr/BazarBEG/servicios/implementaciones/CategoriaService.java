package org.esfr.BazarBEG.servicios.implementaciones;

import org.esfr.BazarBEG.modelos.Categoria; // Modelo de dominio antiguo (para compatibilidad)
import org.esfr.BazarBEG.modelos.dtos.categorias.Categoriadto;
import org.esfr.BazarBEG.repositorios.CategoriaRepository;
import org.esfr.BazarBEG.servicios.interfaces.ICategoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaService implements ICategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // -------------------------------------------------------------------
    // MÉTODOS NATIVOS DE LA API (Usan DTO)
    // -------------------------------------------------------------------

    @Override
    public Categoriadto crear(Categoriadto categoriaDto) {
        return categoriaRepository.crear(categoriaDto);
    }

    @Override
    public Categoriadto editar(Categoriadto categoriaDto) {
        return categoriaRepository.actualizar(categoriaDto);
    }

    @Override
    public Categoriadto obtenerPorId(Integer id) {
        // Delega directamente al repositorio, que devuelve null en caso de fallo
        return categoriaRepository.obtenerPorId(id);
    }

    @Override
    public void eliminar(Integer id) {
        categoriaRepository.eliminar(id);
    }

    // Implementación del método de búsqueda por término (filtrado local)
    @Override
    public Page<Categoriadto> buscarPorTermino(String termino, Pageable pageable) {
        List<Categoriadto> todasCategorias = categoriaRepository.obtenerTodas();

        // Filtramos por nombre en el lado del cliente (Frontend)
        List<Categoriadto> filtradas = todasCategorias.stream()
                .filter(c -> c.getNombre().toLowerCase().contains(termino.toLowerCase()))
                .collect(Collectors.toList());

        // Aplicamos paginación al resultado filtrado
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtradas.size());

        List<Categoriadto> paginadas = filtradas.subList(start, end);

        return new PageImpl<>(paginadas, pageable, filtradas.size());
    }

    @Override
    public Page<Categoriadto> obtenerTodosPaginados(Pageable pageable) {
        List<Categoriadto> todasCategorias = categoriaRepository.obtenerTodas();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), todasCategorias.size());

        List<Categoriadto> paginadas = todasCategorias.subList(start, end);

        return new PageImpl<>(paginadas, pageable, todasCategorias.size());
    }

    // -------------------------------------------------------------------
    // MÉTODOS DE COMPATIBILIDAD
    // -------------------------------------------------------------------

    private Categoria mapToOldModel(Categoriadto dto) {
        Categoria modelo = new Categoria();
        if (dto != null) {
            modelo.setId(dto.getId());
            modelo.setNombre(dto.getNombre());
            // Se asume que el modelo antiguo Categoria existe
        }
        return modelo;
    }

    @Override
    public List<Categoria> obtenerTodos() {
        List<Categoriadto> dtos = categoriaRepository.obtenerTodas();
        return dtos.stream()
                .map(this::mapToOldModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Categoria> buscarPorId(Integer id) {
        Categoriadto dto = categoriaRepository.obtenerPorId(id);
        if (dto != null) {
            return Optional.of(mapToOldModel(dto));
        }
        return Optional.empty();
    }

    @Override
    public Page<Categoria> buscarPorNombrePaginado(String s, Pageable pageable) {
        return Page.empty(pageable);
    }
}
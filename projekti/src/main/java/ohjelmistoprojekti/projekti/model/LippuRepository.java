package ohjelmistoprojekti.projekti.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface LippuRepository extends CrudRepository<Lippu, Long> {
    List<Lippu> findByKoodi(String koodi);

    List<Lippu> findByHinta(double hinta);

}

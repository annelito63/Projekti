package ohjelmistoprojekti.projekti.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface MyyntiriviRepository extends CrudRepository<Myyntirivi, Long> {
    List<Myyntirivi> findByMyynti(Myyntitapahtuma myynti);

    List<Myyntirivi> findByLipputyyppi(Lipputyyppi lipputyyppi);
}

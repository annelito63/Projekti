package ohjelmistoprojekti.projekti.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ohjelmistoprojekti.projekti.model.Tapahtuma;
import ohjelmistoprojekti.projekti.model.TapahtumaRepository;

import java.util.Optional;

@RestController
public class TapahtumaController {

    private final TapahtumaRepository tapahtumaRepository;

    public TapahtumaController(TapahtumaRepository tapahtumaRepository) {
        this.tapahtumaRepository = tapahtumaRepository;
    }

    @GetMapping("/tapahtumat")
    public Iterable<Tapahtuma> findAllTapahtumat() {
        return tapahtumaRepository.findAll();
    }

    @PostMapping("/tapahtumat")
    public ResponseEntity<Tapahtuma> addTapahtuma(@RequestBody Tapahtuma uusiTapahtuma) {
        uusiTapahtuma.setTapahtumaid(null); // kanta generoi id:n itse
        Tapahtuma tallennettu = tapahtumaRepository.save(uusiTapahtuma);
        return ResponseEntity.status(HttpStatus.CREATED).body(tallennettu);
    }

    @GetMapping("/tapahtumat/{id}")
    public ResponseEntity<Tapahtuma> findTapahtuma(@PathVariable Long id) {
        Optional<Tapahtuma> tapahtuma = tapahtumaRepository.findById(id);
        return tapahtuma.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/tapahtumat/{id}")
    public ResponseEntity<Tapahtuma> updateTapahtuma(@PathVariable Long id, @RequestBody Tapahtuma tapahtuma) {
        Optional<Tapahtuma> existingTapahtuma = tapahtumaRepository.findById(id);

        if (existingTapahtuma.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tapahtuma existing = existingTapahtuma.get();
        existing.setNimi(tapahtuma.getNimi());
        existing.setTyyppi(tapahtuma.getTyyppi());
        existing.setAika(tapahtuma.getAika());
        existing.setKaupunki(tapahtuma.getKaupunki());
        existing.setPaikka(tapahtuma.getPaikka());
        existing.setKuvaus(tapahtuma.getKuvaus());
        existing.setMaara(tapahtuma.getMaara());

        Tapahtuma updated = tapahtumaRepository.save(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/tapahtumat/{id}")
    public ResponseEntity<Void> deleteTapahtuma(@PathVariable Long id) {
        Optional<Tapahtuma> existingTapahtuma = tapahtumaRepository.findById(id);

        if (existingTapahtuma.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        tapahtumaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}

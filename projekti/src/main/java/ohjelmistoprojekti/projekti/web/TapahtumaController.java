package ohjelmistoprojekti.projekti.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

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

}

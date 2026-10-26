package ohjelmistoprojekti.projekti.web;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import ohjelmistoprojekti.projekti.model.Lippu;
import ohjelmistoprojekti.projekti.model.LippuRepository;
import ohjelmistoprojekti.projekti.web.dto.LippuDto;
import ohjelmistoprojekti.projekti.web.dto.LippuRequest;

@RestController
@RequestMapping("/ticket")
@CrossOrigin(origins = "*")
public class LippuController {

    private final LippuRepository lippuRepository;

    public LippuController(LippuRepository lippuRepository) {
        this.lippuRepository = lippuRepository;
    }

    @GetMapping("/ticket")
    public ResponseEntity<LippuDto> findByKoodi(@RequestParam String koodi) {

        Lippu lippu = lippuRepository.findByKoodi(koodi)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lippua ei löytynyt"));

        LippuDto dto = new LippuDto(
                lippu.getId(),
                lippu.getTapahtumaid(),
                lippu.getKoodi(),
                lippu.getKuvaus(),
                lippu.getHinta(),
                lippu.getLipputyyppi(),
                lippu.isKaytetty());

        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LippuDto> merkitseKaytetyksi(
            @PathVariable Long id,
            @RequestBody LippuRequest request) {

        Lippu lippu = lippuRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lippua ei löytynyt"));

        lippu.setKaytetty(request.kaytetty());

        Lippu paivitetty = lippuRepository.save(lippu);

        LippuDto dto = new LippuDto(
                paivitetty.getId(),
                paivitetty.getTapahtumaid(),
                paivitetty.getKoodi(),
                paivitetty.getKuvaus(),
                paivitetty.getHinta(),
                paivitetty.getLipputyyppi(),
                paivitetty.isKaytetty());

        return ResponseEntity.ok(dto);
    }

}

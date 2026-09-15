package ohjelmistoprojekti.projekti.web;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ohjelmistoprojekti.projekti.model.Tapahtuma;
import ohjelmistoprojekti.projekti.model.TapahtumaRepository;

@RestController
public class TapahtumaController {

    private final TapahtumaRepository tapahtumaRepository;

    public TapahtumaController(TapahtumaRepository tapahtumaRepository) {
        this.tapahtumaRepository = tapahtumaRepository;
    }

     @GetMapping("/tapahtumat")
    public Iterable<Tapahtuma> findAllTapahtumat(){
        return tapahtumaRepository.findAll();
    }

}

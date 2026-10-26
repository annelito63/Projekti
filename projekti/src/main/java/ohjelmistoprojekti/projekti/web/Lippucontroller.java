package ohjelmistoprojekti.projekti.web;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ohjelmistoprojekti.projekti.model.LippuRepository;

@RestController
@CrossOrigin(origins = "*")
public class Lippucontroller {

    private final LippuRepository lippuRepository;

    public Lippucontroller(LippuRepository lippuRepository){
        this.lippuRepository = lippuRepository;
    }



    
}

package ohjelmistoprojekti.projekti.web.dto;

import java.nio.charset.StandardCharsets;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ohjelmistoprojekti.projekti.model.Rooli;

public record KayttajanLuontiPyynto(
        @NotBlank @Size(max = 120) String nimi,
        @NotBlank @Email @Size(max = 254) String sahkoposti,
        @NotBlank @Size(min = 8, max = 72) String salasana,
        @NotNull Rooli rooli) {

    @AssertTrue(message = "Salasana saa olla enintään 72 UTF-8-tavua")
    public boolean isSalasananBcryptPituusKelvollinen() {
        return salasana == null || salasana.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
}

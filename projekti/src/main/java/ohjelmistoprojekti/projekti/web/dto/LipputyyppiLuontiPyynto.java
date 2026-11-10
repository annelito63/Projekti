package ohjelmistoprojekti.projekti.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LipputyyppiLuontiPyynto(
        @NotBlank String nimi,
        String kuvaus,
        @NotNull @DecimalMin("0.0") BigDecimal hinta) {
}

package ohjelmistoprojekti.projekti.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Test;

import ohjelmistoprojekti.projekti.web.dto.MyyntiriviLuontiDto;
import ohjelmistoprojekti.projekti.web.dto.MyyntitapahtumaLuontiDto;

class MyyntitapahtumaDtoTest {

    @Test
    void myyntiriviLuontiDtoOletuskonstruktoriJaSetteritToimivat() {
        System.out.println("\n[DTO-TESTI] MyyntiriviLuontiDto: oletuskonstruktori ja setterit");
        MyyntiriviLuontiDto dto = new MyyntiriviLuontiDto();

        System.out.println("  Oletusarvot: lipputyyppiId=" + dto.getLipputyyppiId()
            + ", maara=" + dto.getMaara());
        assertNull(dto.getLipputyyppiId());
        assertEquals(0, dto.getMaara());

        dto.setLipputyyppiId(12L);
        dto.setMaara(3);

        System.out.println("  Setterien arvot: lipputyyppiId=" + dto.getLipputyyppiId()
                + ", maara=" + dto.getMaara());

        assertEquals(12L, dto.getLipputyyppiId());
        assertEquals(3, dto.getMaara());
        System.out.println("  OK: oletusarvot ja setterit toimivat.");
    }

    @Test
    void myyntiriviLuontiDtoParametrikonstruktoriAlustaaKentat() {
        System.out.println("\n[DTO-TESTI] MyyntiriviLuontiDto: parametrikonstruktori");
        MyyntiriviLuontiDto dto = new MyyntiriviLuontiDto(7L, 5);

        System.out.println("  Luodut arvot: lipputyyppiId=" + dto.getLipputyyppiId()
                + ", maara=" + dto.getMaara());

        assertEquals(7L, dto.getLipputyyppiId());
        assertEquals(5, dto.getMaara());
        System.out.println("  OK: parametrikonstruktori alustaa molemmat kentat.");
    }

    @Test
    void myyntitapahtumaLuontiDtoOletuskonstruktoriJaSetteritToimivat() {
        System.out.println("\n[DTO-TESTI] MyyntitapahtumaLuontiDto: oletuskonstruktori ja setterit");
        MyyntitapahtumaLuontiDto dto = new MyyntitapahtumaLuontiDto();

        System.out.println("  Oletusarvot: tapahtumaId=" + dto.getTapahtumaId()
            + ", kayttajaId=" + dto.getKayttajaId() + ", rivit=" + dto.getRivit());
        assertNull(dto.getTapahtumaId());
        assertNull(dto.getKayttajaId());
        assertNull(dto.getRivit());

        List<MyyntiriviLuontiDto> rivit = List.of(new MyyntiriviLuontiDto(2L, 4));
        dto.setTapahtumaId(20L);
        dto.setKayttajaId(30L);
        dto.setRivit(rivit);

        System.out.println("  Setterien arvot: tapahtumaId=" + dto.getTapahtumaId()
            + ", kayttajaId=" + dto.getKayttajaId()
            + ", rivien maara=" + dto.getRivit().size());

        assertEquals(20L, dto.getTapahtumaId());
        assertEquals(30L, dto.getKayttajaId());
        assertSame(rivit, dto.getRivit());
        System.out.println("  OK: tunnisteet ja rivilista tallentuvat oikein.");
    }

    @Test
    void myyntitapahtumaLuontiDtoParametrikonstruktoriAlustaaKaikkiKentat() {
        System.out.println("\n[DTO-TESTI] MyyntitapahtumaLuontiDto: parametrikonstruktori ja rivit");
        MyyntiriviLuontiDto rivi = new MyyntiriviLuontiDto(9L, 2);
        List<MyyntiriviLuontiDto> rivit = List.of(rivi);

        MyyntitapahtumaLuontiDto dto = new MyyntitapahtumaLuontiDto(40L, 50L, rivit);

        System.out.println("  Luodut arvot: tapahtumaId=" + dto.getTapahtumaId()
            + ", kayttajaId=" + dto.getKayttajaId()
            + ", rivien maara=" + dto.getRivit().size());
        System.out.println("  Ensimmainen rivi: lipputyyppiId="
            + dto.getRivit().get(0).getLipputyyppiId()
            + ", maara=" + dto.getRivit().get(0).getMaara());

        assertEquals(40L, dto.getTapahtumaId());
        assertEquals(50L, dto.getKayttajaId());
        assertSame(rivit, dto.getRivit());
        assertSame(rivi, dto.getRivit().get(0));
        assertEquals(9L, dto.getRivit().get(0).getLipputyyppiId());
        assertEquals(2, dto.getRivit().get(0).getMaara());
        System.out.println("  OK: tapahtuman tiedot ja sisakkainen myyntirivi ovat oikein.");
    }
}
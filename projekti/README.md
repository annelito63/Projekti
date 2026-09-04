Lipunmyyntijärjestelmä

Tiimi: Kuisma Abara, Kalle Honkanen, Kasperi Kanto, Anneli Torvi

Johdanto
Projektin tarkoituksena on toteuttaa lipunmyyntijärjestelmä, jonka avulla asiakas voi myydä lippuja erilaisiin tapahtumiin. Tarkoituksena on saada myös aikaan palvelu, jolla asiakas voi itse ostaa lippuja verkkokaupassa.

Toteutus- ja toimintaympäristö:
- Palvelinpuoli toteutetaan Spring Boot -teknologialla
- Tietokantana ratkaisu päätetään myöhemmin
- Käyttöliittymä toteutetaan web-selaimessa käytettävänä sovelluksena

Järjestelmän määrittely

Järjestelmän käyttäjänä on asiakasyritys ja lopulta myös asiakasyrityksen asiakkaat.
Asiakasyrityksen tarvitsemat toiminnot: tapahtumien luominen, lipputyyppien määrittely, lippujen identifiointi, raportti myydyistä lipuista

Käyttötapaus: 
-yritys luo tapahtuman ja määrittelee sille haluamansa lipputyypit ja niiden hinnat
-yritys määrittelee lippujen kokonaismäärän
-lippuja myydään asiakkaille, lippujen oltava yksilöitävissä
-loput liput tulostetaan ja myydään ovella

Verkkokaupan asiakkaan tarvitsemat toiminnot: tapahtuman valinta, lipputyypin valinta, lippujen määrän valinta

Käyttötapaus:
-asiakas avaa verkkokaupan ja valitsee haluamansa tapahtuman
-asiakas valitsee sopivan lipputyypin ja lippujen määrän
- ostettuaan voi tulostaa lipun

Käyttäjäroolit:
- Myyjä: lipunmyyntipisteessä työskentelevä henkilö, joka myy ja tulostaa lippuja asiakkaille
- Ylläpitäjä: toimiston työntekijä, joka hallinnoi tapahtumia, lipputyyppejä ja seuraa myyntiä
- Ovella tarkastava henkilö: tarkastaa lipun koodin ja merkitsee lipun käytetyksi
- (Jatkokehitys) Verkkokaupan asiakas: ostaa lippuja itse verkossa

Käyttäjätarinat:

Ylläpitäjä
- Ylläpitäjänä haluan luoda uuden tapahtuman, jotta sille voidaan alkaa myydä lippuja.
- Ylläpitäjänä haluan määritellä tapahtumalle lipputyypit ja niiden hinnat, jotta myyjä voi myydä oikean tyyppisiä lippuja.
- Ylläpitäjänä haluan asettaa tapahtumalle myytävien lippujen enimmäismäärän, jotta lippuja ei myydä yli kapasiteetin.
- Ylläpitäjänä haluan nähdä myyntiraportin tapahtumakohtaisesti, jotta voin seurata myynnin etenemistä.

Myyjä
- Myyjänä haluan valita tapahtuman ja lipputyypin, jotta voin myydä asiakkaalle oikean lipun.
- Myyjänä haluan syöttää myytävien lippujen määrän, jotta järjestelmä laskee summan automaattisesti.
- Myyjänä haluan tulostaa myydyt liput, jotta asiakas saa fyysisen lipun mukaansa.
- Myyjänä haluan, että jokainen lippu on yksilöity omalla koodillaan, jotta lippuja ei voida käyttää useaan kertaan.

Ovella tarkastava henkilö
- Ovella tarkastavana henkilönä haluan syöttää tai skannata lipun koodin, jotta voin tarkistaa lipun kelpoisuuden nopeasti.
- Ovella tarkastavana henkilönä haluan, että käytetty lippu merkitään järjestelmään käytetyksi, jotta samaa lippua ei voida käyttää uudelleen.

Käyttöliittymä:
    Myynti:
    -Kenttä, johon syötetään tapahtuman nimi
    -Valintasarake, josta valitaan tapahtuman tyyppi
    -Valintasarakkeet, joista valitaan eri lipputyypit
    -Kenttä/kentät, joihin syötetään kunkin lipputyypin hinta
    -Kenttä, johon syötetään myytävien lippujen maksimimäärä
    -Näkymässä näkyy valittujen lippujen yhteissumma ja "Myy"-painike
    -Onnistuneen myynnin jälkeen näytetään myyntitapahtuman tiedot

    Tapahtumien hallinta:
    -Lista olemassa olevista tapahtumista: aika, kaupunki, kuvaus, paikka, lippujen määrä.
    -"Uusi tapahtuma" -toiminto
    -Mahdollisuus muokata olemassa olevan tapahtuman tietoja

    Lipputyypit:
    -Lista lipputyypeistä: kuvaus ja hinta
    -Mahdollisuus muokata olemassa olevaa lipputyyppiä tai lisätä uusi

    Myyntiraportti:
    -Yhteenveto myydyistä lipuista lipputyypeittäin (kappalemäärä ja summa)
    -Lista yksittäisitä myyntitapatumista aikaleimalla ja summalla
    Mahdollisuus tarkastella yksittäisten myyntitapahtuman tarkempia tietoja

    Osto:
    -Toteutetaan jatkokehityksessä myöhemmässä sprintissä. Alustavasti asiakas valitsee tapahtuman, lipputyypin ja lippujen määrän, minkä jälkeen voi tulostaa ostetun lipun.


# Lipunmyyntijärjestelmä

Tiimi: Kalle Honkanen, Kasperi Kanto, Anneli Torvi

## Johdanto

Projektin tarkoituksena on toteuttaa lipunmyyntijärjestelmä, jonka avulla asiakas voi myydä lippuja erilaisiin tapahtumiin. Tarkoituksena on saada myös aikaan palvelu, jolla asiakas voi itse ostaa lippuja verkkokaupassa.

### Toteutus- ja toimintaympäristö

- Palvelinpuoli toteutetaan Spring Boot -teknologialla (Java 17, Maven)
- Tietokantana on paikallisessa kehityksessä H2 ja tuotannossa (Render) PostgreSQL. Tietokanta valitaan Spring-profiililla, ks. kohta [Tietokanta](#tietokanta)
- Käyttöliittymä toteutetaan web-selaimessa käytettävänä sovelluksena
- Palvelin on julkaistu Render.comissa Docker-ympäristössä

## Järjestelmän määrittely

Järjestelmän käyttäjänä on asiakasyritys ja lopulta myös asiakasyrityksen asiakkaat.
Asiakasyrityksen tarvitsemat toiminnot: tapahtumien luominen, lipputyyppien määrittely, lippujen identifiointi, raportti myydyistä lipuista

Käyttötapaus:
- yritys luo tapahtuman ja määrittelee sille haluamansa lipputyypit ja niiden hinnat
- yritys määrittelee lippujen kokonaismäärän
- lippuja myydään asiakkaille, lippujen oltava yksilöitävissä
- loput liput tulostetaan ja myydään ovella

Verkkokaupan asiakkaan tarvitsemat toiminnot: tapahtuman valinta, lipputyypin valinta, lippujen määrän valinta

Käyttötapaus:
- asiakas avaa verkkokaupan ja valitsee haluamansa tapahtuman
- asiakas valitsee sopivan lipputyypin ja lippujen määrän
- ostettuaan voi tulostaa lipun

### Käyttäjäroolit

- Myyjä: lipunmyyntipisteessä työskentelevä henkilö, joka myy ja tulostaa lippuja asiakkaille
- Ylläpitäjä: toimiston työntekijä, joka hallinnoi tapahtumia, lipputyyppejä ja seuraa myyntiä
- Ovella tarkastava henkilö: tarkastaa lipun koodin ja merkitsee lipun käytetyksi
- (Jatkokehitys) Verkkokaupan asiakas: ostaa lippuja itse verkossa

### Käyttäjätarinat

**Ylläpitäjä**
- Ylläpitäjänä haluan luoda uuden tapahtuman, jotta sille voidaan alkaa myydä lippuja.
- Ylläpitäjänä haluan määritellä tapahtumalle lipputyypit ja niiden hinnat, jotta myyjä voi myydä oikean tyyppisiä lippuja.
- Ylläpitäjänä haluan asettaa tapahtumalle myytävien lippujen enimmäismäärän, jotta lippuja ei myydä yli kapasiteetin.
- Ylläpitäjänä haluan nähdä myyntiraportin tapahtumakohtaisesti, jotta voin seurata myynnin etenemistä.

**Myyjä**
- Myyjänä haluan valita tapahtuman ja lipputyypin, jotta voin myydä asiakkaalle oikean lipun.
- Myyjänä haluan syöttää myytävien lippujen määrän, jotta järjestelmä laskee summan automaattisesti.
- Myyjänä haluan tulostaa myydyt liput, jotta asiakas saa fyysisen lipun mukaansa.
- Myyjänä haluan, että jokainen lippu on yksilöity omalla koodillaan, jotta lippuja ei voida käyttää useaan kertaan.

**Ovella tarkastava henkilö**
- Ovella tarkastavana henkilönä haluan syöttää tai skannata lipun koodin, jotta voin tarkistaa lipun kelpoisuuden nopeasti.
- Ovella tarkastavana henkilönä haluan, että käytetty lippu merkitään järjestelmään käytetyksi, jotta samaa lippua ei voida käyttää uudelleen.

### Käyttöliittymä

**Myynti**
- Kenttä, johon syötetään tapahtuman nimi
- Valintasarake, josta valitaan tapahtuman tyyppi
- Valintasarakkeet, joista valitaan eri lipputyypit
- Kenttä/kentät, joihin syötetään kunkin lipputyypin hinta
- Kenttä, johon syötetään myytävien lippujen maksimimäärä
- Näkymässä näkyy valittujen lippujen yhteissumma ja "Myy"-painike
- Onnistuneen myynnin jälkeen näytetään myyntitapahtuman tiedot

**Tapahtumien hallinta**
- Lista olemassa olevista tapahtumista: aika, kaupunki, kuvaus, paikka, lippujen määrä.
- "Uusi tapahtuma" -toiminto
- Mahdollisuus muokata olemassa olevan tapahtuman tietoja

**Lipputyypit**
- Lista lipputyypeistä: kuvaus ja hinta
- Mahdollisuus muokata olemassa olevaa lipputyyppiä tai lisätä uusi

**Myyntiraportti**
- Yhteenveto myydyistä lipuista lipputyypeittäin (kappalemäärä ja summa)
- Lista yksittäisistä myyntitapahtumista aikaleimalla ja summalla
- Mahdollisuus tarkastella yksittäisten myyntitapahtumien tarkempia tietoja

**Osto**
- Toteutetaan jatkokehityksessä myöhemmässä sprintissä. Alustavasti asiakas valitsee tapahtuman, lipputyypin ja lippujen määrän, minkä jälkeen voi tulostaa ostetun lipun.

## Tietokannan relaatiot ja toiminta (07.09.2026)

Järjestelmän tärkeimmät kokonaisuudet ovat käyttäjät, tapahtumat, lipputyypit, liput sekä myyntitapahtumat ja niiden rivit.

- **Käyttäjä** kuvaa järjestelmän eri käyttäjärooleja, kuten ylläpitäjää, myyjää ja ovella tarkastavaa henkilöä. Käyttäjä voi hallinnoida tapahtumia, tehdä myyntitapahtumia ja suorittaa lippujen tarkastuksia roolinsa mukaisesti.
- **Tapahtuma** sisältää tapahtuman perustiedot, kuten nimen, ajankohdan, kaupungin, paikan, kuvauksen ja lippujen enimmäismäärän. Tapahtumalle voidaan määritellä useita lipputyyppejä, joilla on oma nimi, kuvaus ja hinta.
- **Lippu** liittyy tiettyyn tapahtumaan ja lipputyyppiin. Jokaiselle lipulle muodostetaan yksilöllinen koodi, jonka avulla lippu voidaan tunnistaa ja tarkastaa. Lipun tila kertoo, onko lippu esimerkiksi käyttämätön tai käytetty.
- **Myyntitapahtuma** kuvaa yhden myyntikerran ja sisältää esimerkiksi myyntiajan sekä kokonaissumman. Myyntitapahtumaan liittyvät **myyntirivit**, joissa määritellään myyty lipputyyppi, määrä, yksikköhinta ja rivin summa. Näiden tietojen avulla voidaan muodostaa tapahtumakohtaisia myyntiraportteja.
- **Tarkastus** tallentaa lipun tarkastamiseen liittyvät tiedot, kuten tarkastusajan ja tarkastuksen suorittaneen käyttäjän. Näin järjestelmä voi estää saman lipun käyttämisen useaan kertaan.

Verkkokaupan osto-toiminnallisuus toteutetaan myöhemmässä kehitysvaiheessa, joten sitä ei ole huomioitu tässä relaatiomallissa.

## Tapahtuman muokkaus ja poisto

Järjestelmässä on toteutettu perus REST-rajapinta tapahtumien muokkaamiseen ja poistamiseen. Toiminnot toimivat Spring Bootin controller-luokan kautta, ja ne käyttävät TapahtumaRepositorya tietokannan lukemiseen ja kirjoittamiseen.

### Tapahtuman muokkaaminen

Tapahtumaa voidaan päivittää HTTP PUT -pyynnöllä osoitteeseen `/tapahtumat/{id}`. Pyynnössä lähetetään päivitettävän tapahtuman uudet tiedot JSON-muodossa.

Päivitettävä tapahtuma voi sisältää seuraavat kentät:

- nimi
- tyyppi
- aika
- kaupunki
- paikka
- kuvaus
- maara

Jos tapahtumaa ei löydy annetulla id:llä, palvelin vastaa `404 Not Found`. Jos tapahtuma löytyy, järjestelmä päivittää olemassa olevan entiteetin kentät ja tallentaa muutokset takaisin tietokantaan. Onnistuneen päivityksen jälkeen palvelin palauttaa päivitetyn tapahtuman `200 OK` -vastauksena.

Tapahtuman muokkaaminen vaatii `YLLAPITAJA`-roolin. Kenttien pakollisuutta validoidaan, mutta liiketoimintatarkistusta, joka estäisi tapahtuman kapasiteetin pienentämisen alle jo myytyjen lippujen määrän, ei vielä ole.

### Tapahtuman poistaminen

Tapahtuma voidaan poistaa HTTP DELETE -pyynnöllä osoitteeseen `/tapahtumat/{id}`. Jos tapahtuma löytyy, järjestelmä kutsuu `deleteById(id)` ja palauttaa `204 No Content` -vastauksen. Jos tapahtumaa ei löydy, palautetaan `404 Not Found`.

Poisto vaatii `YLLAPITAJA`-roolin. Se on tietokantapoisto ilman ylimääräistä vahvistuslogiikkaa tai passiivista poistoa; tällaiset käytännöt voidaan toteuttaa erikseen, jos niitä tarvitaan.

### Nykyinen tilanne ja rajoitukset

- Tapahtumien listaus, haku, lisäys, muokkaus ja poisto toimivat perusrajapintatasolla.
- Rajapinta on toteutettu Spring Bootin REST-controllerilla.
- Tapahtumien kirjoitustoiminnot on rajattu ylläpitäjälle; kapasiteetin pienentämistä jo myytyjen lippujen alle ei vielä estetä.

## Kirjautuminen ja käyttöoikeudet

Yksityiskohtaiset ohjeet rekisteröitymiseen, bootstrap-ylläpitäjään, roolien oikeuksiin ja API-testaukseen löytyvät [autentikoinnin dokumentaatiosta](./dokumentaatio/Autentikointi.md).

REST-rajapinta käyttää HTTP Basic -kirjautumista ja salasanojen BCrypt-tiivistystä. Jokainen kirjautumista vaativa pyyntö sisältää `Authorization: Basic ...` -otsakkeen. Rekisteröinti on julkinen, ja sillä luodaan aina `VERKKOKAUPAN_ASIAKAS`-roolin käyttäjä:

```http
POST /auth/register
Content-Type: application/json

{"nimi":"Asiakas","sahkoposti":"asiakas@example.fi","salasana":"vähintään8merkkiä"}
```

Kirjautumisen voi tarkistaa osoitteesta `GET /auth/me`. Käyttäjä voi hakea omat tilauksensa osoitteista `GET /myynnit` ja `GET /myynnit/{id}`. Muiden käyttäjien tilaukset palauttavat asiakkaalle `404 Not Found`, eikä asiakkaan myyntipyynnössä annettu käyttäjä-ID muuta myynnin omistajaa.

Tapahtumien listaus ja yksittäisen tapahtuman haku ovat julkisia verkkokaupan selaamista varten. Tapahtumien luonti, muokkaus ja poisto vaativat `YLLAPITAJA`-roolin. `YLLAPITAJA` toimii myös tapahtumien järjestäjänä. `MYYJA` ja `YLLAPITAJA` voivat nähdä kaikki myynnit; asiakkaat näkevät vain omansa. Henkilöstön käyttäjät luo ylläpitäjä kirjautuneena osoitteessa `POST /auth/users` antamalla lisäksi roolin, esimerkiksi `{"rooli":"MYYJA"}`.

Ensimmäinen ylläpitäjä luodaan käynnistyksen yhteydessä vain, kun ympäristömuuttujat `BOOTSTRAP_ADMIN_EMAIL` ja `BOOTSTRAP_ADMIN_PASSWORD` on asetettu. `BOOTSTRAP_ADMIN_NAME` on valinnainen. Salasanaa ei tallenneta selväkielisenä eikä sitä palauteta API-vastauksissa. Ilman bootstrap-asetuksia avoin rekisteröinti ei voi luoda ylläpitäjän oikeuksia.

Bootstrap-ylläpitäjä luodaan vain, jos samalla sähköpostilla ei ole jo käyttäjää. Pysyvässä tietokannassa uudelleenkäynnistys ei siis luo duplikaattia eikä kaada sovellusta. Huomaa, että jo luodun ylläpitäjän salasana **ei päivity**, vaikka `BOOTSTRAP_ADMIN_PASSWORD`-muuttujan arvoa vaihdettaisiin myöhemmin.

Koska HTTP Basic lähettää tunnistetiedot jokaisessa pyynnössä, tuotantoympäristössä rajapintaa tulee käyttää vain HTTPS-yhteyden kautta. Render tarjoaa HTTPS:n automaattisesti.

Roolit ovat `YLLAPITAJA`, `MYYJA`, `OVELLA_TARKASTAVA_HENKILO` ja `VERKKOKAUPAN_ASIAKAS`. Roolien tapahtumatoiminnot rajautuvat yllä mainittuihin reitteihin; ovella tehtävän lipuntarkastuksen erillistä API-toimintoa ei ole vielä toteutettu.

### Oikeudet yhdellä silmäyksellä

| Toiminto | Kuka |
|---|---|
| Rekisteröityä asiakkaaksi (`POST /auth/register`) | kuka tahansa |
| Katsoa tapahtumia (`GET /tapahtumat`, `GET /tapahtumat/{id}`) | kuka tahansa |
| Luoda, muokata ja poistaa tapahtumia | `YLLAPITAJA` |
| Nähdä kaikki myynnit | `MYYJA`, `YLLAPITAJA` |
| Nähdä omat myynnit | kirjautunut käyttäjä |
| Luoda henkilöstökäyttäjiä (`POST /auth/users`) | `YLLAPITAJA` |

## Tietokanta

Sovellus tukee kahta tietokantaa, ja valinta tehdään Spring-profiililla. Koodia ei tarvitse muuttaa tietokantaa vaihdettaessa.

| Ympäristö | Tietokanta | Profiili |
|---|---|---|
| Paikallinen kehitys (oletus) | H2, tiedostopohjainen (`./data/projektidb`) | ei profiilia |
| Render (tuotanto) | PostgreSQL | `prod` |
| Automaattiset testit | H2, muistinvarainen | automaattinen |

### Tiedostot

- `pom.xml`: sisältää sekä H2- että PostgreSQL-ajurin (`org.postgresql:postgresql`)
- `src/main/resources/application.properties`: oletusasetukset (H2)
- `src/main/resources/application-prod.properties`: PostgreSQL-asetukset, jotka luetaan ympäristömuuttujista
- `src/test/resources/application.properties`: testien oma konfiguraatio (muistinvarainen H2)

**`application.properties` (oletus, H2):**

```properties
spring.application.name=projekti

app.bootstrap-admin.email=${BOOTSTRAP_ADMIN_EMAIL:}
app.bootstrap-admin.password=${BOOTSTRAP_ADMIN_PASSWORD:}
app.bootstrap-admin.name=${BOOTSTRAP_ADMIN_NAME:}

spring.datasource.url=jdbc:h2:file:./data/projektidb
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
```

**`application-prod.properties` (PostgreSQL):**

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
```

Salasanoja ei kirjoiteta tiedostoihin, vaan ne annetaan ympäristömuuttujina. `.gitignore` sisältää rivit `data/` ja `.env`, jotta paikallinen H2-kanta tai salaisuudet eivät päädy GitHubiin.

### Ympäristömuuttujat

| Muuttuja | Kuvaus |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` = PostgreSQL, tyhjä = paikallinen H2 |
| `DB_URL` | `jdbc:postgresql://HOST:5432/TIETOKANNAN_NIMI` |
| `DB_USERNAME` | Tietokannan käyttäjä |
| `DB_PASSWORD` | Tietokannan salasana (älä tallenna repositorioon) |
| `BOOTSTRAP_ADMIN_EMAIL` | Ensimmäisen ylläpitäjän sähköposti (ei tarvitse olla oikea) |
| `BOOTSTRAP_ADMIN_PASSWORD` | Ensimmäisen ylläpitäjän salasana (enintään 72 tavua) |
| `BOOTSTRAP_ADMIN_NAME` | Ylläpitäjän nimi (valinnainen) |

`DB_URL` on JDBC-muotoa (`jdbc:postgresql://...`). Renderin näyttämä `postgresql://käyttäjä:salasana@host/nimi` -osoite ei kelpaa sellaisenaan, ja käyttäjä ja salasana annetaan erillisinä muuttujina.

### Paikallinen ajo H2-tietokannalla (oletus)

Aja projektin kansiossa (siellä missä `pom.xml` on):

```powershell
.\mvnw.cmd spring-boot:run
```

Jos tarvitset paikallisen ylläpitäjän, aseta ensin bootstrap-muuttujat:

```powershell
$env:BOOTSTRAP_ADMIN_EMAIL="admin@projekti.fi"
$env:BOOTSTRAP_ADMIN_PASSWORD="jokin-salasana-123"
.\mvnw.cmd spring-boot:run
```

H2-tunnukset ovat käyttäjä `sa` ja tyhjä salasana, mutta niitä ei tarvitse syöttää mihinkään, koska sovellus yhdistää automaattisesti. Sovellus kuuntelee osoitteessa `http://localhost:8080`. Pysäytä se painamalla Ctrl+C. Paikallisen kannan voi nollata poistamalla `data/`-kansion.

### Paikallinen ajo PostgreSQL:llä (Renderin kantaa vasten)

Paikallisesta koneesta käytetään Renderin **External**-osoitetta ja SSL:ää:

```powershell
$env:DB_URL="jdbc:postgresql://HOST.frankfurt-postgres.render.com:5432/TIETOKANNAN_NIMI?sslmode=require"
$env:DB_USERNAME="käyttäjä"
$env:DB_PASSWORD="salasana"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=prod"
```

Muutokset menevät tällöin Renderin oikeaan tietokantaan.

### Vaihtaminen takaisin H2:een

Käynnistä sovellus ilman `prod`-profiilia:

```powershell
.\mvnw.cmd spring-boot:run
```

Jos samassa PowerShell-ikkunassa on aiemmin asetettu `$env:SPRING_PROFILES_ACTIVE="prod"`, poista se ennen ajoa:

```powershell
Remove-Item Env:SPRING_PROFILES_ACTIVE
```

Voit tarkistaa käytössä olevan profiilin käynnistyslokista: rivi `No active profile set` tarkoittaa H2:ta, ja `The following 1 profile is active: "prod"` PostgreSQL:ää.

### Milloin kumpaakin käytetään

- **H2:** normaali kehitys, rajapinnan kokeilu Postmanilla ja testit. Se on nopea eikä voi sotkea tuotantodataa.
- **PostgreSQL:** Render-palvelu sekä tilanteet, joissa halutaan varmistaa toiminta oikealla PostgreSQL:llä tai selvittää vain Renderissä esiintyvää virhettä.

H2- ja PostgreSQL-kannat ovat täysin erilliset, eikä niiden data synkronoidu. Kun ryhmäläinen ajaa sovelluksen omalla koneellaan ilman `prod`-profiilia, data menee hänen omaan H2-kantaansa, ei Renderin PostgreSQL:ään.

## Docker ja Render

Projektin `Dockerfile` rakentaa sovelluksen Java 17:llä ja käynnistää sen Renderin tarjoamassa `PORT`-portissa (`--server.port=${PORT:-8080}`). Dockerfile olettaa, että `pom.xml` ja `src` ovat samassa kansiossa sen kanssa. Renderissä luo **Web Service**, valitse **Docker**-ympäristö ja aseta palvelun juurihakemistoksi `projekti` (Maven-projektin hakemisto), jos `pom.xml` on alikansiossa.

### Käyttöönotto Renderissä

1. **Luo PostgreSQL-tietokanta:** New + → PostgreSQL, Instance Type: Free. Valitse alue (esim. Frankfurt) ja muista se, koska web servicen pitää olla samalla alueella.
2. **Kirjoita ylös yhteystiedot** tietokannan sivun Connections-osiosta: Hostname (Internal), Database, Username ja Password. Muodosta niistä `DB_URL`: `jdbc:postgresql://HOSTNAME:5432/DATABASE`. Tietokannan nimen (Database) täytyy täsmätä täsmälleen Renderin antamaan nimeen, sillä väärä nimi aiheuttaa käynnistysvirheen `database "..." does not exist`.
3. **Luo Web Service:** New + → Web Service, yhdistä GitHub-repositorio, Language: Docker, sama alue kuin tietokannalla, Instance Type: Free.
4. **Aseta ympäristömuuttujat** (Environment-välilehti):

   ```
   SPRING_PROFILES_ACTIVE=prod
   DB_URL=jdbc:postgresql://dpg-xxxx-a:5432/TIETOKANNAN_NIMI
   DB_USERNAME=...
   DB_PASSWORD=...
   BOOTSTRAP_ADMIN_EMAIL=...
   BOOTSTRAP_ADMIN_PASSWORD=...
   BOOTSTRAP_ADMIN_NAME=...
   ```

   Käytä Renderin sisällä tietokannan **Internal**-osoitetta. Älä tallenna tunnuksia Dockerfileen tai lähdekoodiin.
5. **Aseta Health Check Path** (Settings): `/tapahtumat`. Tapahtumien listaus on julkinen, joten se toimii ilman kirjautumista.
6. **Deploy ja lokin seuranta:** onnistuneessa käynnistyksessä lokissa näkyy `The following 1 profile is active: "prod"`, `HikariPool-1 - Start completed` ja `Started ProjektiApplication`.

Haaran vaihto: Settings → Build & Deploy → Branch. Uusia ominaisuuksia voi testata feature-haarassa vaihtamalla Renderin haaran siihen, ja onnistuneen testin jälkeen haara mergetään `main`iin ja Renderin haara vaihdetaan takaisin `main`iin.

### Yleisimmät käynnistysvirheet

| Lokissa | Syy |
|---|---|
| `database "..." does not exist` | `DB_URL`:n tietokannan nimi on väärä |
| `password authentication failed` | väärä `DB_USERNAME` tai `DB_PASSWORD` |
| `UnknownHostException` | väärä hostname tai tietokanta ja palvelu ovat eri alueilla |
| `Driver org.postgresql.Driver claims to not accept jdbcUrl` | `DB_URL` ei ala `jdbc:postgresql://` |
| `Could not resolve placeholder 'DB_URL'` | ympäristömuuttuja puuttuu |
| `BOOTSTRAP_ADMIN_EMAIL ja ... on asetettava yhdessä` | vain toinen bootstrap-muuttujista on asetettu |
| `No open ports detected` | sovellus kaatui käynnistyksessä, etsi syy lokin `Caused by:` -riveiltä |

### Huomioita Renderin ilmaisesta tasosta

- Web service nukahtaa noin 15 minuutin käyttämättömyyden jälkeen, ja ensimmäinen pyyntö herätyksen jälkeen voi kestää noin minuutin.
- Ilmaisella PostgreSQL-tietokannalla on voimassaoloaika (aiemmin noin 30 päivää), minkä jälkeen se poistetaan, ellei päivitä maksulliseen. Tarkista nykyiset ehdot Renderin hinnoittelusta.
- Juuriosoite `/` ei ole sovelluksessa määritelty, ja se palauttaa JSON-virheen. Sovellus on tällä hetkellä pelkkä REST-rajapinta. Kokeile esimerkiksi osoitetta `/tapahtumat`.
- `spring.jpa.hibernate.ddl-auto=update` luo ja päivittää taulut automaattisesti. Se sopii kehitykseen ja harjoitusprojektiin, mutta ei poista vanhoja sarakkeita eikä sovellu tuotantokäyttöön ilman erillistä migraatiotyökalua.

## Rajapinnan kokeilu

Rajapintaa voi kokeilla Postmanilla tai komentorivillä. Suojatuissa pyynnöissä käytetään **Authorization → Basic Auth** -kirjautumista sovelluksen käyttäjän sähköpostilla ja salasanalla (ei tietokannan tunnuksilla).

### Pikatesti Renderiä vastaan (PowerShell)

Ensin muuttujat omilla arvoilla, kukin omalle rivilleen:

```powershell
$url = "https://palvelun-nimi.onrender.com"
$admin = "admin@projekti.fi:SALASANA"
```

```powershell
# Julkinen lista: odotus 200
curl.exe -i "$url/tapahtumat"

# Kirjautumaton ei pääse suojattuun reittiin: odotus 401
curl.exe -i "$url/myynnit"

# Ylläpitäjän kirjautuminen: odotus 200
curl.exe -i -u $admin "$url/auth/me"

# Väärä salasana: odotus 401
curl.exe -i -u "admin@projekti.fi:vaara" "$url/auth/me"

# Tapahtuman luonti ylläpitäjänä: odotus 200 tai 201
curl.exe -i -u $admin -X POST "$url/tapahtumat" -H "Content-Type: application/json" -d '{\"nimi\":\"Rendertesti\",\"tyyppi\":\"Konsertti\",\"aika\":\"2026-12-01T18:00\",\"kaupunki\":\"Helsinki\",\"paikka\":\"Halli\",\"maara\":50}'
```

Käytä komentoa `curl.exe`, ei pelkkää `curl`, koska PowerShellissä `curl` on toisen komennon alias.

### Datan säilyminen

Tietokannan pysyvyyden voi varmistaa luomalla testidatan (esim. tapahtuma ja rekisteröity käyttäjä), käynnistämällä palvelun uudelleen Renderissä (Manual Deploy → Restart service) ja tarkistamalla, että tapahtuma näkyy `GET /tapahtumat` -listassa ja käyttäjä pääsee yhä kirjautumaan. Näin varmistettiin, että data säilyy PostgreSQL:ssä eikä katoa uudelleenkäynnistyksessä, kuten aiemmin muistinvaraisessa H2:ssa.

## Tiimityö

- **Paikallinen kehitys:** jokainen ajaa projektin omalla koneellaan oletuksena H2:lla ilman Render-tietoja. Jokaisella on oma paikallinen kanta, ja data ei näy muille.
- **Yhteinen Render-palvelu:** muut voivat lisätä dataa Renderin PostgreSQL:ään rajapinnan kautta lähettämällä pyyntöjä Render-osoitteeseen. He eivät tarvitse tietokannan salasanaa. Ylläpitäjä luo heille omat tunnukset kutsulla `POST /auth/users`, jolloin ylläpitäjän salasanaa ei tarvitse jakaa:

  ```json
  {"nimi":"Kalle","sahkoposti":"kalle@projekti.fi","salasana":"PitkaSalasana123","rooli":"YLLAPITAJA"}
  ```

- **Oma Render-ympäristö:** jokainen voi myös tehdä oman PostgreSQL-tietokannan ja web servicen samasta koodista ylläpitämällä omia ympäristömuuttujiaan. Tietokannan nimi on jokaisella eri, joten se pitää kopioida omasta tietokannasta. Ilmaisen tason rajoitusten takia jokaisella pitää olla oma Render-tili.
- Tietokannan salasanoja ja bootstrap-tunnuksia ei commitoida repositorioon. Ne ovat vain Renderin Environment-välilehdellä tai paikallisesti ympäristömuuttujissa.

## Testit

Projektissa on automaattiset testit (JUnit, Spring Boot Test, MockMvc). Ne käyttävät omaa muistinvaraista H2-tietokantaa, joten ne eivät koske paikalliseen H2-tiedostokantaan eivätkä Renderin PostgreSQL:ään riippumatta profiilista.

Testien oma konfiguraatio sijaitsee tiedostossa `src/test/resources/application.properties`:

```properties
spring.jpa.hibernate.ddl-auto=create-drop
```

Tietokannan osoite jätetään tarkoituksella pois, jolloin Spring Boot luo jokaiselle testikontekstille oman uniikin muistinvaraisen H2-kannan.

### Testiluokat

| Testiluokka | Mitä testataan |
|---|---|
| `ProjektiApplicationTests` | Sovelluskonteksti käynnistyy, lipun tallennus ja haku repositoryn kautta |
| `TapahtumaControllerTest` | Tapahtumarajapinnan virhetilanteet (400, 404, 405, 415) ja tapahtuman elinkaari repositoryn kautta |
| `TapahtumaHttpTest` | Tapahtumien onnistuneet toiminnot HTTP:n yli (luonti, päivitys 200, poisto 204) sekä oikeudet: lukeminen julkista, kirjautumaton 401, myyjä 403 |
| `AuthControllerTest` | Rekisteröinti, kirjautuminen, roolit, asiakkaan omat myynnit ja roolin korotuksen esto |
| `MyyntitapahtumaControllerTest` | Myynnin luonnin validointi ja virhetilanteet |
| `MyyntitapahtumaDtoTest` | Myynnin luonti-DTO:iden kentät, konstruktorit ja setterit |
| `BootstrapAdminTest` | Bootstrap-ylläpitäjän luonti, BCrypt-tiiviste, kirjautuminen ja uudelleenkäynnistyksen simulointi ilman duplikaattia |

Yhteensä testejä on 50, ja ne menevät kaikki läpi.

### Testien ajaminen

Aja projektin kansiossa (siellä missä `pom.xml` ja `mvnw.cmd` ovat):

```powershell
cd Projekti\projekti
.\mvnw.cmd test
```

Pelkkä yksi testiluokka ja `System.out`-tulostukset terminaalissa:

```powershell
.\mvnw.cmd '-Dtest=MyyntitapahtumaDtoTest' '-Dsurefire.useFile=false' test
```

### DTO-testit

`MyyntitapahtumaDtoTest` tarkistaa, että web-kerroksen DTO:t säilyttävät niille annetut tiedot oikein.

**MyyntiriviLuontiDto:** oletuskonstruktori luo olion oletusarvoilla (`lipputyyppiId = null`, `maara = 0`), parametrikonstruktori asettaa kentät ja getterit ja setterit toimivat.

**MyyntitapahtumaLuontiDto:** oletuskonstruktori luo olion oletusarvoilla (`tapahtumaId`, `kayttajaId` ja `rivit` ovat `null`), parametrikonstruktori asettaa kaikki kentät ja `rivit`-lista sekä sen sisältämä `MyyntiriviLuontiDto` säilyvät oikeina.

Testi tulostaa terminaaliin jokaisen testin nimen, käytetyt arvot ja onnistumisilmoituksen. Testin onnistuminen perustuu silti JUnitin `assert`-tarkistuksiin.

### Tunnetut puutteet testeissä

- Lipun tarkastuksella ovella ei ole vielä API:a, joten sitä ei testata.
- Kapasiteettitarkistusta (tapahtuman kapasiteettia ei voi pienentää alle myytyjen lippujen) ei ole toteutettu, joten sitäkään ei testata.
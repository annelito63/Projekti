# Tapahtumien hallinta – REST API -dokumentaatio
 
Tämä dokumentti kuvaa palvelun rajapinnan siltä osin kuin se tarvitaan
**tapahtumien lisäämiseen, hakemiseen, muokkaamiseen ja poistamiseen**.
 
- **Versio:** 0.1 (Sprint 1)
- **Sovellus:** `ohjelmistoprojekti.projekti` (Spring Boot)
- **Kontrolleri:** `ohjelmistoprojekti.projekti.web.TapahtumaController`
- **Tietokanta:** H2 (in-memory, kehitysaikainen – data ei ole persistenttiä)
**Toteutustilanne**
 
| Toiminto | Tila |
|---|---|
| Kaikkien tapahtumien haku | Toteutettu |
| Yksittäisen tapahtuman haku | Toteutettu |
| Tapahtuman lisäys | Toteutettu |
| Tapahtuman muokkaus (PUT / PATCH) | Suunniteltu |
| Tapahtuman poisto (DELETE) | Suunniteltu |
| Suodatus query-parametreilla | Suunniteltu |
 
---
 
## Sisällys
 
- [Base-URL](#base-url)
- [Yleiset periaatteet](#yleiset-periaatteet)
- [Tietomalli: Tapahtuma](#tietomalli-tapahtuma)
- [Endpointit](#endpointit)
  - [GET /tapahtumat](#get-tapahtumat)
  - [GET /tapahtumat/{id}](#get-tapahtumatid)
  - [POST /tapahtumat](#post-tapahtumat)
  - [PUT /tapahtumat/{id}](#put-tapahtumatid)
  - [PATCH /tapahtumat/{id}](#patch-tapahtumatid)
  - [DELETE /tapahtumat/{id}](#delete-tapahtumatid)
- [Paluukoodit](#paluukoodit)
- [Tietokanta kehityksessä](#tietokanta-kehityksessä)
- [Linkit](#linkit)
---
 
## Base-URL
 
| Ympäristö | Base-URL |
|---|---|
| Kehitys (paikallinen) | `http://localhost:8080` |
| Tuotanto | *(ei vielä määritelty)* |
 
Kaikki alla olevat polut ovat suhteessa base-URLiin, esim.
`http://localhost:8080/tapahtumat`.
 
> **Huom.** `TapahtumaController`-luokassa ei ole luokkatason
> `@RequestMapping`-annotaatiota, joten polut ovat sellaisenaan juuressa. Jos
> rajapinnalle lisätään myöhemmin `/api`-etuliite, base-URL muuttuu muotoon
> `http://localhost:8080/api` ja polut pysyvät muuten samoina.
 
---
 
## Yleiset periaatteet
 
| Asia | Kuvaus |
|---|---|
| Tietomuoto | JSON (`application/json`) |
| Merkistö | UTF-8 |
| Pyynnön `Content-Type` | `application/json` (POST, PUT, PATCH) |
| Vastauksen `Content-Type` | `application/json` |
| Id:n generointi | Tietokanta generoi `tapahtumaid`-arvon (`GenerationType.AUTO`) |
| Autentikointi | Ei käytössä tässä vaiheessa |
 
---
 
## Tietomalli: Tapahtuma
 
Vastaa luokkaa `ohjelmistoprojekti.projekti.model.Tapahtuma`.
 
| Kenttä | Tyyppi | Pakollinen (POST) | Kuvaus |
|---|---|---|---|
| `tapahtumaid` | Long | ei | Tapahtuman yksilöivä tunniste. Palvelin generoi. **Pyynnössä annettu arvo ohitetaan** (kontrolleri nollaa sen ennen tallennusta). |
| `nimi` | String | kyllä | Tapahtuman nimi, esim. "Kesäkonsertti" |
| `tyyppi` | String | kyllä | Tapahtuman tyyppi, esim. "Konsertti", "Teatteri", "Urheilu" |
| `aika` | String | kyllä | Tapahtuman ajankohta merkkijonona |
| `kaupunki` | String | kyllä | Kaupunki, jossa tapahtuma järjestetään |
| `paikka` | String | kyllä | Tapahtumapaikka kaupungin sisällä |
| `kuvaus` | String | ei | Vapaamuotoinen kuvaus |
| `maara` | int | kyllä | Tapahtuman lippujen kokonaismäärä. Jos kenttää ei anneta, arvoksi tulee `0`. |
| `liput` | Lippu[] | ei | Tapahtumaan liittyvät liput (yksi-moneen-suhde). Palautetaan vastauksissa. |
 
> **Huom. `aika`-kentästä.** Kenttä on tällä hetkellä tyyppiä `String`, joten
> palvelin ei validoi sen muotoa eikä tapahtumia voi lajitella tai suodattaa
> ajan perusteella tietokantatasolla. Sovitaan tiimissä kiinteä muoto (esim.
> ISO 8601 `2026-06-12T19:00:00`) ja vaihdetaan kenttä myöhemmin tyypiksi
> `LocalDateTime`. Tämä on kirjattu työjonoon.
 
### Esimerkki JSON-esityksestä
 
```json
{
  "tapahtumaid": 1,
  "nimi": "Kesäkonsertti",
  "tyyppi": "Konsertti",
  "aika": "2026-06-12T19:00:00",
  "kaupunki": "Helsinki",
  "paikka": "Kaisaniemen puisto",
  "kuvaus": "Ulkoilmakonsertti puistossa",
  "maara": 500,
  "liput": []
}
```
 
### Liittyvä tietomalli: Lippu
 
`liput`-listan alkiot ovat `Lippu`-olioita. Lipun `tapahtumaid`-viittaus
takaisin tapahtumaan jätetään serialisoinnissa pois
(`@JsonIgnoreProperties`), joten vastaus ei mene silmukkaan.
 
| Kenttä | Tyyppi | Kuvaus |
|---|---|---|
| `id` | Long | Lipun tunniste |
| `nimi` | String | Lipun nimi |
| `kuvaus` | String | Lipun kuvaus |
| `hinta` | double | Lipun hinta euroina |
| `lipputyyppi` | Lipputyyppi | Lipputyyppi, johon lippu kuuluu |
 
Lippujen oma rajapinta (`/liput`) dokumentoidaan erikseen, kun se
toteutetaan.
 
---
 
## Endpointit
 
| Metodi | Polku | Kuvaus | Tila |
|---|---|---|---|
| GET | `/tapahtumat` | Hae kaikki tapahtumat | Toteutettu |
| GET | `/tapahtumat/{id}` | Hae yksittäinen tapahtuma | Toteutettu |
| POST | `/tapahtumat` | Lisää uusi tapahtuma | Toteutettu |
| PUT | `/tapahtumat/{id}` | Korvaa tapahtuman tiedot | Suunniteltu |
| PATCH | `/tapahtumat/{id}` | Päivitä osa tapahtuman tiedoista | Suunniteltu |
| DELETE | `/tapahtumat/{id}` | Poista tapahtuma | Suunniteltu |
 
---
 
### GET /tapahtumat
 
Palauttaa listan kaikista tapahtumista.
 
**Metodi ja polku**
 
```
GET /tapahtumat
```
 
**Polkuparametrit**
 
Ei polkuparametreja.
 
**Query-parametrit**
 
Tällä hetkellä ei tuettuja query-parametreja. Seuraavat on suunniteltu ja
niille on jo olemassa hakumetodit `TapahtumaRepository`-rajapinnassa:
 
| Parametri | Tyyppi | Pakollinen | Oletus | Kuvaus | Tila |
|---|---|---|---|---|---|
| `nimi` | String | ei | – | Palauttaa vain tapahtumat, joiden nimi täsmää (`findByNimi`) | Suunniteltu |
| `paikka` | String | ei | – | Palauttaa vain tapahtumat annetussa paikassa (`findByPaikka`) | Suunniteltu |
| `kaupunki` | String | ei | – | Palauttaa vain tapahtumat annetussa kaupungissa | Suunniteltu |
 
**Pyynnön sisältö**
 
Ei sisältöä.
 
**Vastaus: 200 OK**
 
```json
[
  {
    "tapahtumaid": 1,
    "nimi": "Kesäkonsertti",
    "tyyppi": "Konsertti",
    "aika": "2026-06-12T19:00:00",
    "kaupunki": "Helsinki",
    "paikka": "Kaisaniemen puisto",
    "kuvaus": "Ulkoilmakonsertti puistossa",
    "maara": 500,
    "liput": []
  },
  {
    "tapahtumaid": 2,
    "nimi": "Klassikkonäytelmä",
    "tyyppi": "Teatteri",
    "aika": "2026-07-01T18:30:00",
    "kaupunki": "Tampere",
    "paikka": "Kaupunginteatteri",
    "kuvaus": "Suuri näyttämö",
    "maara": 200,
    "liput": []
  }
]
```
 
Jos tapahtumia ei ole, vastaus on `200 OK` ja tyhjä lista `[]`.
 
**Esimerkkikutsu**
 
```bash
curl http://localhost:8080/tapahtumat
```
 
---
 
### GET /tapahtumat/{id}
 
Palauttaa yhden tapahtuman tunnisteen perusteella.
 
**Metodi ja polku**
 
```
GET /tapahtumat/{id}
```
 
**Polkuparametrit**
 
| Parametri | Tyyppi | Pakollinen | Kuvaus |
|---|---|---|---|
| `id` | Long | kyllä | Haettavan tapahtuman `tapahtumaid` |
 
**Query-parametrit**
 
Ei query-parametreja.
 
**Pyynnön sisältö**
 
Ei sisältöä.
 
**Vastaus: 200 OK**
 
```json
{
  "tapahtumaid": 1,
  "nimi": "Kesäkonsertti",
  "tyyppi": "Konsertti",
  "aika": "2026-06-12T19:00:00",
  "kaupunki": "Helsinki",
  "paikka": "Kaisaniemen puisto",
  "kuvaus": "Ulkoilmakonsertti puistossa",
  "maara": 500,
  "liput": []
}
```
 
**Vastaus: 404 Not Found**
 
Tapahtumaa annetulla id:llä ei löytynyt. Vastauksella ei ole sisältöä.
 
**Esimerkkikutsu**
 
```bash
curl http://localhost:8080/tapahtumat/1
```
 
---
 
### POST /tapahtumat
 
Luo uuden tapahtuman. Palvelin generoi `tapahtumaid`-arvon; pyynnössä
mahdollisesti annettu id ohitetaan.
 
**Metodi ja polku**
 
```
POST /tapahtumat
```
 
**Polkuparametrit**
 
Ei polkuparametreja.
 
**Query-parametrit**
 
Ei query-parametreja.
 
**Pyynnön otsakkeet**
 
```
Content-Type: application/json
```
 
**Pyynnön sisältö**
 
```json
{
  "nimi": "Kesäkonsertti",
  "tyyppi": "Konsertti",
  "aika": "2026-06-12T19:00:00",
  "kaupunki": "Helsinki",
  "paikka": "Kaisaniemen puisto",
  "kuvaus": "Ulkoilmakonsertti puistossa",
  "maara": 500
}
```
 
**Vastaus: 201 Created**
 
Palauttaa luodun tapahtuman generoidulla tunnisteella.
 
```json
{
  "tapahtumaid": 1,
  "nimi": "Kesäkonsertti",
  "tyyppi": "Konsertti",
  "aika": "2026-06-12T19:00:00",
  "kaupunki": "Helsinki",
  "paikka": "Kaisaniemen puisto",
  "kuvaus": "Ulkoilmakonsertti puistossa",
  "maara": 500,
  "liput": null
}
```
 
**Vastaus: 400 Bad Request**
 
JSON on syntaktisesti virheellinen tai kentän tyyppi on väärä (esim. `maara`
merkkijonona). Kenttäkohtainen validointi (`@NotBlank`, `@Min`) ja sen
tuottamat virheilmoitukset lisätään seuraavassa sprintissä.
 
**Esimerkkikutsu**
 
```bash
curl -X POST http://localhost:8080/tapahtumat \
  -H "Content-Type: application/json" \
  -d '{
        "nimi": "Kesäkonsertti",
        "tyyppi": "Konsertti",
        "aika": "2026-06-12T19:00:00",
        "kaupunki": "Helsinki",
        "paikka": "Kaisaniemen puisto",
        "kuvaus": "Ulkoilmakonsertti puistossa",
        "maara": 500
      }'
```
 
---
 
### PUT /tapahtumat/{id}
 
 
Korvaa olemassa olevan tapahtuman tiedot kokonaan pyynnön sisällöllä.
Kentät, joita ei anneta, tyhjennetään.
 
**Metodi ja polku**
 
```
PUT /tapahtumat/{id}
```
 
**Polkuparametrit**
 
| Parametri | Tyyppi | Pakollinen | Kuvaus |
|---|---|---|---|
| `id` | Long | kyllä | Muokattavan tapahtuman `tapahtumaid` |
 
**Query-parametrit**
 
Ei query-parametreja.
 
**Pyynnön sisältö**
 
```json
{
  "nimi": "Kesäkonsertti 2026",
  "tyyppi": "Konsertti",
  "aika": "2026-06-12T19:30:00",
  "kaupunki": "Helsinki",
  "paikka": "Kaisaniemen puisto",
  "kuvaus": "Ulkoilmakonsertti, päivitetty ohjelmisto",
  "maara": 600
}
```
 
**Vastaukset**
 
| Koodi | Tilanne | Sisältö |
|---|---|---|
| `200 OK` | Päivitys onnistui | Päivitetty tapahtuma JSON-muodossa |
| `400 Bad Request` | Virheellinen sisältö | Virheilmoitus |
| `404 Not Found` | Tapahtumaa ei löytynyt | – |
 
**Esimerkkikutsu**
 
```bash
curl -X PUT http://localhost:8080/tapahtumat/1 \
  -H "Content-Type: application/json" \
  -d '{ "nimi": "Kesäkonsertti 2026", "tyyppi": "Konsertti", "aika": "2026-06-12T19:30:00", "kaupunki": "Helsinki", "paikka": "Kaisaniemen puisto", "kuvaus": "Päivitetty ohjelmisto", "maara": 600 }'
```
 
---
 
### PATCH /tapahtumat/{id}
 
*Suunniteltu – ei vielä toteutettu.*
 
Päivittää vain pyynnössä annetut kentät. Muut kentät säilyvät ennallaan.
 
**Metodi ja polku**
 
```
PATCH /tapahtumat/{id}
```
 
**Polkuparametrit**
 
| Parametri | Tyyppi | Pakollinen | Kuvaus |
|---|---|---|---|
| `id` | Long | kyllä | Muokattavan tapahtuman `tapahtumaid` |
 
**Query-parametrit**
 
Ei query-parametreja.
 
**Pyynnön sisältö**
 
```json
{
  "maara": 750
}
```
 
**Vastaukset**
 
| Koodi | Tilanne | Sisältö |
|---|---|---|
| `200 OK` | Päivitys onnistui | Päivitetty tapahtuma JSON-muodossa |
| `400 Bad Request` | Virheellinen sisältö | Virheilmoitus |
| `404 Not Found` | Tapahtumaa ei löytynyt | – |
 
**Esimerkkikutsu**
 
```bash
curl -X PATCH http://localhost:8080/tapahtumat/1 \
  -H "Content-Type: application/json" \
  -d '{ "maara": 750 }'
```
 
---
 
### DELETE /tapahtumat/{id}
 
 
Poistaa tapahtuman tunnisteen perusteella.
 
**Metodi ja polku**
 
```
DELETE /tapahtumat/{id}
```
 
**Polkuparametrit**
 
| Parametri | Tyyppi | Pakollinen | Kuvaus |
|---|---|---|---|
| `id` | Long | kyllä | Poistettavan tapahtuman `tapahtumaid` |
 
**Query-parametrit**
 
Ei query-parametreja.
 
**Pyynnön sisältö**
 
Ei sisältöä.
 
**Vastaukset**
 
| Koodi | Tilanne | Sisältö |
|---|---|---|
| `204 No Content` | Poisto onnistui | – |
| `404 Not Found` | Tapahtumaa ei löytynyt | – |
| `409 Conflict` | Tapahtumaa ei voi poistaa, koska siihen liittyy myytyjä lippuja | Virheilmoitus |
 
> **Huom.** `Tapahtuma.liput` on määritelty `cascade = CascadeType.ALL`, joten
> tapahtuman poisto poistaa oletuksena myös siihen liittyvät liput. Tiimin on
> päätettävä, sallitaanko poisto lainkaan, jos lippuja on jo myyty – tällöin
> vastaus on `409 Conflict`.
 
**Esimerkkikutsu**
 
```bash
curl -X DELETE http://localhost:8080/tapahtumat/1
```
 
---
 
## Paluukoodit
 
| Koodi | Merkitys | Milloin |
|---|---|---|
| `200 OK` | Pyyntö onnistui | GET, PUT, PATCH |
| `201 Created` | Resurssi luotiin | POST |
| `204 No Content` | Onnistui, ei palautettavaa sisältöä | DELETE |
| `400 Bad Request` | Virheellinen pyyntö tai puuttuva pakollinen kenttä | POST, PUT, PATCH |
| `404 Not Found` | Pyydettyä resurssia ei löytynyt | GET, PUT, PATCH, DELETE yksittäiselle id:lle |
| `409 Conflict` | Toiminto on ristiriidassa tietokannan nykytilan kanssa | DELETE |
| `500 Internal Server Error` | Odottamaton palvelinvirhe | – |
 
**Suunniteltu virhevastauksen rakenne** *(toteutetaan validoinnin yhteydessä)*
 
```json
{
  "timestamp": "2026-09-16T10:15:30",
  "status": 404,
  "error": "Not Found",
  "message": "Tapahtumaa id:llä 42 ei löytynyt",
  "path": "/tapahtumat/42"
}
```
 
---
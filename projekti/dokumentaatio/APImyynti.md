# Myyntitapahtumien hallinta – REST API -dokumentaatio
 
Tämä dokumentti kuvaa palvelun rajapinnan siltä osin kuin se tarvitaan
**myyntitapahtumien lisäämiseen, hakemiseen, muokkaamiseen ja poistamiseen**.
 
- **Versio:** 0.1 (Sprint 1)
- **Sovellus:** `ohjelmistoprojekti.projekti` (Spring Boot)
- **Kontrolleri:** `ohjelmistoprojekti.projekti.web.MyyntitapahtumaController`
- **Tietokanta:** H2 (in-memory, kehitysaikainen – data ei ole persistenttiä)

**Toteutustilanne**
 
| Toiminto | Tila |
|---|---|
| Kaikkien tapahtumien haku | Suunniteltu |
| Yksittäisen tapahtuman haku | Suunniteltu |
| Tapahtuman lisäys | Suunniteltu |
| Tapahtuman muokkaus (PUT / PATCH) | Suunniteltu |
| Tapahtuman poisto (DELETE) | Suunniteltu |
| Suodatus query-parametreilla | Suunniteltu |
 
 ---
 
## Sisällys
 
- [Base-URL](#base-url)
- [Yleiset periaatteet](#yleiset-periaatteet)
- [Tietomalli: Tapahtuma](#tietomalli-tapahtuma)
- [Endpointit](#endpointit)
  - [GET /myynnit](#get-myynnit)
  - [GET /myynnit/{id}](#get-myynnitid)
  - [POST /myynnit](#post-myynnit)
  - [PUT /myynnit/{id}](#put-myynnitid)
  - [PATCH /myynnit/{id}](#patch-myynnitid)
  - [DELETE /myynnit/{id}](#delete-myynnitid)
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
`http://localhost:8080/myynnit`.
 
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
| Id:n generointi | Tietokanta generoi `myyntiId`-arvon (`GenerationType.AUTO`) |
| Autentikointi | Ei käytössä tässä vaiheessa |
 
---

## Tietomalli: Myyntitapahtuma
 
Vastaa luokkaa `ohjelmistoprojekti.projekti.model.Myyntitapahtuma`.
 
| Kenttä | Tyyppi | Pakollinen (POST) | Kuvaus |
|---|---|---|---|
| `myyntiId` | Long | ei | Myyntitapahtuman yksilöivä tunniste. Palvelin generoi. **Pyynnössä annettu arvo ohitetaan** (kontrolleri nollaa sen ennen tallennusta). |
| `tapahtuma` | Tapahtuma | kyllä | Tapahtuman ID |
| `kayttaja` | Kayttaja | kyllä | Käyttäjän ID |
| `myyntiaika` | LocalDateTime | kyllä | Myyntitapahtuman ajankohta |
| `kokonaissumma` | double | kyllä | Myyntihinnan kokonaissumma |
| `myyntirivit` | Myyntirivi[] | ei | Myyntitapahtumaan liittyvät myyntirivit (yksi-moneen-suhde). Palautetaan vastauksissa. |
 
 
### Esimerkki JSON-esityksestä
 
```json
{
  "myyntiId": 1,
  "tapahtuma": 1,
  "kayttaja": 1,
  "myyntiaika": "2026-06-12T19:00:00",
  "kokonaissumma": 50.95,
  "myyntirivit": []
}
```
 
### Liittyvät tietomallit: Tapahtuma, Kayttaja, Myyntirivi
 
Viittaukset toisiin luokkiin jätetään serialisoinnissa pois
(`@JsonIgnoreProperties`), joten vastaus ei mene silmukkaan.

Käsittelyä varten luotu MyyntitapahtumaLuontiDTO.java ja MyyntiriviLuontiDTO.java 
---
 
## Endpointit
 
| Metodi | Polku | Kuvaus | Tila |
|---|---|---|---|
| GET | `/myynnit` | Hae kaikki myyntitapahtumat | Suunniteltu |
| GET | `/myynnit/{id}` | Hae yksittäinen myyntitapahtuma | Suunniteltu |
| POST | `/myynnit` | Lisää uusi myyntitapahtuma | Suunniteltu |
| PUT | `/myynnit/{id}` | Korvaa myyntitapahtuman tiedot | Suunniteltu |
| PATCH | `/myynnit/{id}` | Päivitä osa myyntitapahtuman tiedoista | Suunniteltu |
| DELETE | `/myynnit/{id}` | Poista myyntitapahtuma | Suunniteltu |
 
---
### GET /myynnit
 
Palauttaa listan kaikista myyntitapahtumista.
 
**Metodi ja polku**
 
```
GET /myynnit
```
 
**Polkuparametrit**
 
Ei polkuparametreja.
 
**Query-parametrit**
 
Tällä hetkellä ei tuettuja query-parametreja. Seuraavat on suunniteltu ja
niille on jo olemassa hakumetodit `TapahtumaRepository`-rajapinnassa:
 
| Parametri | Tyyppi | Pakollinen | Oletus | Kuvaus | Tila |
|---|---|---|---|---|---|
| `kayttaja` | String | ei | – | Palauttaa vain myyntitapahtumat, joiden nimi täsmää (`findByKayttaja`) | Suunniteltu |
| `tapahtuma` | String | ei | – | Palauttaa vain myyntitapahtumat tietyssä tapahtumassa (`findByTapahtuma`) | Suunniteltu |

 
**Pyynnön sisältö**
 
Ei sisältöä.
 
**Vastaus: 200 OK**
 
```json
[
  {
    "myyntiid": 1,
    "tapahtuma": 1,
    "kayttaja": 1,
    "myyntiaika": "2026-06-12T19:00:00",
    "kokonaissumma": 50.95,
    "myyntirivit": []
  },
  {
    "myyntiid": 2,
    "tapahtuma": 2,
    "kayttaja": 1,
    "myyntiaika": "2026-07-18T12:00:00",
    "kokonaissumma": 39.95,
    "myyntirivit": []
  }
]
```
 
Jos myyntitapahtumia ei ole, vastaus on `200 OK` ja tyhjä lista `[]`.
 
**Esimerkkikutsu**
 
```bash
curl http://localhost:8080/myynnit
```
 
---
 
### GET /myynnit/{id}
 
Palauttaa yhden myyntitapahtuman tunnisteen perusteella.
 
**Metodi ja polku**
 
```
GET /myynnit/{id}
```
 
**Polkuparametrit**
 
| Parametri | Tyyppi | Pakollinen | Kuvaus |
|---|---|---|---|
| `id` | Long | kyllä | Haettavan tapahtuman `myyntiId` |
 
**Query-parametrit**
 
Ei query-parametreja.
 
**Pyynnön sisältö**
 
Ei sisältöä.
 
**Vastaus: 200 OK**
 
```json
{
  "myyntiid": 1,
  "tapahtuma": 1,
  "kayttaja": 1,
  "myyntiaika": "2026-06-12T19:00:00",
  "kokonaissumma": 50.95,
  "myyntirivit": []
}
```
 
**Vastaus: 404 Not Found**
 
Tapahtumaa annetulla id:llä ei löytynyt. Vastauksella ei ole sisältöä.
 
**Esimerkkikutsu**
 
```bash
curl http://localhost:8080/myynnit/1
```
 
---
 
### POST /myynnit
 
Luo uuden tapahtuman. Palvelin generoi `myyntiId`-arvon; pyynnössä
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
  "myyntiid": 1,
  "tapahtuma": 1,
  "kayttaja": 1,
  "myyntiaika": "2026-06-12T19:00:00",
  "kokonaissumma": 50.95,
  "myyntirivit": []
}
```
 
**Vastaus: 201 Created**
 
Palauttaa luodun tapahtuman generoidulla tunnisteella.
 
```json
{
  "myyntiid": 1,
  "tapahtuma": 1,
  "kayttaja": 1,
  "myyntiaika": "2026-06-12T19:00:00",
  "kokonaissumma": 50.95,
  "myyntirivit": null
}
```
 
**Vastaus: 400 Bad Request**
 
JSON on syntaktisesti virheellinen tai kentän tyyppi on väärä (esim. `maara`
merkkijonona). Kenttäkohtainen validointi (`@NotBlank`, `@Min`) ja sen
tuottamat virheilmoitukset lisätään seuraavassa sprintissä.
 
**Esimerkkikutsu**
 
```bash
curl -X POST http://localhost:8080/myynnit \
  -H "Content-Type: application/json" \
  -d '{
        "myyntiid": 1,
        "tapahtuma": 1,
        "kayttaja": 1,
        "myyntiaika": "2026-06-12T19:00:00",
        "kokonaissumma": 50.95,
        "myyntirivit": []
      }'
```
 
---
 
### PUT /myynnit/{id}
 
 
Korvaa olemassa olevan myyntitapahtuman tiedot kokonaan pyynnön sisällöllä.
Kentät, joita ei anneta, tyhjennetään.
 
**Metodi ja polku**
 
```
PUT /myynnit/{id}
```
 
**Polkuparametrit**
 
| Parametri | Tyyppi | Pakollinen | Kuvaus |
|---|---|---|---|
| `id` | Long | kyllä | Muokattavan myyntitapahtuman `myyntiId` |
 
**Query-parametrit**
 
Ei query-parametreja.
 
**Pyynnön sisältö**
 
```json
{
"myyntiid": 1,
"tapahtuma": 3,
"kayttaja": 5,
"myyntiaika": "2026-08-12T19:00:00",
"kokonaissumma": 74.95,
"myyntirivit": []
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
curl -X PUT http://localhost:8080/myynnit/1 \
  -H "Content-Type: application/json" \
  -d '{ "myyntiid": 1, "tapahtuma": 3, "kayttaja": 5, "myyntiaika": "2026-08-12T19:00:00", "kokonaissumma": 74.95 }'
```
 
---
 
### PATCH /tapahtumat/{id}
 
*Suunniteltu – ei vielä toteutettu.*
 
Päivittää vain pyynnössä annetut kentät. Muut kentät säilyvät ennallaan.
 
**Metodi ja polku**
 
```
PATCH /myynnit/{id}
```
 
**Polkuparametrit**
 
| Parametri | Tyyppi | Pakollinen | Kuvaus |
|---|---|---|---|
| `id` | Long | kyllä | Muokattavan myyntitapahtuman `myyntiId` |
 
**Query-parametrit**
 
Ei query-parametreja.
 
**Pyynnön sisältö**
 
```json
{
  "kokonaissumma": 59.95
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
curl -X PATCH http://localhost:8080/myynnit/1 \
  -H "Content-Type: application/json" \
  -d '{ "kokonaissumma": 59.95 }'
```
 
---
 
### DELETE /myynnit/{id}
 
 
Poistaa tapahtuman tunnisteen perusteella.
 
**Metodi ja polku**
 
```
DELETE /myynnit/{id}
```
 
**Polkuparametrit**
 
| Parametri | Tyyppi | Pakollinen | Kuvaus |
|---|---|---|---|
| `id` | Long | kyllä | Poistettavan tapahtuman `myyntiId` |
 
**Query-parametrit**
 
Ei query-parametreja.
 
**Pyynnön sisältö**
 
Ei sisältöä.
 
**Vastaukset**
 
| Koodi | Tilanne | Sisältö |
|---|---|---|
| `204 No Content` | Poisto onnistui | – |
| `404 Not Found` | Myyntitapahtumaa ei löytynyt | – |
| `409 Conflict` | Myyntitapahtumaa ei voi poistaa | Virheilmoitus |

 
**Esimerkkikutsu**
 
```bash
curl -X DELETE http://localhost:8080/myynnit/1
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
  "message": "Myyntitapahtumaa id:llä 42 ei löytynyt",
  "path": "/myynnit/42"
}
```
 
---

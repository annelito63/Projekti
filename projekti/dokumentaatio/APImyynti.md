# Myyntitapahtumien hallinta – REST API -dokumentaatio
 
Tämä dokumentti kuvaa toteutetut myyntitapahtumien haku- ja lisäysrajapinnat.
Myyntitapahtumien muokkaus ja poisto eivät ole toteutettuja rajapintoja.
 
- **Versio:** 0.1 (Sprint 1)
- **Sovellus:** `ohjelmistoprojekti.projekti` (Spring Boot)
- **Kontrolleri:** `ohjelmistoprojekti.projekti.web.MyyntitapahtumaController`
- **Tietokanta:** H2 (in-memory, kehitysaikainen – data ei ole persistenttiä)

**Toteutustilanne**
 
| Toiminto | Tila |
|---|---|
| Myyntitapahtumien haku (roolin sallimassa laajuudessa) | Toteutettu |
| Yksittäisen myyntitapahtuman haku | Toteutettu |
| Myyntitapahtuman lisäys | Toteutettu |
| Myyntitapahtuman muokkaus (PUT / PATCH) | Suunniteltu |
| Myyntitapahtuman poisto (DELETE) | Suunniteltu |
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
 
> **Huom.** `MyyntitapahtumaController`-luokassa ei ole luokkatason
> `@RequestMapping`-annotaatiota, joten polut ovat sellaisenaan juuressa. Jos
> rajapinnalle lisätään myöhemmin `/api`-etuliite, base-URL muuttuu muotoon
> `http://localhost:8080/api` ja polut pysyvät muuten samoina.
 
---
 
## Yleiset periaatteet
 
| Asia | Kuvaus |
|---|---|
| Tietomuoto | JSON (`application/json`) |
| Merkistö | UTF-8 |
| Pyynnön `Content-Type` | `application/json` (POST) |
| Vastauksen `Content-Type` | `application/json` |
| Id:n generointi | Tietokanta generoi `myyntiId`-arvon (`GenerationType.AUTO`) |
| Autentikointi | HTTP Basic vaaditaan. `VERKKOKAUPAN_ASIAKAS` näkee vain omat myyntinsä; `MYYJA` ja `YLLAPITAJA` näkevät kaikki. |
 
---

Kirjautumisen, käyttöoikeuksien sekä HTTP-pyyntöjen ja automaattisten testien esimerkit ovat [autentikoinnin ohjeessa](./Autentikointi.md).

## Tietomalli: Myyntitapahtuma
 
Vastaa luokkaa `ohjelmistoprojekti.projekti.model.Myyntitapahtuma`.
 
| Kenttä | Tyyppi | Pakollinen (POST) | Kuvaus |
|---|---|---|---|
| `myyntiId` | Long | ei | Myyntitapahtuman yksilöivä tunniste. Palvelin generoi. **Pyynnössä annettu arvo ohitetaan** (kontrolleri nollaa sen ennen tallennusta). |
| `tapahtuma` | Tapahtuma | kyllä | Myyntiin liittyvä tapahtuma; luontipyynnössä annetaan `tapahtumaId`. |
| `kayttaja` | Kayttaja | kyllä | Myynnin omistaja; henkilökunnan pyynnössä annetaan `kayttajaId`, asiakkaan myynti liitetään automaattisesti kirjautuneeseen käyttäjään. |
| `myyntiaika` | LocalDateTime | kyllä | Palvelin asettaa ajan myynnin luonnissa. |
| `kokonaissumma` | double | kyllä | Palvelin laskee summan lipputyyppien hinnoista ja määristä. |
| `myyntirivit` | Myyntirivi[] | ei | Myyntiin liittyvät rivit; luontipyynnössä annetaan `rivit`. |
 
 
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
 
Vastaus sisältää tapahtuma-, käyttäjä- ja myyntirivitietoja sisäkkäisinä JSON-objekteina.
Niiden takaisinviittauksia rajataan `@JsonIgnoreProperties`-annotaatioilla,
jotta sarjallistaminen ei muodosta päättymätöntä silmukkaa. Salasanatiivistettä
ei palauteta API-vastauksessa.

Käsittelyä varten luotu MyyntitapahtumaLuontiDTO.java ja MyyntiriviLuontiDTO.java 
---
 
## Endpointit
 
| Metodi | Polku | Kuvaus | Tila |
|---|---|---|---|
| GET | `/myynnit` | Hae käyttäjän roolin sallimat myyntitapahtumat | Toteutettu |
| GET | `/myynnit/{id}` | Hae yksittäinen myyntitapahtuma, jos käyttäjällä on siihen oikeus | Toteutettu |
| POST | `/myynnit` | Lisää myyntitapahtuma | Toteutettu |
| PUT | `/myynnit/{id}` | Korvaa myyntitapahtuman tiedot | Suunniteltu |
| PATCH | `/myynnit/{id}` | Päivitä osa myyntitapahtuman tiedoista | Suunniteltu |
| DELETE | `/myynnit/{id}` | Poista myyntitapahtuma | Suunniteltu |
 
---
### GET /myynnit
 
Palauttaa myyntitapahtumat kirjautuneen käyttäjän roolin mukaisesti.
Asiakas saa vain omat myyntinsä; myyjä ja ylläpitäjä saavat kaikki myynnit.
 
**Metodi ja polku**
 
```
GET /myynnit
```
 
**Polkuparametrit**
 
Ei polkuparametreja.
 
**Query-parametrit**
 
Tällä hetkellä ei tuettuja query-parametreja. Repositoryssa on sisäiset
hakumetodit käyttäjän ja tapahtuman perusteella, mutta niitä ei ole julkaistu
query-parametreina:
 
| Parametri | Tyyppi | Pakollinen | Oletus | Kuvaus | Tila |
|---|---|---|---|---|---|
| `kayttaja` | Long | ei | – | Suodatus käyttäjän tunnisteella (`MyyntitapahtumaRepository.findByKayttaja`) | Suunniteltu |
| `tapahtuma` | Long | ei | – | Suodatus tapahtuman tunnisteella (`MyyntitapahtumaRepository.findByTapahtuma`) | Suunniteltu |

 
**Pyynnön sisältö**
 
Ei sisältöä.
 
**Vastaus: 200 OK**
 
```json
[
  {
    "myyntiId": 1,
    "tapahtuma": { "tapahtumaid": 1, "nimi": "Esimerkkitapahtuma" },
    "kayttaja": {
      "userId": 1,
      "nimi": "Aino Asiakas",
      "sahkoposti": "aino@example.fi",
      "rooli": "VERKKOKAUPAN_ASIAKAS"
    },
    "myyntiaika": "2026-06-12T19:00:00",
    "kokonaissumma": 50.95,
    "myyntirivit": [
      { "myyntiriviId": 1, "maara": 1, "yksikkohinta": 50.95, "rivisumma": 50.95 }
    ]
  }
]
```

Esimerkki on rakenteeltaan lyhennetty. Sisäkkäisten tapahtuma- ja
myyntiriviobjektien muut kentät riippuvat niiden tietomalleista.
 
Jos myyntitapahtumia ei ole, vastaus on `200 OK` ja tyhjä lista `[]`.
 
**Esimerkkikutsu**
 
```bash
curl -u "asiakas@example.fi:salasana" http://localhost:8080/myynnit
```

Yllä olevan pyynnön asiakasrooli saa vain omat myyntinsä. Myyjän tai
ylläpitäjän tunnuksilla sama reitti palauttaa kaikki myyntitapahtumat.
 
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
  "myyntiId": 1,
  "tapahtuma": { "tapahtumaid": 1, "nimi": "Esimerkkitapahtuma" },
  "kayttaja": {
    "userId": 1,
    "nimi": "Aino Asiakas",
    "sahkoposti": "aino@example.fi",
    "rooli": "VERKKOKAUPAN_ASIAKAS"
  },
  "myyntiaika": "2026-06-12T19:00:00",
  "kokonaissumma": 50.95,
  "myyntirivit": [
    { "myyntiriviId": 1, "maara": 1, "yksikkohinta": 50.95, "rivisumma": 50.95 }
  ]
}
```
 
**Vastaus: 404 Not Found**
 
Myyntiä ei löytynyt tai asiakkaalla ei ole oikeutta nähdä toisen käyttäjän
myyntiä. Vastauksella ei ole sisältöä.
 
**Esimerkkikutsu**
 
```bash
curl -u "asiakas@example.fi:salasana" http://localhost:8080/myynnit/1
```
 
---
 
### POST /myynnit
 
Luo uuden tapahtuman. Palvelin generoi `myyntiId`-arvon; pyynnössä
mahdollisesti annettu id ohitetaan.
 
**Metodi ja polku**
 
```
POST /myynnit
```
 
**Polkuparametrit**
 
Ei polkuparametreja.
 
**Query-parametrit**
 
Ei query-parametreja.
 
**Pyynnön otsakkeet**
 
```
Content-Type: application/json
```

Asiakas voi luoda myynnin vain itselleen. Henkilöstöroolien (`MYYJA`,
`YLLAPITAJA`) pyynnöissä `kayttajaId` on pakollinen. Asiakkaan pyynnössä
`kayttajaId` on vapaaehtoinen ja ohitetaan; myynti liitetään kirjautuneeseen
asiakkaaseen.
 
**Pyynnön sisältö**
 
```json
{
  "tapahtumaId": 1,
  "rivit": [
    {
      "lipputyyppiId": 1,
      "maara": 1
    }
  ]
}
```

Esimerkki on asiakkaan pyyntö. Myyjän tai ylläpitäjän pyyntöön lisätään
`"kayttajaId": 1`, joka viittaa olemassa olevaan käyttäjään.
 
**Vastaus: 201 Created**
 
Palauttaa luodun myyntitapahtuman generoidulla tunnisteella.
 
```json
{
  "myyntiId": 1,
  "tapahtuma": { "tapahtumaid": 1, "nimi": "Esimerkki" },
  "kayttaja": {
    "userId": 1,
    "nimi": "Aino Asiakas",
    "sahkoposti": "aino@example.fi",
    "rooli": "VERKKOKAUPAN_ASIAKAS"
  },
  "myyntiaika": "2026-06-12T19:00:00",
  "kokonaissumma": 50.95,
  "myyntirivit": []
}
```

Esimerkki näyttää vastauksen rakenteen lyhennettynä. Tapahtuma- ja
myyntiriviobjektit sisältävät lisäksi omat mallinsa kentät.
 
**Vastaus: 400 Bad Request**
 
JSON on syntaktisesti virheellinen, tapahtumatunniste tai rivit puuttuvat,
rivin lipputyyppitunniste puuttuu tai rivin määrä ei ole positiivinen.

**Vastaus: 404 Not Found**

Pyynnössä viitattua tapahtumaa, käyttäjää tai lipputyyppiä ei löydy.
 
**Esimerkkikutsu**
 
```bash
curl -X POST http://localhost:8080/myynnit \
  -u "asiakas@example.fi:salasana" \
  -H "Content-Type: application/json" \
  -d '{
        "tapahtumaId": 1,
        "rivit": [{ "lipputyyppiId": 1, "maara": 1 }]
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
 
### PATCH /myynnit/{id}
 
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
 
*Suunniteltu – ei vielä toteutettu.*

 
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
| `401 Unauthorized` | Kirjautuminen puuttuu tai tunnukset ovat virheelliset | Suojatut reitit |
| `403 Forbidden` | Kirjautuneella käyttäjällä ei ole toimintoon oikeuttavaa roolia | Roolisuojatut reitit |
| `404 Not Found` | Pyydettyä resurssia ei löytynyt | GET, PUT, PATCH, DELETE yksittäiselle id:lle |
| `409 Conflict` | Toiminto on ristiriidassa tietokannan nykytilan kanssa | DELETE |
| `500 Internal Server Error` | Odottamaton palvelinvirhe | – |
 
Virhevastauksen yleinen rakenne on `{"virhe":"..."}`. Tuntemattomalle
myynnille tai asiakkaan toisen käyttäjän myynnille ei palauteta sisältöä.

**Esimerkki oletetusta Springin virhevastauksesta** *(ei tämän API:n sopimus)*
 
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

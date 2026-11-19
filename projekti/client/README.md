# TicketGuru-asiakasnäkymä

Staattinen MVP tapahtumien selaamiseen ja myynnin tallentamiseen. Client käyttää
`server.js`-palvelinta sekä palvelimen välittämää `/api`-polkua backendille.
Pelkkä `index.html`-tiedoston avaaminen selaimessa ei riitä, koska API-pyynnöt
vaativat tämän välityspalvelimen.

## Render Web Service Clientille

https://lippupalvelu-client.onrender.com/

## Käynnistys Windowsissa

1. Asenna Node.js 18 tai uudempi.
2. Avaa PowerShell projektin juurihakemistossa `Projekti` ja siirry
   `client`-kansioon:

   ```powershell
   cd ".\projekti\client"
   ```
3. Käynnistä client:

   ```powershell
   node server.js
   ```

4. Avaa selaimessa <http://localhost:4173>.
5. Lopeta palvelin PowerShellissä näppäinyhdistelmällä `Ctrl+C`.

Palvelin välittää oletuksena API-pyynnöt osoitteeseen
`https://lippupalvelu-uons.onrender.com`. Render-palvelun ensimmäinen pyyntö
saattaa kestää, jos palvelu on ollut käyttämättömänä.

## Paikallisen backendin käyttö

Käynnistä Spring Boot -backend ensin. Aseta sitten samassa PowerShell-ikkunassa
API-osoite ja käynnistä client:

```powershell
$env:API_ORIGIN = "http://localhost:8080"
node server.js
```

Client hakee tapahtumat reitistä `GET /tapahtumat`, lipputyypit reitistä
`GET /tapahtumat/{id}/lipputyypit` ja tallentaa ostot reitillä `POST /myynnit`.
Ostaminen vaatii asiakastilin. Kirjautumistunnuksia säilytetään vain sivun
muistissa, ei selaimen pysyvässä tallennustilassa.

## Julkaisu

Clientin voi julkaista Node-palveluna ajamalla `node server.js`. Palvelin kuuntelee
oletuksena porttia `4173`; ympäristömuuttuja `PORT` ohittaa portin ja mahdollistaa
pilvipalvelun asettaman portin. Jos client julkaistaan staattisena sivustona,
julkaisualustan täytyy lisäksi välittää `/api/*`-pyynnöt backendille, sillä
pelkkä staattinen tiedostojulkaisu ei suorita `server.js`-välityspalvelinta.

## MVP:n rajaus

Korttimaksua tai tulostettavaa lippua ei vielä toteuteta tässä asiakasnäkymässä.
Jos palvelun tapahtumalistauksen tai lipputyyppien haku epäonnistuu, käyttöliittymä
näyttää virheen eikä näytä esimerkkitietoja ostettavina lippuina.

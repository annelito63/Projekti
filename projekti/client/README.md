# TicketGuru-asiakasnäkymä

Staattinen, MVP tapahtumien selaamiseen ja myynnin tallentamiseen.
Näkymä käyttää Spring-palvelua osoitteessa `https://projekti-6k8i.onrender.com`.

## Käyttö

Avaa `index.html` selaimessa tai julkaise `client`-kansion sisältö staattisena
sivustona (esimerkiksi Render Static Site). Sovellus hakee tapahtumat suoraan
palvelun julkisesta `GET /tapahtumat` -reitistä. Tapahtuman lipputyypit
haetaan reitistä `GET /tapahtumat/{id}/lipputyypit`. Ostoksen tallentaminen
vaatii asiakastilin: kirjaudu sisään tai rekisteröidy näkymässä.

Kehityspalvelimen osoitteen vaihtamista varten muuta `API`-arvoa
tiedostossa `app.js`. Kirjautumistunnuksia säilytetään vain sivun muistissa,
ei selaimen pysyvässä tallennustilassa.

## MVP:n rajaus

Ostaminen tallentaa myyntitapahtuman API:in (`POST /myynnit`). Korttimaksua,
lippukoodien generointia tai tulostettavaa lippua ei vielä toteuteta tässä
asiakasnäkymässä. Palvelun tapahtumalistauksen tai lipputyyppien puuttuessa
käyttöliittymä näyttää virheen eikä näytä esimerkkitietoja ostettavina lippuina.

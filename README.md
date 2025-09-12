# Phishing Message Detector

Aplikacja została napisana w języku Scala z wykorzystaniem Play Framework.

Aplikacja opiera się o założenie że nasłuchuje na requesty w postaci:
```
curl.exe '-X','POST', 'http://localhost:9000/check'  --header 'Accept: application/json' --header 'Content-type: application/json' --data ' {
  \"sender\": \"234100200300\",
  \"recipient\": \"48700800999\",
  \"message\": \"Dzieą dobry. W związku z audytem nadzór finansowy w naszym banku proszą o potwierdzanie danych pod adresem: https://www.m-bonk.pl.ng/personal-data\"
}'
```
Gdzie body ma postać wiadomości SMS opisanej w treści zadania.

W przypadku, kiedy usługa jest włączona i we wiadomości zostanie znalezione zagrożenie aplikacja odeśle wiadomość `{"status":"THREAT_DETECTED"}`

W przypadku, kiedy usługa jest włączona i we wiadomości nie zostanie znalezione zagrożenie aplikacja odeśle wiadomość `{"status":"NO_THREAT_DETECTED"}`

W przypadku, kiedy pod określony numer (`SERVICE_PHONE_NUMBER`) zostanie wysłana wiadomość o treści `START` aplikacja zapamięta wybór i odeśle wiadomość `{"status":"SERVICE_TURNED_ON"}`

W przypadku, kiedy pod określony numer (`SERVICE_PHONE_NUMBER`) zostanie wysłana wiadomość o treści `STOP` aplikacja zapamięta wybór i odeśle wiadomość `{"status":"SERVICE_TURNED_OFF"}`

W przypadku, kiedy usługa jest wyłączona i SMS nie będzie zawierał żadnej z w/w komend aplikacja odeśle wiadomość `{"status":"SERVICE_DISABLED"}`

## Wymagania

Do działania aplikacja potrzebuje zainstalowanej bazy danych POSTGRES 17.2 o parametrach wymienionych poniżej.

Zmienne środowiskowe wymagane przez aplikację
- `SERVICE_PHONE_NUMBER` - numer telefonu serwisu, pod który przychodzą wiadomości z poleceniem rozpoczęcia i zakończenia działania usługi dla danego numeru
- `POSTGRES_DATABASE_NAME` - nazwa bazy danych, której używa aplikacja
- `POSTGRES_PASS` - hasło do bazy danych
- `POSTGRES_SERVER` - host bazy danych
- `POSTGRES_PORT` - port bazy danych
- `POSTGRES_USER` - użytkownik bazy danych 
- `GOOGLE_API_KEY` - klucz API do usługi https://cloud.google.com/web-risk/docs/reference/rest/v1eap1/TopLevel/evaluateUri
- `APPLICATION_SECRET` - sekret aplikacji, używany przez Play Framework do szyfrowania informacji

W celu zbudowania obrazu i opublikowania go poprzez DockerHub należy 
- mieć zainstalawaną javę (wersja conajmniej 21)
- mieć ustawioną zmienną środowiskową `DOCKER_REPOSITORY` z określonym repozytorium
- wykonać polecenie `sbt "docker:publish"`

W celu uruchomienia aplikacji lokalnie należy
- wykonać polecenie `sbt run`
- aplikacja będzie potem nasłuchiwać na porcie 9000

Aplikacja nasłuchuje domyślnie na porcie 9000. W celu użycia obrazu Docker należy przemapować ten port na wybrany przez siebie port.

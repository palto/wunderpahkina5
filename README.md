# wunderpahkina vol5 ratkaisu
Ratkaisu wunderpahkina vol5 kisaan. Ratkaisu on toteutettu Java 8:lla ja Ratpack web palvelimella

Sovellus käynnistää webbipalvelimen osoitteeseen http://localhost:5050 josta ratkaistu kuva voidaan käydä tarkistamassa. 

Osoitteessa http://localhost:5050/debug näkyy kuvan merkitsevät pikselit

# Kääntäminen ja ajaminen

Sovelluksen ajaminen ja kääntäminen vaativat että JDK 8 on asennettu ja sen polku on asetettu M2_HOME ympäristömuuttujaan

Sovellus käännetään komennolla mvnw clean install

Sovelluksen voi käynnistää komennolla mvnw exec:java

Ratkaisun merkitsevä lähdekoodi on Java luokassa fi.tonipaloniemi.wunderpahkina5.SolutionDrawer
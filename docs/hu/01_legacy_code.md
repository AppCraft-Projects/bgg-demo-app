# A legacy kód megismerése

Ezen a ponton szeretnénk nektek 5 percet adni, hogy átnézzétek. Azt követően Tibi fogja végig mutagatni nektek ezeket a pontokat részletesen, itt csak néhány részletet szeretnék kiemelni. 

## Activity

**Hol vagyunk?** *NearbyActivity*

Rengeteg dolog van egyben az *Activity*-ben, nem szeparáljuk a felelősségi köröket:

* *View* és kód összekötése
* User inputok kezelése
* UI renderelés
* Activity lifecycle kezelés
* *Networking* és *AsyncTask* bele a kellős közepébe
* *Adatbázis* kezelés

### .findViewById()

**Hol vagyunk?** *NearbyActivity.onCreate()*

Ez jelen helyzeztben nem vészes, lehetne durvább is, láttam osztályokat ahol 15-20-30 elemet is kötöttek be így. 

Ami viszont már zavaróbb, hogy 

### AsyncTask:

Hol vagyunk? *NearbyActivity* és azon belül *GetRecomendationTask*.

Benne az *Activity* közepébe, egészen zseniális.

[TODO] Miért nem szeretjük az AsncyTaskokat? [TODO] Hogyan lehet másképp csinálni?

### HttpURLConnection

**Hol vagyunk?** *doInBackground* metódus

Ismét a web-es példából szeretnék kiindulni, egy sima JSON-t akarok letölteni, de ez kb 40 sor kódot fog eredményezni.

[TODO] Emelj még ki néhány csúnya megoldást! Beszélj erről Tibivel 

[TODO] Hogyan lehet másképp csinálni?

## Tekintsünk a múltba...

Két gyors kérdés... 


[TODO] ezeket vigyük előrébb, korábban kérdezzük meg, bemutatkozás után 

**[KÉRDÉS]** Hány Android fejlesztő van köztetek a teremben? 

**[KÉRDÉS]** Oké, látom a többség jó helyen jár! De mivel foglalkoztok TI, akik nem nyújtottátok fel a kezeteket? :)
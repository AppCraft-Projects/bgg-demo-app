# Android Refactor

## Bemutatkozás

Tibi ... Gábor ...

## Erre számíthattok a következő órákban

Szuper, mielőtt elkezdem, felhívnám a figyelmet arra, hogy nem kell a jegyzeteléssel töltenetek az időtöket. Ezt már
megoldottuk nektek, rövidesen megmutatom. Figyeljetek, beszélgessünk, ez legyen a fókusz.

Előtte azonban ki szeretném fejteni, hogy mivel foglalkozunk mi itt.

Ugye a workshop címe az Android Refactor. Minden olyan projekt esetében, ami évek óta tart, óhatatlanul eljön az a pont,
amikor is a kódbázis elég erősen rozsdásodni kezd. Megjelennek ilyen-olyan rossz megoldások a kódban.

[KÉRDÉS] Ugye nem kell ezt túlmagyaráznom, tegye fel a kezét, aki még nem találkozott ilyennel! :) - Nyugi, Ti majd még
fogtok. :)

Szóval, hoztunk magunkkal egy példakódot, ami kapcsán Tibi tényleg beleadott mindent, igazi, fasza, tákolmány. Mindenek
előtt ezt fogjuk megismerni.

Majd azt követően számos lépésben rendbe fogjuk vágni ezt a kódot:

* Egy jó kis legacy kóddal fogjuk kezdeni.
* Honnan jövünk?
* Hol tartunk most?
* Kotlin alapozó
* Networking
* Repository
* Room
* Live Data
* ViewModell
* Lifecycle kezelése Owner / Observer
* A View réteg feldarabolása kis komponensekre
* Navigation

Tibivel mi alapvetően egy két napos workshopot dolgoztunk, annak látjátok most egy tömörített változatát. Persze sok
rész kimaradt, kevesebbet dolgoztok Ti, ellenben mi többet dumálunk. Fontos kijelenteni, hogy nem fogunk ma a végére
érni, nem teszünk ilyen vállalást. Amit kimondunk, hogy jó cserkész módjára számos szempontból egy átláthatóbb és jobban
kezelhetőbb kódbázist hagyunk magunk után.

Még valami, ha kérdés van, akkor nyugodtan kezet fel, adjatok füstjelet, kukorékoljatok, ami jól esik. Közbe is
nyugodtan, de a végén lesz 20-30 perc, amit külön a Q&A-re dedikálunk.

## Anyagok

[KÉRDÉS] Ugye hoztatok magatokkal gépet? Azon jobb lesz mostantól dolgozni.

Nagyon sok anyaggal szeretnénk titeket segíteni.

Egyrészt, a projekt amit mutatunk, az fullosan elérhető GitHubon, itt is tudjátok követni.

Backend: [TODO]

Android: [TODO]

Valamint az Android repóban vannak előre elkészített jegyzetek is, itt éritek el: [TODO]

## A legacy kód megismerése

Ezen a ponton szeretnénk nektek 5 percet adni, hogy átnézzétek.

Majd fel szeretném hívni a figyelmet néhány igazán érdekes pontra, ki is emeljük ezeket.

Innentől belemászunk a kódba.

### Activity

Hol vagyunk? NearbyActivity osztály

Minden ott van az Activity-ben, nem szeparáljuk a felelősségi köröket:

* View és kód összekötése
* Networking és AsyncTask bele a kellős közepébe
* Adatbázis kezelés

De menjünk mélyebbre ebben.

### .findViewById()

Hol vagyunk? onCreate metódus

Ez most istenes is, láttam osztályokat, ahol 15-20 elemet is kötöttek így.

[TODO] Miért nem szeretem? [TODO] Hogyan lehet másképp csinálni?

### AsyncTask:

Hol vagyunk? NearbyActivity és azon belül GetRecomendationTask

Benne az Activity közepébe, egészen zseniális.

[TODO] Miért nem szeretjük az AsncyTaskokat? [TODO] Hogyan lehet másképp csinálni?

### HttpURLConnection

Hol vagyunk? doInBackground metódus

Ismét a web-es példából szeretnék kiindulni, egy sima JSON-t akarok letölteni, de ez kb 40 sor kódot fog eredményezni.

[TODO] Emelj még ki néhány csúnya megoldást! Beszélj erről Tibivel [TODO] Hogyan lehet másképp csinálni?

## Tekintsünk a múltba...

Két gyors kérdés... [KÉRDÉS] Hány Android fejlesztő van köztetek a teremben? [KÉRDÉS] Oké, látom a többség jó helyen
jár! De mivel foglalkoztok TI, akik nem nyújtottátok fel a kezeteket? :)

Innentől pedig a lényegre térek!

Az Android fejlesztés nehéz, ugye? Próbáltatok összerakni egyszerűbb appokat mondjuk Angulárral, Reacttal vagy Vue-val,
az valahogy sokkal egyszerűbben indul egész.

[TÁBLÁZAT] web android hello world easy easy fetch & display data easy tricky complex app hard hard & tricky

[TODO - Jöhetne ide néhány webes példa, csak pár sor... mellé egy Androidos - https://youtu.be/2QDAbH2tdoE?t=10m37s]

Mindemelett kevesebb fogalmat, kevesebb osztályt és metódust kell megtanulni és fejben tartani. És mennyivel kevesebb a
boilerplate!

Évek óta morgok ezen, hogy a Google kényelmesen úgy gondoltak, hogy csak ad egy keretrendszert, és ezzel az Ő dolgának
vége.

## Nem foglaltak állást...

Szerintem sok baj forrása, nagyon sokáig nem akartak állást foglalni számos fontos kérdésben, egyszerűen a fejlesztőkre
hagyták, magas szintről nézve ilyen volt az architektúra is.

Ez pedig nem kevés káoszt szült, rengeteg különböző verziót hoztak össze az évek során:

* MVC
* MVP
* MVVM
* Clean Architecture / VIPER / Uber RIBS
* stb.

Beszéljünk át ezekből az első hármat részletesebben is, hogy megadjunk egy alaphangot az elkövetkezőknek.

Példaként az egyszerűség kedvéért az Android Studio egyik beépített template-ét fogom használni, a LoginActivity-t.
Kicsi, jól átlátható, könnyen elképzelhető, így pedig én könnyen tudom majd szemléltetni felétek vele az alap
koncepciókat.

### MVC

A 80-as években a SmallTalkkal terjedt el igazán, majd sokan átvették az ötletet. Az Objective-C és a Cocoa keretrenszer
fejlesztői különösen sokat inspirálódtak belőle, így szivárgott át később a CocoaTouch, avagy az iOS fejlesztőkhöz is.
Valamint később sok más helyre is.

Az architektúra egyik alapvetése, hogy az információ egy irányba halad / folyik. Avagy a View figyel az inputra, majd
azt átadja a Controllernek, az elvégez néhány műveletet, mondjuk némi input validációt, majd némi transzformáció után
továbbadja a domain Model irányába.

Talán a lényeges szempont, amiben ténylegesen eltér a többi megoldástól, hogy a View és a Model között direkt függőség
van.

Ez pedig kínálja a lehetőséget egy megfigyelő (observer) minta valamilyenféle megvalósítására. Avagy ha változik bármi
az alsó domain adat Modelben, az visszaszinkronizálható a View-ra.

Ilyesfajta állapot szinkronizálásához szépen passzolhat egy Data Binding is, erre az Android kínál is egy normális
megoldást, ami az XML leíróból is elérhető .

### MVP

A fő cél, hogy a logika minél nagyobb részét kivigyük a viewból.

Három fontos különbséget szeretnék kiemelni az MVC-vel szemben:

1. Nincs direkt függőség a View és a Domain model között, a Presenter közvetít közöttük.
2. Ennek következtében az adat-áramlás kétirányú.
3. A View könnyedén mockolható.

Ahogy a képről látjátok a Presenter nincs direkt függőségben a View-val, ha valamit meg akar változtatni, akkor egy
Interface-en keresztül kommunikál azzal. Ez erősen különbözik a korábbitól, nincs szó observation-ről, nincs szó data
bindingról, ez egy konkrét parancs, “Hé View, jelenítsd meg a felhasználót!”.

Ez pedig a tesztelésnél válik igazán hasznossá, nem kell a valós Androidos view objektumokkal dolgozni. Kicserélhetővé
válik mindez egy tesztkörnyezetben.

### MVVM

A harmadik és egyúttal utolsó az MVVM, ez is egy klasszikus minta. Valahol az előző kettő közötti megoldásról van szó.

Abból a szempontból hasonlít az MVP-re, hogy nincs direkt függőség a View és a Domain model között, a ViewModel közvetít
közöttük.

A ViewModel magába zárja a logika jelentős részét, validációt, hasonlókat.

A View két kapcsolatot tart fenn a ViewModel-el:

* Továbbítja az inputokat.
* Megfigyeli (observe) azt, ha változik az állapot, akkor frissíti önmagát.

### Még néhány gondolat...

Mindez cégről-cégre, projektről-projektre változik, sokszor elég szabadosan is értelmezik. De persze aláírom, hogy
projekt mérettől és igényektől függően szükség lehet más megközelítésekre, de akkor is.

Ez a helyzet bizonyosan nem kevés fejfájást is okoz a fejlesztőknek. Megnehezítik, ha egyik projektről a másikra
váltanak.

## Véleményt kellett alkotniuk

Szóval úgy röpke 10 évvel az első Android megjelenése után a Googlenél is ráéreztek, hogy ez nem teljesen frankó így.

Összehívtak egy méretes bizottságot, számos cég fejlesztőjével, oktatókkal, szabadúszúkkal. Miután elkezdték körbejárni
az általános problémákat, bizony számos fájdalmas terület kijött:

* Adat perzisztálás
* Életciklus menedzselés
* Alkalmazás modularitás
* Adat szinkronizálás
* Szálkezelés
* Sok boilerplate

És volt egy emlékezetes tanulság. Nem akarnak a technikai részletekkel vesződni, egyszerűen csak szállítani akarják a
működő terméket, építeni az üzletet.

Elsőre durván hangzik, de gondoljatok csak bele, mert a maga szempontjából igaza van. Ehhez pedig az alapokat rendbe
kell szedni.

A céljaik a következők voltak:

* Az egyszerű dolgok tényleg legyenek egyszerűek
* A bonyolultak legyenek lehetségesek.
* Ne találják fel újra a melegvizet.

Külön kiemelném az utolsót. Nem akarták újragondolni az egész mindenséget, szépen játszanak a meglévő és bevált
megoldásokkal, mint Retrofit, Dagger, vagy RxJava.

Bevezettek tehát néhány architektúrális komponenset:

* Room
* LiveData
* ViewModel
* Lifecycle Observer

Így néz ki az architektúra ajánlása egészében: ￼

Külön-külön is használhatóak ezek a komponensek, de együtt lesz igazán érdekes. Ezt egyelőre csak felületesen veszem át,
de rövidesen részeltesebben is ránézek az egyes részekre.

Ezekből szeretném egyelőre átvenni veletek az Android Architecture Components által kínált hármat, mivel feltételezem ez
fog a többségeteknek újdonságot jelenteni:

* Room,
* LiveData
* ViewModel.

FONTOS: ezeket a kódokat még ne próbáljátok beírni, ezekkel csak a koncepciókat akarom szemlélteni.

### Room

Kezdjük a bal alsó sarokban a Room-nál, ez pedig egy SQL-Java mappelő könyvtár. Mindössze ennyit csinálunk majd vele.

Mindössze két “új” fogalmat kell majd itt memorizálnotok:

* POJO,
* és DAO.

Nézzük is ezeket. Egyszerű POJO-kat (Plain Old Java Object) képez le az SQLite adatbázisnak megfelelő formátumra és
vissza.

Ilyen egyszerű, már százszor írtatok hasonlót:

public class User { public String id; public String firstName; public String lastName; public String jobTitle; public
int age; }

Ezt egészítjük ki néhány egyszerű annotációval, és már jó is lesz a Roomnak:

@Entity public class User { public @PrimaryKey String id; public String firstName; public String lastName; public String
jobTitle; public int age; }

Mindegyik POJO-hoz tartozik egy DAO (Database Access Object) is, ahol minden egyes funckió egy SQLite parancsnak felel
meg.

Mutatok egy példát az összes alap CRUD (Create, Read, Update, Delete) műveletre:

@Dao public interface UserDao {

    @Insert(onConflict = IGNORE)
    void insertUser(User user);

    @Query(“SELECT *  FROM User”)
    public List<User> findAllUsers();

    @Update(onConflict = REPLACE)
    void updateUser(User user);

    @Query(“DELETE FROM User”)
    void deleteAllUsers();

}

Figyeld meg az első három funkciót, mindháromnál megjelenik a User objektum, ezt fogja átalakítani oda és vissza a POJO
és az SQL adatbázis között.

Valamint @Query után szereplő parancsok folyamatosan ellenőrizve vannak fordítási időben, egész érthető hibaüzeneteket
ad az Android Studio, ha valami nem stimmel.

Tegyük fel, hogy egy sima elgépelés történt: ￼

Egyelőre ennyit akartam a Roomról mondani, rövidesen visszatérünk rá részletesebben is.

### LiveData

Lépjünk egyet tovább, tegyük fel, hogy egy tisztességes reaktív alkalmazást akarunk építeni, ez esetben jól fog jönni a
LiveData, a második architektúra komponensünk. Ezzel monitorozhatóak a adatváltozások az adatbázisban.

A LiveData tömören egy megfigyelhető (observable) adat tároló. Értesíti a megfigyelőket (observers), amikor valami
adatváltozás történik. Így azok frissíthetik az alkalmazás UI-át.

Így illeszkedik ez a komponenens a mi kis egyszerű alkalmazásunkba...

A használatához mindössze két egyszerű új osztályt kell memorizálnotok:

* LiveData,
* és MutableLiveData.

Nagyon egyszerű használni:

MutableLivedData<String> dayOfWeek = new MutableLiveData<>(); dayOfWeek.observe(this, data -> {
mTextView.setText(dayOfWeek.getValue() + “ is a good day.”); });

Tehát...

1. deklarálod valamilyen típusra,
2. megfigyelem azt,
3. frissíted a UI-t.

Valahogy így lehet ezt vizualizálni szépen: ￼

Ha meg akarod változtatni az értékét, mindössze ennyi egy metódust kell hívnod:

dayOfWeek.setValue(“Friday”);

Ez is vizualizálva: ￼

Ami igazán király, hogy a Room teljes mértékben támogatja a LiveData komponenst, nagyon egyszerű rávenni, hogy
LiveData-t adjon vissza, mindössze a DAO objektum visszatérési típusát kell finomhangolni.

@Dao public interface UserDao { @Insert(onConflict = IGNORE) void insertUser(User user);

@Query(“SELECT \* FROM User”) public LiveData<List<User>> findAllUsers();

@Update(onConflict = REPLACE) void updateUser(User user);

@Query(“DELETE FROM User”) void deleteAllUsers();

}

Tehát a második parancsnál a visszatérési érték korábban List<User> volt, most pedig wrappeltük azt,
LiveData<List<User>>.

Ezután tudsz mondjuk egy RecyclerView-t ennyivel frissíteni az UserListActivity-ben:

userLiveData.observe(this, users -> { mUserRecyclerAdapter.replaceItems(users);
mUserRecyclerAdapter.notifyDataSetChanged(); });

Összefoglalva annyi történik, hogy...

1. megváltozik az adat a DB-ben,
2. frissül a LiveData,
3. a UI is értesítve lesz az eseményről, így az szintén frissülhet.

Ennyi!

### ViewModel

Van még itt nekünk egy fogos problémánk. Miért van az, hogy a legtöbb Android fejlesztő azzal kezd egy új appot, hogy
letiltja az alkalmazás elforgathatóságát? Nos, a válasz a konfiguráció változás. Ahogy forgatjuk az appot az Activity-k
létrejönnek, élnek, meghalnak. Valahogy így...

Nos, ez az adatbázis kezelés szempontjából nem kifejezetten optimális működés, sok pazarláshoz vezethet. Konkrétan
számos költséges lekérdezés megismétléséhez. Avagy minden egyes alkalommal, amikor elforgatja a felhasználó a telefont.

Ezt lehet kicselezni a ViewModel-eket, bevezetünk egy újabb réteget, amelynek az életciklusa különáll az Activity-től,
és túléli azt, amikor az Activity-k létrejöttét és megsemmisítését a forgatásoknál.

Tehát, röviden összefoglalva a View model egy olyan objektum, amely adatot szolgáltat a UI komponenseknek, és túléli a
konfiguráció változásokat.

Ismét három új osztályt / fogalmat kell memorizálni:

* ViewModel,
* AndroidViewModel,
* és ViewModelProvider.

Lássuk a példát. Először is örökölni kell a TrailListViewModelből:

public class UserListViewModel extends AndroidViewModel { private AppDatabase mDatabase; private LiveData<List<User>>
users;

public UserListViewModel(Application application) { super(application);

    mDatabase = AppDatabase.getDb(getApplication());
    users = mDatabase.userModel().findAllUsers();

}

// Getters, setters ...

}

A ViewModel és az AndroidViewModel közötött annyi a különbség, hogy átadásra kerül az Application objektum vagy sem. Az
utóbbi ennyivel jobban kötődik az Android keretrendszerhez, ami a tesztelésnél lehet hátrány.

Az AppDatabase egy Room adatbázis singleton, onnan tudod elkérni az alkalmazáshoz tartozó adatbázist.

Abból lehet lekérni a UserModel-t, amin keresztül a Dao parancsok elérhetővé válnak.

A UserListActivity-ben a használata nagyon egyszerű, mindössze ez a pár sor:

userListViewModel = ViewModelProviders.of(this).get(UserListViewModel.class);

userListViewModel.getUsers().observe(this, users -> { mUserRecyclerAdapter.replaceItems(users);
mUserRecyclerAdapter.notifyDataSetChanged(); });

Lássuk a lépéseket sorban:

* A ViewModelProviders.of(this) használatával elkérjük a ViewModelProivoder példányt.
* Ebből a .get(UserListViewModel.class) használatávan egy újabb példányt teszünk magunkévá.
* A userListViewModel változó keresztül elérhetővé tesszük az Activity-ben.
* A userListViewModel.getUsers() használatával megszerezzük a LiveData-t.
* .observe(this, users -> { ... } metódussal feliratkozunk az adatváltozás eseményre.
* Végül utolsó lépésként azon belül frissítjük a UI-t.

Összefoglalva ezt csináltuk most: ￼

Okos, igaz?

Itt tartunk tehát most a nagy képből, ennyit kínál az Android Architecture Components, de ki fogjuk egészíteni a még
szükséges komponensekkel. ￼

## Kotlin alapok

[TODO] Ez úgy fog történni, hogy megnézem milyen konstrukciókat használtál, és csak azokat húzom be ide.

[TODO] A Kotlinos workshopból áthúzok majd részleteket.

## Networking leválasztása

### Ahonnan indulunk....

Érdemes egy kicsit ízlelgetni a kódot, amit a legacy változat esetében látunk.

[TODO] Másold be a kódot a legacy verzióból.

Mennyire hosszú kódot és hány osztályt használunk ahhoz, hogy letöltsünk egy sima JSON formátumú adatot egy szerverről,
majd átalakítsuk azt a megfelelő formátumra?

AsnycTask, HTTPURLConnection, JSONObject, és még számos más osztályt alkalmazunk itt.

[TODO] fejtsd ki röviden mit csinálnak az egyes részek.

Alapvetően egyszerű és kifejezetten repetatív feladatot végzünk itt, de kód egyrészt nem egyszerű, másrészt csupa
boilerplate.

### Lehet ezt jobban is

Ezt nem mi vettük észre először, a Square fejlesztő csapatát is nyomasztotta ez az egész, ezért hozták létre az OKHttp,
Retrofit és Picasso nevezetű könyvtárakat. Használták mellé a Google féle JSON-to-POJO mapping könyvtárát, és kész is
van.

Mind sokat bizonyított, maximálisan érett könyvtárak, és együtt mostanra de-facto szabvánnyá váltak. Teljes nyugalommal
tudom azt mondani, hogy az esetek 90%-ban ez a csomag hibátlanul fog működni, és nem kell sokat dolgozni rajt.

### OkHttp 3

Kifejezetten alacsony szintű könyvtárról van szó, az Android alap HTTP stackét cseréli ki egy modernebbre. A
következőnéhány előnnyel jár:

* Teljes HTTP/2 támogatás, illetve connection pooling, ha az nem támogatott.
* GZip tömörítés, hogy összenyomja a csomagok méretét.
* Cacheli a szerverek válaszát, hogy csökkentse az ismételt hívások számát.
* Kezeli az IPv4 és IPv6 váltásokat.
* Elfed számos különböző hálózati problémát és csendben feláll azokból.

Olyan szinten elterjedt könyvtárról van szó, hogy az Android framework csapat is gyakran kifejezetten ennek a
használatát javasolja.

Az alacsony szinthez viszonyítva kifejezetten egyszerű a használata, de előlünk a mélyebb részeket el fogja rejteni a
Retrofit.

### Retrofit

Térjünk ki erre részletesebben, ez a könyvtár nagyságrendekkel egyszerűbbé teszi a REST API-k kezelését. A kód maga
sokkal rövidebb és könnyebben értelmezhető lesz.

Két dolgot kell majd magadnak csinálni, definiálni kell a Retrofittel egy sima interfacet, deklaratív módon leírni a
kommunikációt a REST API-val. A Retrofit.Builderrel legyárthatóak lesznek a hívások, majd mehetnek a megfelelő hívások.

Szépen be tudod kötni ebbe HTTP kliensként az OkHttp-t, illetve számos konvertert is, így számos formátumból mappelhető
lesz az adat.

[TODO] Mutasd meg, hogy néz ki a REST API, jó lenne erről egy kép. [TODO] Mutasd meg a Retrofit inteface-t.

[TODO] Építsd fel OkHttp-vel és GSON-nel.

Még egy apóság, ez az inferace egyébként kifejezetten egyszerűen generálható, mondjuk egy Swagger doksiból.

### Picasso

Még egy puzzle darab azért hiányzik nekünk. Szükségünk lesz képek kezelésére is, de ez nem egyszerű történet, le kell
fedni számos use-caset:

* Rendkívül költséges az ImageView-t újra és újra létrehozni és mindig megsemmisíteni, például egy RecyclerView
  esetében, jó lenne valahogy újrafelhasználni.
* Letöltés megszakítását. Például, ha az Activity megsemmisül.
* Különböző kép-transzformációk alacsony memória használattal.
* Automatikus kép cachelés memóriába és háttértárra.

Mindezt hagyományos módon piszkosul melós lenne lekódolni. Jó lenne, ha ezt valami frankón elfedné előlünk. Na mindezt
hozza a Picasso.

[TODO] Mutasd az ide kapcsolódó legacy kódot.

[TODO] Mutasd az új kódot.

Látványosan egyszerűbb így, ugye.

## Repository

Már csak egy utolsó lépés van hátra, szeretnénk, ha a hálózat, illetve az adatbázis, illetve így összességében az adatok
kezelése nem az Activity-ben foglalna helyet. Szeretnénk, ha ezek a részek még jobban elkülönülnének.

[TODO] Mutasd a régi kódot.

Ehhez pedig egy fontos lépés a Repository bevezetése, ami épp ezeket fogja össze. Egyébként egy rendkívül egyszerű
komponensről van szó, tényleg nincs benne semmi nagy csavar, mindössze egy pici logika.

Ami így néz ki:

* Használja a lokálisan perzisztált adatot, ha még adott időkereten belül van.
* Egyébként próbáljon meg frissebbet letölteni. Arról már az OkHttp és a szerver együttesen képesek gondoskodni, ha nem
  áll rendekezésre a cacheltnél frissebb adat.

[TODO] Mutasd az új kódot.

## ViewModel

Ismétlésként, a View model egy olyan objektum, amely adatot szolgáltat a UI komponenseknek, és túléli a konfiguráció
változásokat. Ennyi, semmi több. ￼

[TODO] Mutass valami béna példát.

Másrészt segít pár fokkal jobban betartani a Single Responsibility Patternt. Erre azért van szükség, mert a legtöbb
Activity esetében túl sok felelősség van egymásra halmozva, és ettől szépen meg is tud hízni.

Lássuk csak:

* UI komponensek kirajzolása
* Felhasználói interakciók kezelése
* UI mentése és visszaállítása konfiguráció változásnál
* Adatok betöltése
* Adatok feldolgozása

Segítünk szétválasztani a dolgok:

* ViewModel: UI adatok kezelése,
* Activity: UI rajzolás, felhasználói interakciók menedzselése,

Még mindig nem százas, ezért szoktunk egy harmadik szintet is bevezetni:

* Repository: API kezelés, adatok mentéséhez és letöltéséhez.

Persze igazán komplex alkalmazások esetében tovább lehet lépni egy MVP mint irányába, és bevezetni egy újabb réteget:

* Presenter: UI adatok feldolgozása.

Extrém esetben még tovább is lehet menni.

Így néz ki egyben ez az egész: ￼

Van itt egy trükkös rész, a ViewModel konstruktor alapból nem kap semmi paramtért, ha szeretnéd, akkor erre két
módszerrel van lehetőséged:

* Egy setter metódussal beállítol értéket, rögtön miután megkaptad a ViewModel objektumot.
* Vagy a ViewModelFactory használatával lesz lehetőséged saját testreszabott konstruktor kialakítására.

FONTOS! Soha, de soha ne tárolj Activity, Fragment vagy View referenciákat, illetve azok Context-ét a ViewModelben.
Ezekből lesznek a csúnya memory leak-ek. Avagy olyan dolgot tartunk majd a memóriában, amit már egyébként megsemmisített
a keretrendszer. Ezek egyébként csúnya kivételeket is tudnak majd dobálni.

Ha mégis szükség lenne egy ApplicationContextre, akkor használd az AndroidViewModel osztályt, az megoldja ezt neked.

Még egy FONTOS pont! Nem váltja ki az onSaveInstance() funkció használatát, ha kilövik a folyamatot, akkor bizony a
ViewModel is megy a levesbe.

A következő stratégiát érdemes követni adattárolás szempontjából:

* Adatbázis: nagyobb adatok, hosszabb távra, mind ide menjenek.
* ViewModel: minden adat, amit aktuális az Activity UI-on meg akarok jeleníteni.
* onSaveInstance(): vészhelyzet esetén az adott Activity UI visszaállításához a lehetős legkisebb adatok. Pl egy
  UserDetail képernyő esetében elég a User-hez tartozó id.

￼Végül pedig van arra lehetőség, hogy egy ViewModel használatával megosszatok adatokat Fragmentek között. Ezt csak mint
érdekesség említettem meg, nem fogjuk részleteiban tárgyalni, javaslom azonban a hivatalos doksik böngészését.

￼[TODO] Saját kódot bemásolgatni. [TODO] Saját kódot elmagyarázni.

## Live Data

Ismételjünk kicsit. A LiveData tömören egy megfigyelhető (observable) adat tároló. Értesíti a megfigyelőket (observers),
amikor valami adatváltozás történik. Így azok frissíthetik az alkalmazás UI-át.

Egy fontos részletet azonban nem tárgyaltunk korábban, ez pedig a teljes életciklus témakör. Három fogalommal érdemes
itt tisztában lenni:

* Lifecycle: Objektum, ami definiálja az Android életciklust.
* LifecycleOwner: Interface egy objektumnak, ami rendelkezik életciklussal.
* LifecycleObserver: interface egy LifecycleOwner megfigyeléséhez (observation).

A Support Library 26.1-től kezdődően az Activity és Fragment osztályok implementálják a LifecycleOwnert.

Így néz ez ki: ￼

Az ide tartozó kódot pedig ismeritek a korábbi példából:

userListViewModel = ViewModelProviders.of(this).get(UserListViewModel.class);

userListViewModel.getUsers().observe(this, users -> { mUserRecyclerAdapter.replaceItems(users);
mUserRecyclerAdapter.notifyDataSetChanged(); });

Itt az Activity-ben az az observe hívást pont ezt a monitorozást váltja ki.

Fontos, hogy amikor egy Activity megsemmisül, akkor ez a monitorozás is végetér, nem lesz semmi csintalan leak, neked
pedig ehhez semmit sem kell tenned.

￼Több módja is van egy LiveData értékének a módosítására:

* setValue() - ha UI szálról történik,
* postValue() - ha háttér szálról.

Ahogy arról már szó volt, a Room és a LiveData nagyon simán működnek együtt:

* Autómatikusan értesít adatbázis frissítésnél,
* Az adatokat automatikusan betölti egy háttérszálon.

Korábban volt szó arról, hogy két változata van a LiveData osztálynak:

* MutableLiveData - írható, setValue() és postValue() metódusokkal változtatható az értéke.
* LiveData - csak olvasható, immutable, nincs se setValue(), se postValue().

Azt javaslom, hogy legfeljebb a ViewModel-en belül használd a MutableLiveData osztályt, azon kívül az immutable
LiveData-t ajánld ki, csomó bugnak az elejébe lehet így menni.

A későbbiekben előfordulhat az, hogy manipulálni / transzformálni szeretnéd a LiveData-t, erre három funckiót kínál
jelenleg a rendszer:

* map() - egy funkcióval a LiveData kimenetét lehet megváltoztatni a használatával.
* switchMap() - mint a map(), de teljesen új LiveData objektumot ad vissza.
* MediatorLiveData - több LiveData összefuttatásához. Pl adatbázisból és hálózatról.

[TODO] Jöhetnek a saját kódok

## Room

Tehát, ahogy beszéltük a Room egy robosztus SQL objektum mappelő könyvtár. Ez esetben már majdnem mindent megmutattam,
de csak egy kicsi van hátra a DAO kapcsán.

Nézzünk egy példát:

@Entity(tableName = “users”) public class User {

@PrimaryKey @ColumnInfo(name = “user_id”) public String id;

@ColumnInfo(name = “first_name”) public String firstName;

@ColumnInfo(name = “last_name”) public String lastName;

@ColumnInfo(name = “job_title”) public String jobTitle;

public int age;

}

Amennyiben az egy tábla vagy oszlop neve eltérne az osztály vagy egy tulajdonság nevétől, az lehet jelölni:

* @Entity(tableName = “users”) - Összeköti a “User” osztályt a “users” táblával,
* @ColumnInfo(name = “user_id”) - Összeköti az “id” tulajdonságot a “user_id” osztloppal.

Innentől pedig nézzük, hogy mit látunk a saját projektünkben.

[TODO] a kód a saját projektből

## Mi maradt ki?

[TODO]

## Q&A és lezárás

[TODO]

package model;


import java.time.LocalDate;
import java.util.regex.Pattern;

import dal.exceptions.ValidationException;

public class Jarat {
    private Integer vonatszam;

    public Integer getVonatszam() {
        return vonatszam;
    }

    public void setVonatszam(Integer vonatszam) {
        this.vonatszam = vonatszam;
    }

    public void parseVonatszam(String vonatszam) throws ValidationException {
        //TODO: validate
        if (Pattern.matches("[0-9]{1,5}",vonatszam)) {
            this.vonatszam = Integer.parseInt(vonatszam);
        } else {
            ValidationException ve = new ValidationException("Kötelező kitölteni, min 1 és max 5\n"
                    + " szám lehet, és csak szám!");
            ve.setFieldName("vonatszam");
            throw ve;
        }
    }
    private String tipus;

    public String getTipus() {
        return tipus;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    public void parseTipus(String tipus) throws ValidationException {
        //TODO: validate
        if (Pattern.matches("((Gyors|IC)|(szemely|EC))", tipus)) { //nem elírás, így volt a constrainteknél
            this.tipus = tipus;
        } else {
            ValidationException ve = new ValidationException("Tipus csak IC, EC, Gyors \n"
                    + "illetve személy lehet");
            ve.setFieldName("tipus");
            throw ve;
        }
    }
    private String nap;

    public String getNap() {
        return nap;
    }

    public void setNap(String nap) {
        this.nap = nap;
    }

    public void parseNap(String nap) throws ValidationException {
        //TODO: validate
        if (Pattern.matches("(0|1){7}", nap)){
            this.nap = nap;
        } else {
            ValidationException ve = new ValidationException("A nap csak 0 vagy 1 lehet és\n"
                    + " pontosan 7 db, megadása kötelező.");
            ve.setFieldName("nap");
            throw ve;
        }
    }
    private LocalDate kezd;

    public LocalDate getKezd() {
        return kezd;
    }

    public void setKezd(LocalDate kezd) {
        this.kezd = kezd;
    }

    public void parseKezd(String kezd) throws ValidationException {
        //TODO: validate
        if (Pattern.matches("(1|2)[0-9]{3}-(0|1)[1-9]-[0-3][0-9]", kezd)){
            this.kezd = makeLocalDateFromString(kezd);
        } else {
            ValidationException ve = new ValidationException("A kezdési dátum formátuma helytelen.");
            ve.setFieldName("kezd");
            throw ve;
        }
    }
    private LocalDate vege;

    public LocalDate getVege() {
        return vege;
    }

    public void setVege(LocalDate vege) {
        this.vege = vege;
    }

    public void parseVege(String vege) throws ValidationException {
        //TODO: validate
        if (Pattern.matches("(1|2)[0-9]{3}-(0|1)[1-9]-[0-3][0-9]", vege)){
            this.vege = makeLocalDateFromString(vege);
        } else {
            ValidationException ve = new ValidationException("A vége dátum formátuma helytelen.");
            ve.setFieldName("vege");
            throw ve;
        }
    }
    private String megjegyzes;

    public String getMegjegyzes() {
        return megjegyzes;
    }

    public void setMegjegyzes(String megjegyzes) {
        this.megjegyzes = megjegyzes;
    }

    public void parseMegjegyzes(String megjegyzes) throws ValidationException {
        //TODO: validate
        if (Pattern.matches("([a-z]| ){0,40}", megjegyzes)) {
            this.megjegyzes = megjegyzes;
        } else {
            ValidationException ve = new ValidationException("A megjegyzés max 40 char és\n csak szöveges");
            ve.setFieldName("megjegyzes");
            throw ve;
        }
    }

    /**
     * Default konstruktor, formai ellenőrzésekhez
     */
    public Jarat() {}

    /**
     * 6 paraméteres konstruktor, lekérdezéseknél használható legfőképp
     * mert ott a kiolvasott értékeket nem szükséges formai ellenőrzés alá vetni
     * @param vonatszam Kötelező tagváltozó, Primary Key, vonatszám
     * @param tipus Kötelező megadni, 4 értéke lehet (EC, IC, Gyors, szemely)
     * @param nap Melyik napokon közlekedik 7 karakter, közlekedik 1, nem közlekedik 0
     * @param kezd (opc) Mikortól közlekedik (dátum)
     * @param vege (opc) Meddig közlekedett (dátum)
     * @param megjegyzes (opc) Megjegyzés
     */
    public Jarat(Integer vonatszam, String tipus, String nap, LocalDate kezd, LocalDate vege, String megjegyzes) {
        super();
        this.vonatszam = vonatszam;
        this.tipus = tipus;
        this.nap = nap;
        this.kezd = kezd;
        this.vege = vege;
        this.megjegyzes = megjegyzes;
    }

    /**
     * A String -> Dátum irányú konverzió megkönnyítésére szolgál.
     * @param datum Paraméterként kapott dátumot tároló String.
     * @return Visszatér egy új LocalDate-tel.
     */
    public LocalDate makeLocalDateFromString(String datum){
        int year;
        int month;
        int day;
        year = Integer.parseInt(datum.substring(0,4));
        month = Integer.parseInt(datum.substring(5,7));
        day = Integer.parseInt(datum.substring(8,10));
        return LocalDate.of(year, month, day);
    }
}
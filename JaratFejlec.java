package model;

import java.time.LocalDate;

import dal.exceptions.ValidationException;

public class JaratFejlec {
    private Integer vonatszam;

    public Integer getVonatszam() {
        return vonatszam;
    }

    public void setVonatszam(Integer vonatszam) {
        this.vonatszam = vonatszam;
    }

    private String tipus;

    public String getTipus() {
        return tipus;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    private String megjegyzes;

    public String getMegjegyzes() {
        return megjegyzes;
    }

    public void setMegjegyzes(String megjegyzes) {
        this.megjegyzes = megjegyzes;
    }

    /**
     * Default konstruktor, ha szükség van rá
     */
    public JaratFejlec() {}

    /**
     * A search listájához használt konstruktor
     * @param vonatszam Kötelező tagváltozó, Primary Key, vonatszám
     * @param tipus Kötelező megadni, 4 értéke lehet (EC, IC, Gyors, szemely)
     * @param megjegyzes (opc) Megjegyzes
     */
    public JaratFejlec(Integer vonatszam, String tipus, String megjegyzes) {
        super();
        this.vonatszam = vonatszam;
        this.tipus = tipus;
        this.megjegyzes = megjegyzes;
    }
}
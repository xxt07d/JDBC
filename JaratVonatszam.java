package model;

import java.time.LocalDate;

import dal.exceptions.ValidationException;

public class JaratVonatszam {
    private Integer vonatszam;

    public Integer getVonatszam() {
        return vonatszam;
    }

    public void setVonatszam(Integer vonatszam) {
        this.vonatszam = vonatszam;
    }

    public JaratVonatszam() {}
}
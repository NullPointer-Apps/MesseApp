package com.messedagliavr.messeapp.Objects;


public class Assenza {
    String tipo;
    String mese;
    int giorno;
    String tipoR;
    boolean giustificata = true;

    public Assenza() {
    }

    public boolean isRitardo() {
        return tipo.equals("R");
    }

    public String getTipoR() {
        if (tipoR.equals("ritardo breve")) tipoR = "Breve";
        return tipoR;
    }

    public void setTipoR(String tipoR) {
        this.tipoR = tipoR;
    }

    public String getTipo() {
        switch (tipo) {
            case "R":
                return "Ritardo";
            case "A":
                return "Assenza";
            case "U":
                return "Uscita";
        }
        return tipoR;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getGiorno() {
        return giorno;
    }

    public void setGiorno(int giorno) {
        this.giorno = giorno;
    }

    public boolean isGiustificata() {
        return giustificata;
    }

    public void setGiustificata(boolean g) {
        giustificata = g;
    }

    public String getMeseS() {
        return mese.substring(0, 3);
    }

    public String getGiornoS() {
        if (giorno < 10) {
            return "0" + giorno;
        } else return "" + giorno;
    }

    public int getMese() {
        switch (mese) {
            case "gennaio":
                return 1;
            case "febbraio":
                return 2;
            case "marzo":
                return 3;
            case "aprile":
                return 4;
            case "maggio":
                return 5;
            case "giugno":
                return 6;
            case "luglio":
                return 7;
            case "agosto":
                return 8;
            case "settembre":
                return 9;
            case "ottobre":
                return 10;
            case "novembre":
                return 11;
            case "dicembre":
                return 12;
            default:
                return 0;
        }
    }

    public void setMese(String mese) {
        this.mese = mese;
    }

}

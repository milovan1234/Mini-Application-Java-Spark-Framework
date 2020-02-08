package models;

public class Automobil {
    private int id;
    private String marka;
    private String model;
    private int cena;
    private int godiste;
    private int kubikaza;
    private String karoserija;
    private String gorivo;
    private String menjac;
    private String slika;
    public Automobil(int id,String marka, String model, int cena, int godiste,int kubikaza, String karoserija,
                     String gorivo, String menjac, String slika){
        this.id = id;
        this.marka = marka;
        this.model = model;
        this.cena = cena;
        this.godiste = godiste;
        this.kubikaza = kubikaza;
        this.karoserija = karoserija;
        this.gorivo = gorivo;
        this.menjac = menjac;
        this.slika = slika;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarka() {
        return marka;
    }

    public void setMarka(String marka) {
        this.marka = marka;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getCena() {
        return cena;
    }

    public void setCena(int cena) {
        this.cena = cena;
    }

    public int getGodiste() {
        return godiste;
    }

    public void setGodiste(int godiste) {
        this.godiste = godiste;
    }

    public int getKubikaza() {
        return kubikaza;
    }

    public void setKubikaza(int kubikaza) {
        this.kubikaza = kubikaza;
    }

    public String getKaroserija() {
        return karoserija;
    }

    public void setKaroserija(String karoserija) {
        this.karoserija = karoserija;
    }

    public String getGorivo() {
        return gorivo;
    }

    public void setGorivo(String gorivo) {
        this.gorivo = gorivo;
    }

    public String getMenjac() {
        return menjac;
    }

    public void setMenjac(String menjac) {
        this.menjac = menjac;
    }

    public String getSlika() {
        return slika;
    }

    public void setSlika(String slika) {
        this.slika = slika;
    }
}

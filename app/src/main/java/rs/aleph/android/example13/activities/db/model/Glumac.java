package rs.aleph.android.example13.activities.db.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Date;

import static rs.aleph.android.example13.activities.db.model.Film.TABELA_IME;
import static rs.aleph.android.example13.activities.db.model.Glumac.POLJE_FILM;
import static rs.aleph.android.example13.activities.db.model.Glumac.POLJE_RODJENJE;


@DatabaseTable(tableName = Glumac.TABELA_IME)
public class Glumac {

    public static final String TABELA_IME = "glumac";
    public static final String POLJE_ID = "id";
    public static final String POLJE_IME = "ime";
    public static final String POLJE_BIOGRAFIJA = "biografija";
    public static final String POLJE_OCENA = "ocena";
    public static final String POLJE_RODJENJE = "datum";
    public static final String POLJE_FILM = "film";


    @DatabaseField(columnName = POLJE_ID, generatedId = true)
    private int glumacId;

    @DatabaseField(columnName = POLJE_IME)
    private String glumacIme;

    @DatabaseField(columnName = POLJE_BIOGRAFIJA)
    private String glumacBiografija;

    @DatabaseField(columnName = POLJE_OCENA)
    private double glumacOcena;

    @DatabaseField(columnName = POLJE_RODJENJE)
    private Date glumacDatumRodjenja;

    @ForeignCollectionField(columnName = Glumac.POLJE_FILM, eager = true)
    private ForeignCollection<Film> film;

    public Glumac() {
    }



    public int getGlumacId() {
        return glumacId;
    }

    public void setGlumacId(int glumacId) {
        this.glumacId = glumacId;
    }

    public String getGlumacIme() {
        return glumacIme;
    }

    public void setGlumacIme(String glumacIme) {
        this.glumacIme = glumacIme;
    }

    public String getGlumacBiografija() {
        return glumacBiografija;
    }

    public void setGlumacBiografija(String glumacBiografija) {
        this.glumacBiografija = glumacBiografija;
    }

    public double getGlumacOcena() {
        return glumacOcena;
    }

    public void setGlumacOcena(double glumacOcena) {
        this.glumacOcena = glumacOcena;
    }

    public Date getGlumacDatumRodjenja() {
        return glumacDatumRodjenja;
    }

    public void setGlumacDatumRodjenja(Date glumacDatumRodjenja) {
        this.glumacDatumRodjenja = glumacDatumRodjenja;
    }

    public ForeignCollection<Film> getFilm() {
        return film;
    }

    public void setFilm(ForeignCollection<Film> film) {
        this.film = film;
    }

    @Override
    public String toString() {
        return glumacIme;
    }
}

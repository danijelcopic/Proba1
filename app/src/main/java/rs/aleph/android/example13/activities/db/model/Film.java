package rs.aleph.android.example13.activities.db.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import static rs.aleph.android.example13.activities.db.model.Film.POLJE_GLUMAC;
import static rs.aleph.android.example13.activities.db.model.Glumac.POLJE_FILM;
import static rs.aleph.android.example13.activities.db.model.Glumac.POLJE_ID;
import static rs.aleph.android.example13.activities.db.model.Glumac.POLJE_IME;

@DatabaseTable(tableName = Film.TABELA_IME)
public class Film {

    public static final String TABELA_IME = "film";
    public static final String POLJE_ID = "id";
    public static final String POLJE_NAZIV = "naziv";
    public static final String POLJE_ZANR = "zanr";
    public static final String POLJE_GODINA_IZLASKA = "godinaIzlaska";
    public static final String POLJE_GLUMAC = "glumac";


    @DatabaseField(columnName = POLJE_ID, generatedId = true)
    private int filmId;

    @DatabaseField(columnName = POLJE_NAZIV)
    private String filmNaziv;

    @DatabaseField(columnName = POLJE_ZANR)
    private String filmZanr;

    @DatabaseField(columnName = POLJE_GODINA_IZLASKA)
    private int filmGodinaIzlaska;

    @DatabaseField(columnName = Film.POLJE_GLUMAC, foreign = true,foreignAutoRefresh = true)
    private Glumac glumac;


    public Film() {
    }



    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public String getFilmNaziv() {
        return filmNaziv;
    }

    public void setFilmNaziv(String filmNaziv) {
        this.filmNaziv = filmNaziv;
    }

    public String getFilmZanr() {
        return filmZanr;
    }

    public void setFilmZanr(String filmZanr) {
        this.filmZanr = filmZanr;
    }

    public int getFilmGodinaIzlaska() {
        return filmGodinaIzlaska;
    }

    public void setFilmGodinaIzlaska(int filmGodinaIzlaska) {
        this.filmGodinaIzlaska = filmGodinaIzlaska;
    }

    public Glumac getGlumac() {
        return glumac;
    }

    public void setGlumac(Glumac glumac) {
        this.glumac = glumac;
    }

    @Override
    public String toString() {
        return filmNaziv;
    }
}

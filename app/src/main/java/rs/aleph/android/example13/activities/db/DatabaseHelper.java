package rs.aleph.android.example13.activities.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import rs.aleph.android.example13.activities.db.model.Actor;
import rs.aleph.android.example13.activities.db.model.Movie;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper{


    private static final String DATABASE_NAME    = "ormlite1.db";


    private static final int    DATABASE_VERSION = 10;

    private Dao<Actor, Integer> mGlumacDao = null;
    private Dao<Movie,Integer> mFilmDao = null;

    //Potrebno je dodati konstruktor zbog pravilne inicijalizacije biblioteke
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Prilikom kreiranja baze potrebno je da pozovemo odgovarajuce metode biblioteke
    //prilikom kreiranja moramo pozvati TableUtils.createTable za svaku tabelu koju imamo
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Movie.class);
            TableUtils.createTable(connectionSource, Actor.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //kada zelimo da izmenomo tabele, moramo pozvati TableUtils.dropTable za sve tabele koje imamo
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Actor.class, true);
            TableUtils.dropTable(connectionSource, Movie.class,true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //jedan Dao objekat sa kojim komuniciramo. Ukoliko imamo vise tabela
    //potrebno je napraviti Dao objekat za svaku tabelu
    public Dao<Actor, Integer> getGlumacDao() throws SQLException {
        if (mGlumacDao == null) {
            mGlumacDao = getDao(Actor.class);
        }

        return mGlumacDao;
    }
    public Dao<Movie,Integer> getFilmDao() throws SQLException{
        if (mFilmDao == null){
            mFilmDao = getDao(Movie.class);
        }
        return mFilmDao;
    }

    //obavezno prilikom zatvarnaj rada sa bazom osloboditi resurse
    @Override
    public void close() {
        mGlumacDao = null;
        mFilmDao = null;

        super.close();
    }
}

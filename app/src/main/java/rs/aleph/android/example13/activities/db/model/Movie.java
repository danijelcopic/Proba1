package rs.aleph.android.example13.activities.db.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Movie.TABLE_NAME_MOVIE)
public class Movie {

    public static final String TABLE_NAME_MOVIE = "movie";
    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_NAME = "name";
    public static final String FIELD_NAME_GENRE = "genre";
    public static final String FIELD_NAME_YEAR = "year";
    public static final String FIELD_NAME_ACTOR = "actor";


    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = FIELD_NAME_NAME)
    private String mName;

    @DatabaseField(columnName = FIELD_NAME_GENRE)
    private String mGenre;

    @DatabaseField(columnName = FIELD_NAME_YEAR)
    private int mYear;

    @DatabaseField(columnName = Movie.FIELD_NAME_ACTOR, foreign = true,foreignAutoRefresh = true)
    private Actor actor;


    public Movie() {
    }



    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmGenre() {
        return mGenre;
    }

    public void setmGenre(String mGenre) {
        this.mGenre = mGenre;
    }

    public int getmYear() {
        return mYear;
    }

    public void setmYear(int mYear) {
        this.mYear = mYear;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    @Override
    public String toString() {
        return mName;
    }
}

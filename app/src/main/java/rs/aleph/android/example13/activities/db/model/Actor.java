package rs.aleph.android.example13.activities.db.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;


@DatabaseTable(tableName = Actor.TABLE_NAME_ACTOR)
public class Actor {

    public static final String TABLE_NAME_ACTOR = "actor";
    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_NAME = "name";
    public static final String FIELD_NAME_BIOGRAPHY = "biography";
    public static final String FIELD_NAME_RATING = "rating";
    public static final String FIELD_NAME_BIRTHDAY = "birthday";
    public static final String FIELD_NAME_MOVIE = "movie";


    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = FIELD_NAME_NAME)
    private String mName;

    @DatabaseField(columnName = FIELD_NAME_BIOGRAPHY)
    private String mBiography;

    @DatabaseField(columnName = FIELD_NAME_RATING)
    private double mRating;

    @DatabaseField(columnName = FIELD_NAME_BIRTHDAY)
    private Date mBirthday;

    @ForeignCollectionField(columnName = Actor.FIELD_NAME_MOVIE, eager = true)
    private ForeignCollection<Movie> movie;

    public Actor() {
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

    public String getmBiography() {
        return mBiography;
    }

    public void setmBiography(String mBiography) {
        this.mBiography = mBiography;
    }

    public double getmRating() {
        return mRating;
    }

    public void setmRating(double mRating) {
        this.mRating = mRating;
    }

    public Date getmBirthday() {
        return mBirthday;
    }

    public void setmBirthday(Date mBirthday) {
        this.mBirthday = mBirthday;
    }

    public ForeignCollection<Movie> getMovie() {
        return movie;
    }

    public void setMovie(ForeignCollection<Movie> movie) {
        this.movie = movie;
    }

    @Override
    public String toString() {
        return mName;
    }
}

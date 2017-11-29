package rs.aleph.android.example13.activities.activity;


import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rs.aleph.android.example13.R;
import rs.aleph.android.example13.activities.db.DatabaseHelper;
import rs.aleph.android.example13.activities.db.model.Movie;
import rs.aleph.android.example13.activities.db.model.Actor;


import static rs.aleph.android.example13.activities.activity.FirstActivity.NOTIF_STATUS;
import static rs.aleph.android.example13.activities.activity.FirstActivity.NOTIF_TOAST;


public class SecondActivity extends AppCompatActivity  {



    private int position = 0;

    private DatabaseHelper databaseHelper;
    private Actor actor;
    private SharedPreferences preferences;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // TOOLBAR
        // aktiviranje toolbara 2 koji je drugaciji od onog iz prve aktivnosti
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_second);
        setSupportActionBar(toolbar);


        // prikazivanje strelice u nazad u toolbaru ... mora se u manifestu definisati zavisnost parentActivityName
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }


        // status podesavanja
        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        // hvatamo intent iz prve aktivnosti
        Intent intent = getIntent();
        position = intent.getExtras().getInt("position");

        // na osnovu dobijene pozicije od intenta, pupunjavamo polja u drugoj aktivnosti
        try {

            actor = getDatabaseHelper().getGlumacDao().queryForId((int) position);

            String name = actor.getmName();

            String biography = actor.getmBiography();

            double rating = actor.getmRating();
            String stringRating = Double.toString(rating);

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
            String date = sdf.format(actor.getmBirthday());

            //ispisujemo ime glumca
            TextView actorName = (TextView) findViewById(R.id.name);
            actorName.setText(name);

            //ispisujemo  datum rodjenja glumca
            TextView actorBirthday = (TextView) findViewById(R.id.birthday);
            actorBirthday.setText(date);

            //ispisujemo  biografiju glumca
            TextView actorBiography = (TextView) findViewById(R.id.biography);
            actorBiography.setText(biography);

            //ispisujemo  ocenu glumca
            TextView actorRating = (TextView) findViewById(R.id.rating);
            actorRating.setText(stringRating);


            // prikazujemo listu filmova u drugoj aktivnosti
            final ListView listView = (ListView) findViewById(R.id.list_movie);

            List<Movie> movies = getDatabaseHelper().getFilmDao(). // konstruisemo QueryBuilder
                    queryBuilder().
                    where().
                    eq(Movie.FIELD_NAME_ACTOR, position).
                    query();

            List<String> moviesName = new ArrayList<>();
            for (Movie f : movies) {
                moviesName.add(f.getmName());
            }
            ListAdapter adapter = new ArrayAdapter<String>(SecondActivity.this, R.layout.list_item_movie, moviesName);
            listView.setAdapter(adapter);


            // sta se desi kada kliknemo na film u drugoj aktivnosti (toast sa podacima)
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Movie f = (Movie) listView.getItemAtPosition(position);
                    Toast.makeText(SecondActivity.this, " FILM: " + f.getmName() + "\n" +
                                    " ZANR: " + f.getmGenre() + "\n" +
                                    " GODINA: " + f.getmYear() + "\n" +
                                    " GLUMAC: " + f.getActor(),
                            Toast.LENGTH_LONG).show();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }









    /**
     *
     * NOTIFICATION
     *
     */

    // prikazivanje poruka u notification baru (status bar)
    private void showStatusMesage(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setContentTitle("Actor");
        builder.setContentText(message);

        // slicica u notification drawer-u
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_movietable);
        builder.setLargeIcon(bm);

        notificationManager.notify(1, builder.build());
    }


    // //provera podesavanja (toast ili notification bar) .... ovo pozivamo kada kliknemo na ikonicu u Tolbaru
    private void showMessage(String message) {

        boolean toast = preferences.getBoolean(NOTIF_TOAST, false);
        boolean status = preferences.getBoolean(NOTIF_STATUS, false);

        if (toast) {  // ako je aktivan toast prikazi ovo
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

        if (status) {  // ako je aktivan statusbar pozovi metodu ... i prosledi joj poruku (tekst) koji ce ispisati
            showStatusMesage(message);
        }
    }










    /**
     *
     * MENU
     *
     */
    // prikaz menija
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // sta se desi kada kliknemo na stavke iz menija
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.action_picture:

                showRandomImage();
                showMessage("Show some picture");

                break;

            // kada pritisnemo ikonicu za menjanje podataka
            case R.id.action_edit:

                edit();  // pozivamo metodu edit()

                break;

            // kada pritisnemo ikonicu za brisanje
            case R.id.action_delete:
                try {
                    getDatabaseHelper().getGlumacDao().delete(actor);

                    showMessage("Actor is deleted");

                    finish();


                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            // kada pritisnemo ikonicu za dodavanje filma
            case R.id.action_add_film:

                final Dialog dialog = new Dialog(SecondActivity.this);
                dialog.setContentView(R.layout.dialog_movie);


                final EditText movieName = (EditText) dialog.findViewById(R.id.input_movie_name);
                final EditText movieGenre = (EditText) dialog.findViewById(R.id.input_movie_genre);
                final EditText movieYear = (EditText) dialog.findViewById(R.id.input_movie_year);


                Button ok = (Button) dialog.findViewById(R.id.btn_ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String name = movieName.getText().toString();
                        if (name.isEmpty()) {
                            Toast.makeText(SecondActivity.this, "Must be entered", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String genre = movieGenre.getText().toString();
                        if (genre.isEmpty()) {
                            Toast.makeText(SecondActivity.this, "Must be entered", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int year = 0;
                        try {
                            year = Integer.parseInt(movieYear.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(SecondActivity.this, "Must be entered in format: dd.mm.yyyy.", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        Movie movie = new Movie();
                        movie.setmName(name);
                        movie.setmGenre(genre);
                        movie.setmYear(year);
                        movie.setActor(actor);


                        try {
                            getDatabaseHelper().getFilmDao().create(movie);

                            refresh();

                            showMessage("New movie is added");

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();

                    }
                });

                Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

                break;

        }
        return super.onOptionsItemSelected(item);
    }







    /**
     *
     * BAZA
     *
     */

    //Metoda koja komunicira sa bazom podataka
    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    // osvezavanje baze
    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }



    // refresh() prikazuje novi sadrzaj.Povucemo nov sadrzaj iz baze i popunimo listu filmova
    private void refresh() {
        ListView listview = (ListView) findViewById(R.id.list_movie);
        if (listview != null) {
            ArrayAdapter<Movie> adapter = (ArrayAdapter<Movie>) listview.getAdapter();
            if (adapter != null) {

                try {
                    adapter.clear();

                    // konstruisemo QueryBuilder
                    List<Movie> movies = getDatabaseHelper().getFilmDao()
                            .queryBuilder()
                            .where()
                            .eq(Movie.FIELD_NAME_ACTOR, actor.getmId())
                            .query();


                    adapter.addAll(movies);
                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();

                }
            }
        }
    }





    // EDIT
    // pozivamo pri izmeni podataka ....
    private void edit(){

        final Dialog dialog = new Dialog(SecondActivity.this);
        dialog.setContentView(R.layout.dialog_actor_edit);

        if (actor != null){

            final EditText actorName = (EditText) dialog.findViewById(R.id.input_actor_name);
            final EditText actorBiography = (EditText) dialog.findViewById(R.id.input_actor_biography);
            final EditText actorRating = (EditText) dialog.findViewById(R.id.input_actor_rating);
            final EditText actorBirthday = (EditText) dialog.findViewById(R.id.input_actor_birthday);


            // update podataka u dialog pre edita
            actorName.setText(actor.getmName());
            actorBiography.setText(actor.getmBiography());

            double rating = actor.getmRating();
            String stringRating = Double.toString(rating);
            actorRating.setText(stringRating);

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
            String date = sdf.format(actor.getmBirthday());
            actorBirthday.setText(date);


            // ok
            Button ok = (Button) dialog.findViewById(R.id.btn_ok);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String name = actorName.getText().toString();
                    if (name.isEmpty()) {
                        Toast.makeText(SecondActivity.this, "Must be entered", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String biography = actorBiography.getText().toString();
                    if (biography.isEmpty()) {
                        Toast.makeText(SecondActivity.this, "Must be entered", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double rating = 0;
                    try {
                        rating = Double.parseDouble(actorRating.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(SecondActivity.this, "Must be number.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
                    Date date = null;
                    try {
                        date = sdf.parse(actorBirthday.getText().toString());
                    } catch (ParseException e) {
                        Toast.makeText(SecondActivity.this, "Must be entered in format: dd.mm.yyyy.", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    actor.setmName(name);
                    actor.setmBiography(biography);
                    actor.setmRating(rating);
                    actor.setmBirthday(date);



                    try {

                        getDatabaseHelper().getGlumacDao().update(actor);

                        //provera podesavanja (toast ili notification bar)
                        boolean toast = preferences.getBoolean(NOTIF_TOAST, false);
                        boolean status = preferences.getBoolean(NOTIF_STATUS, false);

                        if (toast){
                            Toast.makeText(SecondActivity.this, "Actor is updated" , Toast.LENGTH_SHORT).show();
                        }

                        if (status){
                            showStatusMesage("Actor is updated");
                        }

                        refresh(); // osvezavanje baze



                       finish();  // ovo sam morao da bi se vratio na prvu aktivnost i osvezio bazu novim podacima

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();



                }
            });


            // cancel
            Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();


        }
    }






    // picasso
    private void showRandomImage() {
        final Dialog dialog = new Dialog(SecondActivity.this);
        dialog.setContentView(R.layout.picasso_layout);

        ImageView image = (ImageView) dialog.findViewById(R.id.picasso_image);

        Picasso.with(SecondActivity.this).load("https://source.unsplash.com/random").into(image);

        Button close = (Button) dialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


}

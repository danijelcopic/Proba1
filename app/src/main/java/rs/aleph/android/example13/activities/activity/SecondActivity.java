package rs.aleph.android.example13.activities.activity;


import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.RatingBar;
import android.widget.Spinner;
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
import rs.aleph.android.example13.activities.db.model.Film;
import rs.aleph.android.example13.activities.db.model.Glumac;

import static rs.aleph.android.example13.activities.activity.FirstActivity.NOTIF_STATUS;
import static rs.aleph.android.example13.activities.activity.FirstActivity.NOTIF_TOAST;


public class SecondActivity extends AppCompatActivity  {

    private int position = 0;

    private DatabaseHelper databaseHelper;
    private Glumac glumac;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        // TOOLBAR
        // aktiviranje toolbara 2 koji je drugaciji od onog iz prve aktivnosti
        Toolbar toolbar = (Toolbar) findViewById(R.id.second_toolbar);
        setSupportActionBar(toolbar);


        // prikazivanje strelice u nazad u toolbaru ... mora se u manifestu definisati zavisnost parentActivityName
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }


       /// mozda potrebno za edit ????
        if (savedInstanceState != null) {
            glumac = new Glumac();
            glumac.setGlumacIme(savedInstanceState.getString("ime"));
            glumac.setGlumacBiografija(savedInstanceState.getString("biografija"));
            glumac.setGlumacOcena(savedInstanceState.getDouble("ocena"));
            //jos datum ????
        }



        // status podesavanja
        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        // hvatamo intent iz prve aktivnosti
        Intent intent = getIntent();
        position = intent.getExtras().getInt("position");

        // na osnovu dobijene pozicije od intenta, pupunjavamo polja u drugoj aktivnosti
        try {

            glumac = getDatabaseHelper().getGlumacDao().queryForId((int) position);
            String ime = glumac.getGlumacIme();
            String biografija = glumac.getGlumacBiografija();
            double ocena = glumac.getGlumacOcena();
            String stringOcena = Double.toString(ocena);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
            String datum = sdf.format(glumac.getGlumacDatumRodjenja());

            //ispisujemo ime glumca
            TextView imeGlumac = (TextView) findViewById(R.id.imeGlumac);
            imeGlumac.setText(ime);

            //ispisujemo  datum rodjenja glumca
            TextView datumGlumac = (TextView) findViewById(R.id.inputDatumRodjenjaGlumac);
            datumGlumac.setText(datum);

            //ispisujemo  biografiju glumca
            TextView biografijaGlumac = (TextView) findViewById(R.id.inputBiografijaGlumac);
            biografijaGlumac.setText(biografija);

            //ispisujemo  ocenu glumca
            TextView ocenaGlumac = (TextView) findViewById(R.id.inputOcenaGlumac);
            ocenaGlumac.setText(stringOcena);


            // prikazujemo listu filmova u drugoj aktivnosti
            final ListView listView = (ListView) findViewById(R.id.inputListaFilmovaGlumac);

            List<Film> filmovi = getDatabaseHelper().getFilmDao(). // konstruisemo QueryBuilder
                    queryBuilder().
                    where().
                    eq(Film.POLJE_GLUMAC, position).
                    query();

            List<String> filmoviNazivi = new ArrayList<>();
            for (Film f : filmovi) {
                filmoviNazivi.add(f.getFilmNaziv());
            }
            ListAdapter adapter = new ArrayAdapter<String>(SecondActivity.this, R.layout.list_item_film, filmoviNazivi);
            listView.setAdapter(adapter);


            // sta se desi kada kliknemo na film u drugoj aktivnosti (toast sa podacima)
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Film f = (Film) listView.getItemAtPosition(position);
                    Toast.makeText(SecondActivity.this, " FILM: " + f.getFilmNaziv() + "\n" +
                                    " ZANR: " + f.getFilmZanr() + "\n" +
                                    " GODINA: " + f.getFilmGodinaIzlaska() + "\n" +
                                    " GLUMAC: " + f.getGlumac(),
                            Toast.LENGTH_LONG).show();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    // prikazivanje poruka u notification baru (status bar)
    private void showStatusMesage(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setContentTitle("Ispit");
        builder.setContentText(message);

        // slicica u notification drawer-u
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_movietable);
        builder.setLargeIcon(bm);

        notificationManager.notify(1, builder.build());
    }


    // MENU
    // prikaz menija
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.second_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // sta se desi kada kliknemo na stavke iz menija
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.action_picture:

                showRandomImage();
                showMessage("Evo neke slike");

                break;

            // kada pritisnemo ikonicu za menjanje podataka
            case R.id.action_edit:

                edit();

                break;

            // kada pritisnemo ikonicu za brisanje
            case R.id.action_delete:
                try {
                    getDatabaseHelper().getGlumacDao().delete(glumac);

                    showMessage("Glumac je obrisan");

                    finish();


                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            // kada pritisnemo ikonicu za dodavanje filma
            case R.id.action_add_film:

                final Dialog dialog = new Dialog(SecondActivity.this);
                dialog.setContentView(R.layout.dialog_film);


                final EditText filmNaziv = (EditText) dialog.findViewById(R.id.film_naziv);
                final EditText filmZanr = (EditText) dialog.findViewById(R.id.film_zanr);
                final EditText filmGodina = (EditText) dialog.findViewById(R.id.film_godina);


                Button ok = (Button) dialog.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String naziv = filmNaziv.getText().toString();
                        if (naziv.isEmpty()) {
                            Toast.makeText(SecondActivity.this, "Ime filma ne sme biti prazno", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String zanr = filmZanr.getText().toString();
                        if (zanr.isEmpty()) {
                            Toast.makeText(SecondActivity.this, "Zanr filma ne sme biti prazan", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int godina = 0;
                        try {
                            godina = Integer.parseInt(filmGodina.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(SecondActivity.this, "Godina mora biti broj.", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        Film film = new Film();
                        film.setFilmNaziv(naziv);
                        film.setFilmZanr(zanr);
                        film.setFilmGodinaIzlaska(godina);
                        film.setGlumac(glumac);


                        try {
                            getDatabaseHelper().getFilmDao().create(film);

                            refresh();

                            showMessage("Novi film je dodan");

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();

                    }
                });

                Button cancel = (Button) dialog.findViewById(R.id.cancel);
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
        ListView listview = (ListView) findViewById(R.id.inputListaFilmovaGlumac);
        if (listview != null) {
            ArrayAdapter<Film> adapter = (ArrayAdapter<Film>) listview.getAdapter();
            if (adapter != null) {

                try {
                    adapter.clear();

                    // konstruisemo QueryBuilder
                    List<Film> films = getDatabaseHelper().getFilmDao()
                            .queryBuilder()
                            .where()
                            .eq(Film.POLJE_GLUMAC, glumac.getGlumacId())
                            .query();


                    adapter.addAll(films);
                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();

                }
            }
        }
    }



    // mozda isto potrebno za edit ????
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            savedInstanceState.putInt("id", glumac.getGlumacId());
            savedInstanceState.putString("ime", glumac.getGlumacIme());
            savedInstanceState.putString("biografija", glumac.getGlumacBiografija());
            savedInstanceState.putDouble("ocena", glumac.getGlumacOcena());
            // jos datum ?????

        }
    }




    // pozivamo pri izmeni podataka .... treba doraditi ????? ... uvuci postojece podatke iz baze ili mozda Bundle ????
    private void edit(){

        final Dialog dialog = new Dialog(SecondActivity.this);
        dialog.setContentView(R.layout.dialog_glumac_edit);

        if (glumac != null){

            final EditText glumacIme = (EditText) dialog.findViewById(R.id.glumac_ime);
            final EditText glumacBiografija = (EditText) dialog.findViewById(R.id.glumac_biografija);
            final EditText glumacOcena = (EditText) dialog.findViewById(R.id.glumac_ocena);
            final EditText glumacDatumRodjenja = (EditText) dialog.findViewById(R.id.glumac_datum_rodjenja);


            Button ok = (Button) dialog.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String ime = glumacIme.getText().toString();
                    if (ime.isEmpty()) {
                        Toast.makeText(SecondActivity.this, "Ime glumca ne sme biti prazno", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String biografija = glumacBiografija.getText().toString();
                    if (biografija.isEmpty()) {
                        Toast.makeText(SecondActivity.this, "Podaci o biografiji ne smeju biti prazni", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double ocena = 0;
                    try {
                        ocena = Double.parseDouble(glumacOcena.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(SecondActivity.this, "Ocena mora biti broj.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
                    Date datum = null;
                    try {
                        datum = sdf.parse(glumacDatumRodjenja.getText().toString());
                    } catch (ParseException e) {
                        Toast.makeText(SecondActivity.this, "Datum mora biti u formatu dd.mm.yyyy.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Glumac glumac = new Glumac();
                    glumac.setGlumacIme(ime);
                    glumac.setGlumacBiografija(biografija);
                    glumac.setGlumacOcena(ocena);
                    glumac.setGlumacDatumRodjenja(datum);


                    try {
                        getDatabaseHelper().getGlumacDao().update(glumac);

                        //provera podesavanja (toast ili notification bar)
                        boolean toast = preferences.getBoolean(NOTIF_TOAST, false);
                        boolean status = preferences.getBoolean(NOTIF_STATUS, false);

                        if (toast){
                            Toast.makeText(SecondActivity.this, "Podaci o glumcu su promenjeni" , Toast.LENGTH_SHORT).show();
                        }

                        if (status){
                            showStatusMesage("Podaci o glumcu su promenjeni");
                        }

                        refresh(); // osvezavanje baze

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();

                }
            });

            Button cancel = (Button) dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

            try {
                getDatabaseHelper().getGlumacDao().update(glumac);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            refresh();
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

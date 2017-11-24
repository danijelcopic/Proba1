package rs.aleph.android.example13.activities.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.io.InputStream;
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

import static android.R.attr.name;
import static android.R.attr.rating;
import static rs.aleph.android.example13.R.string.biografija;


public class SecondActivity extends AppCompatActivity {

    private int position = 0;
    private static int NOTIFICATION_ID = 1;

    private DatabaseHelper databaseHelper;
    private Glumac glumac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        // TOOLBAR
        // aktiviranje toolbara 2 koji je drugaciji od onog iz prve aktivnosti
        Toolbar toolbar = (Toolbar) findViewById(R.id.second_toolbar);
        setSupportActionBar(toolbar);


        // hvatamo intent iz prve aktivnosti
        Intent intent = getIntent();
        position = intent.getExtras().getInt("position");

        // na osnovu dobijene pozicije od intenta, pupunjavamo polja u drugoj aktivnosti
        try {

            glumac = getDatabaseHelper().getGlumacDao().queryForId((int)position);
            String ime = glumac.getGlumacIme();
            String biografija = glumac.getGlumacBiografija();
            double ocena = glumac.getGlumacOcena();
            String stringOcena = Double.toString(ocena);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
            String datum = sdf.format(glumac.getGlumacDatumRodjenja());

            //ispisujemo ime glumca
            TextView imeGlumac  = (TextView)findViewById(R.id.imeGlumac);
            imeGlumac.setText(ime);

            //ispisujemo  datum rodjenja glumca
            TextView datumGlumac  = (TextView)findViewById(R.id.inputDatumRodjenjaGlumac);
            datumGlumac.setText(datum);

            //ispisujemo  biografiju glumca
            TextView biografijaGlumac = (TextView)findViewById(R.id.inputBiografijaGlumac);
            biografijaGlumac.setText(biografija);

            //ispisujemo  ocenu glumca
            TextView ocenaGlumac = (TextView)findViewById(R.id.inputOcenaGlumac);
            ocenaGlumac.setText(stringOcena);


            ListView listView = (ListView)findViewById(R.id.inputListaFilmovaGlumac);
            List<Film> filmovi = getDatabaseHelper().getFilmDao().
                    queryBuilder().
                    where().
                    eq(Film.POLJE_GLUMAC, position).
                    query();
            List<String> filmoviNazivi = new ArrayList<>();
            for (Film f : filmovi) {
                filmoviNazivi.add(f.getFilmNaziv());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(SecondActivity.this, R.layout.list_item_film, filmoviNazivi);
            listView.setAdapter(adapter);

        } catch (SQLException e) {
            e.printStackTrace();
        }





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
            case R.id.action_edit:
                // sta se desi kada pritisnemo edit
                break;
            case R.id.action_delete:
                // sta se desi kada pritisnemo delete
                break;
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



                        try {
                            getDatabaseHelper().getFilmDao().create(film);
                            refresh();
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



    // refresh() prikazuje novi sadrzaj.Povucemo nov sadrzaj iz baze i popunimo listu filmova
    private void refresh() {
        ListView listview = (ListView) findViewById(R.id.inputListaFilmovaGlumac);
        if (listview != null) {
            ArrayAdapter<Film> adapter = (ArrayAdapter<Film>) listview.getAdapter();
            if (adapter != null) {
                adapter.clear();
                try {
                    List<Film> list = getDatabaseHelper().getFilmDao().queryForAll();
                    adapter.addAll(list);
                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();

                }
            }
        }
    }




     /*// Finds "btnBuy" Button and sets "onClickListener" listener
        Button btnBuy = (Button) findViewById(R.id.btn_buy);
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(v.getContext(), "You've bought " + ActorProvider.getActorById(position).getName() + "!", Toast.LENGTH_LONG);
                toast.show();
            /*}
        });*/
}

package rs.aleph.android.example13.activities.activity;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rs.aleph.android.example13.R;
import rs.aleph.android.example13.activities.db.DatabaseHelper;
import rs.aleph.android.example13.activities.db.model.Glumac;
import rs.aleph.android.example13.activities.dialogs.AboutDialog;



public class FirstActivity extends AppCompatActivity {


    private DatabaseHelper databaseHelper;
    private AlertDialog dialogAlert;
    private static int NOTIFICATION_ID = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);

        // TOOLBAR
        // aktiviranje toolbara
        Toolbar toolbar = (Toolbar) findViewById(R.id.first_toolbar);
        setSupportActionBar(toolbar);


        //  ZA BAZU
        // ucitamo sve podatke iz baze u listu
        List<Glumac> glumci = new ArrayList<Glumac>();
        try {
            glumci = getDatabaseHelper().getGlumacDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        // u String izvucemo iz gornje liste imana i sa adapterom posaljemo na View
        List<String> glumciIme = new ArrayList<String>();
        for (Glumac i : glumci) {
            glumciIme.add(i.getGlumacIme());
        }

        final ListView listView = (ListView)findViewById(R.id.listFirstActivity); // definisemo u koji View saljemo podatke (listFirstActivity)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(FirstActivity.this, R.layout.list_item, glumciIme);  // definisemo kako ce izgledati jedna stavka u View (list_item)
        listView.setAdapter(adapter);


        // sta se desi kada kliknemo na, u ovom slucaju, ime glumca iz liste
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Glumac glumac = (Glumac) listView.getItemAtPosition(position);
                Intent intentGLumac = new Intent(FirstActivity.this, SecondActivity.class);
                intentGLumac.putExtra("position", glumac.getGlumacId());  // saljemo intent o poziciji (id glumca)
                startActivity(intentGLumac);

            }

        });

    }


    /**
     * MENU
     */

    // prikaz menija
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.first_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // sta se desi kada kliknemo na stavke iz menija
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /**
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);  // setujemo SharedPreferences
        final boolean toast = sharedPreferences.getBoolean("@string/pref_toast",true);
        final boolean notification = sharedPreferences.getBoolean("@string/pref_notification",true);
         */

        switch (item.getItemId()) {

            case R.id.action_add: // otvara se dialog za upis u bazu


                final Dialog dialog = new Dialog(FirstActivity.this); // aktiviramo dijalog
                dialog.setContentView(R.layout.dialog_glumac);


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
                            Toast.makeText(FirstActivity.this, "Ime glumca ne sme biti prazno", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String biografija = glumacBiografija.getText().toString();
                        if (biografija.isEmpty()) {
                            Toast.makeText(FirstActivity.this, "Podaci o biografiji ne smeju biti prazni", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        double ocena = 0;
                        try {
                            ocena = Double.parseDouble(glumacOcena.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(FirstActivity.this, "Ocena mora biti broj.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
                        Date datum = null;
                        try {
                            datum = sdf.parse(glumacDatumRodjenja.getText().toString());
                        } catch (ParseException e) {
                            Toast.makeText(FirstActivity.this, "Datum mora biti u formatu dd.mm.yyyy.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Glumac glumac = new Glumac();
                        glumac.setGlumacIme(ime);
                        glumac.setGlumacBiografija(biografija);
                        glumac.setGlumacOcena(ocena);
                        glumac.setGlumacDatumRodjenja(datum);


                        try {
                            getDatabaseHelper().getGlumacDao().create(glumac);
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
            case R.id.action_settings:
                Intent preferences = new Intent(FirstActivity.this, PreferencesActivity.class);  // saljemo intent PreferancesActivity.class
                startActivity(preferences);
                break;

            case R.id.action_about:
                if (dialogAlert == null) {
                    dialogAlert = new AboutDialog(FirstActivity.this).prepareDialog(); // pozivamo prepareDialog() iz klase AboutDialog
                } else {
                    if (dialogAlert.isShowing()) {
                        dialogAlert.dismiss();
                    }

                }
                dialogAlert.show();
                break;

        }

        return super.onOptionsItemSelected(item);
    }






    /**
     * TABELE I BAZA
     */

    //Metoda koja komunicira sa bazom podataka
    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }


    // refresh() prikazuje novi sadrzaj.Povucemo nov sadrzaj iz baze i popunimo listu glumaca
    private void refresh() {
        ListView listview = (ListView) findViewById(R.id.listFirstActivity);
        if (listview != null) {
            ArrayAdapter<Glumac> adapter = (ArrayAdapter<Glumac>) listview.getAdapter();
            if (adapter != null) {
                adapter.clear();
                try {
                    List<Glumac> list = getDatabaseHelper().getGlumacDao().queryForAll();
                    adapter.addAll(list);
                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();

                }
            }
        }
    }



    public void showMessage(String text, String newGlumac) {
        SharedPreferences st = PreferenceManager.getDefaultSharedPreferences(FirstActivity.this);
        String name = st.getString("message", "Toast");
        if (name.equals("Toast")) {
            Toast.makeText(FirstActivity.this, text + "\n" + newGlumac, Toast.LENGTH_LONG).show();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(FirstActivity.this);
            builder.setSmallIcon(R.drawable.ic_action_name);

            builder.setContentTitle(text);
            builder.setContentText(newGlumac);


            // Shows notification with the notification manager (notification ID is used to update the notification later on)
            NotificationManager manager = (NotificationManager) FirstActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1, builder.build());
        }
    }


    // kompatibilnost u nazad
    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        // nakon rada sa bazo podataka potrebno je obavezno
        //osloboditi resurse!
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
package rs.aleph.android.example13.activities.activity;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private SharedPreferences preferences;

    public static String NOTIF_TOAST = "pref_toast";
    public static String NOTIF_STATUS = "pref_notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);

        // TOOLBAR
        // aktiviranje toolbara
        Toolbar toolbar = (Toolbar) findViewById(R.id.first_toolbar);
        setSupportActionBar(toolbar);

        // status podesavanja
        preferences = PreferenceManager.getDefaultSharedPreferences(this);


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

        final ListView listView = (ListView) findViewById(R.id.listFirstActivity); // definisemo u koji View saljemo podatke (listFirstActivity)
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


    // prikazivanje poruka u notification baru (status bar)
    private void showStatusMesage(String message){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setContentTitle("Ispit");
        builder.setContentText(message);

        // ubacio sam smajlija na kraju poruke u Notification Drawer
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_smile);
        builder.setLargeIcon(bm);

        notificationManager.notify(1, builder.build());
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

                            //provera podesavanja (toast ili notification bar)
                            boolean toast = preferences.getBoolean(NOTIF_TOAST, false);
                            boolean status = preferences.getBoolean(NOTIF_STATUS, false);

                            if (toast){
                                Toast.makeText(FirstActivity.this, "Novi glumac je dodan", Toast.LENGTH_SHORT).show();
                            }

                            if (status){
                                showStatusMesage("Novi glumac je dodan");
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



    // kompatibilnost u nazad
    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }


    // ovde refreshujemo bazu kada smo se vratili iz druge aktivnosti (kada je glumac obrisan, pa da se vise ne pokazuje)
    @Override
    protected void onResume() {
        super.onResume();
        refresh();
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
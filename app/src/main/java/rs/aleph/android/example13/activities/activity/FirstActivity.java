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
import rs.aleph.android.example13.activities.db.model.Actor;
import rs.aleph.android.example13.activities.dialogs.AboutDialog;

import static java.lang.Double.parseDouble;


public class FirstActivity extends AppCompatActivity {


    private DatabaseHelper databaseHelper;
    private AlertDialog dialogAlert;
    private SharedPreferences preferences;

    public static String NOTIF_TOAST = "pref_toast";
    public static String NOTIF_STATUS = "pref_notification";

    private static final String TAG = "PERMISSIONS"; // za permission




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);




        // TOOLBAR
        // aktiviranje toolbara
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_first);
        setSupportActionBar(toolbar);

        // status podesavanja
        preferences = PreferenceManager.getDefaultSharedPreferences(this);





        //  ZA BAZU
        // ucitamo sve podatke iz baze u listu
        List<Actor> actors = new ArrayList<Actor>();
        try {
            actors = getDatabaseHelper().getGlumacDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        // u String izvucemo iz gornje liste imana i sa adapterom posaljemo na View
        List<String> actorNames = new ArrayList<String>();
        for (Actor i : actors) {
            actorNames.add(i.getmName());
        }

        final ListView listView = (ListView) findViewById(R.id.list_first_activity); // definisemo u koji View saljemo podatke (listFirstActivity)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(FirstActivity.this, R.layout.list_item, actorNames);  // definisemo kako ce izgledati jedna stavka u View (list_item)
        listView.setAdapter(adapter);


        // sta se desi kada kliknemo na element iz liste
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Actor actor = (Actor) listView.getItemAtPosition(position);
                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                intent.putExtra("position", actor.getmId());  // saljemo intent o poziciji (id glumca)
                startActivity(intent);

            }

        });

    }






    // prikazivanje poruka u notification baru (status bar)
    private void showStatusMesage(String message){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setContentTitle("Actor");
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
        getMenuInflater().inflate(R.menu.menu_first, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // sta se desi kada kliknemo na stavke iz menija
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.action_add: // otvara se dialog za upis u bazu


                final Dialog dialog = new Dialog(FirstActivity.this); // aktiviramo dijalog
                dialog.setContentView(R.layout.dialog_actor);


                final EditText actorName = (EditText) dialog.findViewById(R.id.input_actor_name);
                final EditText actorBiography = (EditText) dialog.findViewById(R.id.input_actor_biography);
                final EditText actorRating = (EditText) dialog.findViewById(R.id.input_actor_rating);
                final EditText actorRatingbar = (EditText) dialog.findViewById(R.id.input_actor_ratingbar);
                final EditText actorBirthday = (EditText) dialog.findViewById(R.id.input_actor_birthday);


                // ok
                Button ok = (Button) dialog.findViewById(R.id.btn_ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String name = actorName.getText().toString();
//                        if (name.isEmpty()) {
//                            Toast.makeText(FirstActivity.this, "Must be entered", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        String biography = actorBiography.getText().toString();
//                        if (biography.isEmpty()) {
//                            Toast.makeText(FirstActivity.this, "Must be entered", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        double rating = 0;
                        try {
                            rating = parseDouble(actorRating.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(FirstActivity.this, "Must be number.", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        float ratingbar = 0;
                        try {
                            ratingbar = Float.parseFloat(actorRatingbar.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(FirstActivity.this, "Must be number [1-5].", Toast.LENGTH_SHORT).show();
                            return;
                        }




                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
                        Date date = null;
                        try {
                            date = sdf.parse(actorBirthday.getText().toString());
                        } catch (ParseException e) {
                            Toast.makeText(FirstActivity.this, "Must be entered in format: dd.mm.yyyy.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Actor actor = new Actor();
                        actor.setmName(name);
                        actor.setmBiography(biography);
                        actor.setmRating(rating);
                        actor.setmRatingbar(ratingbar);
                        actor.setmBirthday(date);


                        try {
                            getDatabaseHelper().getGlumacDao().create(actor);

                            //provera podesavanja (toast ili notification bar)
                            boolean toast = preferences.getBoolean(NOTIF_TOAST, false);
                            boolean status = preferences.getBoolean(NOTIF_STATUS, false);

                            if (toast){
                                Toast.makeText(FirstActivity.this, "New actor is added", Toast.LENGTH_SHORT).show();
                            }

                            if (status){
                                showStatusMesage("New actor is added");
                            }

                            refresh(); // osvezavanje baze

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

                break;
            case R.id.action_settings:
                Intent preferences = new Intent(FirstActivity.this, SettingsActivity.class);  // saljemo intent PreferancesActivity.class
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
        ListView listview = (ListView) findViewById(R.id.list_first_activity);
        if (listview != null) {
            ArrayAdapter<Actor> adapter = (ArrayAdapter<Actor>) listview.getAdapter();
            if (adapter != null) {
                adapter.clear();
                try {
                    List<Actor> list = getDatabaseHelper().getGlumacDao().queryForAll();
                    adapter.addAll(list);
                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();

                }
            }
        }
    }


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
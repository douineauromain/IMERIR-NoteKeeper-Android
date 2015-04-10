package rcdsm.imerir.notekeeper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.Note;
import models.TestRealm;


public class DetailNote extends ActionBarActivity {

    TextView textViewDateCreated,textViewDateUpdated,textViewCity;
    EditText editTextTitle, editTextContent;
    Button saveButton;

    TestRealm testRealm;
    Note note;
    boolean newnote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_note);

        textViewDateCreated = (TextView)findViewById(R.id.textViewDateCreated);
        textViewDateUpdated = (TextView)findViewById(R.id.textViewDateUpdated);
        textViewCity = (TextView)findViewById(R.id.textViewCity);
        editTextTitle = (EditText)findViewById(R.id.editTextTitle);
        editTextContent = (EditText)findViewById(R.id.editTextContent);
        saveButton = (Button)findViewById(R.id.saveButton);

        saveButton.setOnClickListener(saveButtonListener);


        testRealm = new TestRealm(this);


//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras.getLong("noteid") != 0) {
            note = testRealm.getNoteById(extras.getLong("noteid"));
            setTitle(note.getTitle());
        } else {
            setTitle("Nouvelle note");
            note = testRealm.createOneNote();
            newnote = true;
        }

        textViewDateCreated.setText(note.getCreatedAt().toString());
        textViewDateUpdated.setText(note.getUpdatedAt().toString());
        editTextTitle.setText(note.getTitle().toString());
        editTextContent.setText(note.getContent().toString());
        textViewCity.setText(note.getCity().toString());

        //GPS
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }

    private View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            saveAndGo();

        }
    };

    private void saveAndGo(){
        testRealm.saveNoteById(note.getId(), editTextTitle.getText().toString(), editTextContent.getText().toString(), textViewCity.getText().toString());

        Intent intent = new Intent(DetailNote.this, MainActivity.class);
        startActivity(intent);
    }

    private void deleteAndGo(){
        testRealm.deleteOneNote(note.getId());
        Intent intent = new Intent(DetailNote.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.validateItem) {
            saveAndGo();
        }
        if (id == R.id.deleteItem) {
            deleteAndGo();
        }
        if (id == R.id.shareItem) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"jon@example.com"}); // recipients
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "NoteKeeper : "+note.getTitle());
            emailIntent.putExtra(Intent.EXTRA_TEXT, note.getContent());
//            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://path/to/email/attachment"));
            startActivity(emailIntent);
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(newnote) {
            deleteAndGo();
        } else {
            Intent intent = new Intent(DetailNote.this, MainActivity.class);
            startActivity(intent);
        }
    }

    /*---------- Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

            Toast.makeText(
                    getBaseContext(),
                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " + loc.getLongitude();

            String latitude = "Latitude: " + loc.getLatitude();


        /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0)
                    System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                    + cityName;
            textViewCity.setText(s);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}

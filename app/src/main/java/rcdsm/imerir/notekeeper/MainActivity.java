package rcdsm.imerir.notekeeper;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

import models.Note;
import models.TestRealm;


public class MainActivity extends ActionBarActivity {

    ArrayList<Note> notes;
    ListView listView;

    SearchView searchView;
    TestRealm testRealm;
    NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        FloatingActionButton newNoteButton = (FloatingActionButton) findViewById(R.id.newNoteButton);
        newNoteButton.attachToListView(listView);

        newNoteButton.setOnClickListener(newNoteButtonListener);


        Log.d("azerty", "zegr,egklrz,klezgrrezkgezrlgkzeg");

        testRealm = new TestRealm(this);
        //testRealm.deleteAll();
        //testRealm.createNotes();
        //testRealm.updateSomeNotes();
        if(testRealm.isPopulated()){
            notes = testRealm.listNotesWithArray();
        } else {
            Log.d("azertyError", "Pas de notes trouvées");

            Toast.makeText(this, "Liste vide, créez votre première note !", Toast.LENGTH_LONG).show();
            notes = testRealm.listNotesWithArray();
        }

        majListView();

        listView.setOnItemClickListener(listViewItemClickListener);
    }

    private void majListView(){
        adapter = new NoteAdapter(this, notes);
        listView.setAdapter(adapter);
    }

    private View.OnClickListener newNoteButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(MainActivity.this, DetailNote.class);
            long id = 0;
            intent.putExtra("noted",id);
            startActivity(intent);
        }
    };

    private ListView.OnItemClickListener listViewItemClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent intent = new Intent(MainActivity.this, DetailNote.class);
            Note tmpNote = notes.get(position);
            Log.d("azenextra", String.valueOf(tmpNote.getId())+" id sended");
            intent.putExtra("noteid", tmpNote.getId());
            startActivity(intent);
        }
    };

    private SearchView.OnQueryTextListener searchQueryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            search(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText == "") {
                search("");
            } else {
                search(newText);
            }

            return true;
        }

        public void search(String query) {
            Log.d("azesearch", "Searching...");
            notes = testRealm.listNotesWithArrayByTitle(query);
            majListView();
        }

    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(searchQueryListener);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar note_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}

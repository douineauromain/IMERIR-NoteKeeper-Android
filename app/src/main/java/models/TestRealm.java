package models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import junit.framework.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by rcdsm on 09/04/15.
 */
public class TestRealm {

    Realm realm;
    Context context;
    AQuery aquery;
    JSONObject noteJson;
    SharedPreferences preferences;
    long tempId;

    public TestRealm(Context rcontext){
        context = rcontext;
        realm = Realm.getInstance(rcontext);
        preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        aquery = new AQuery(rcontext);

    }

//    public void createNotes(){
//        realm.beginTransaction();
//        for (int i = 0;i<=20;i++) {
//            createOneNote();
//        }
//        realm.commitTransaction();
//    }

    public void createOneNote(final NoteListener noteListener){

        //Send note online
        noteJson = new JSONObject();
        try {
            noteJson.put("title", "");
            noteJson.put("content", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("azertjson", noteJson.toString());
        String url = "http://notes.lloyd66.fr/api/v1/note/?token="+preferences.getString("token","LOL");
        aquery.post(url, noteJson, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                Log.d("azertjson", "Reponse createnote : " + json.toString());
                JSONObject notecreated = null;
                try {
                    notecreated = json.getJSONObject("note");
                    tempId = notecreated.getLong("id");
                    Log.d("azertid", "Id de la note créée"+tempId);

                    Toast.makeText(context, "Note créée", Toast.LENGTH_LONG).show();

                    //Store offline

                    realm.beginTransaction();
                    Note note = realm.createObject(Note.class);
                    note.setId(tempId);
                    note.setTitle("");
                    note.setContent("");
                    note.setCreatedAt(new Date());
                    note.setUpdatedAt(new Date());
                    note.setCity("Pas de localisation actuellement sur cette note");
                    realm.commitTransaction();

                    noteListener.onCreateNote(tempId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });




        //return note;
    }

    public void listNotes(){
        RealmResults<Note> results = realm.where(Note.class).findAll();
        for(Note note : results){
            Log.d("azertyRealMTest", note.getId() + " : " + note.getTitle() + " : " + note.getContent()+ " at " + note.getCreatedAt());

        }

    }

    public void getNoteById(final long id, final NoteListener noteListener){
        String url = "http://notes.lloyd66.fr/api/v1/note/"+id+"?token="+preferences.getString("token","LOL");
        aquery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                Log.d("azertjson", "Reponse getnote : " + json.toString());
                JSONObject noterecup = null;
                try {
                    noterecup = json.getJSONObject("note");
                    tempId = noterecup.getLong("id");
                    Log.d("azertid", "Id de la note récupéré" + tempId);

                    saveNoteById(tempId, noterecup.getString("title"), noterecup.getString("content"), "No city");

                    RealmResults<Note> results = realm.where(Note.class)
                            .equalTo("id", tempId)
                            .findAll();
                    Note noteFound = results.get(0);


                    noteListener.onGetTheNoteById(noteFound);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });




    }

    public ArrayList<Note> listNotesWithArray(){
        ArrayList<Note> notelist = new ArrayList<Note>();
        RealmResults<Note> results = realm.where(Note.class).findAll();
        results.sort("updatedAt", RealmResults.SORT_ORDER_DESCENDING);
        for(Note note : results){
            Log.d("azertyRealMTest", note.getId() + " : " + note.getTitle() + " : " + note.getContent()+ " at " + note.getCreatedAt());
            notelist.add(note);
        }
        return notelist;
    }

    public ArrayList<Note> listNotesOnline(){
        final ArrayList<Note> notelist = new ArrayList<Note>();
        String url = "http://notes.lloyd66.fr/api/v1/note/?token="+preferences.getString("token","LOL");
        aquery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                Log.d("azertyjson", "list note json : " + json);
                try {
                    JSONArray noteJsonArray = json.getJSONArray("notes");
                    for (int i = 0; i <= noteJsonArray.length(); i++) {
                        JSONObject jsonNoteFromArray = noteJsonArray.getJSONObject(i);
                        Note newNote = new Note();
                        newNote.setTitle(jsonNoteFromArray.getString("title"));
                        newNote.setContent(jsonNoteFromArray.getString("content"));
                        newNote.setId(jsonNoteFromArray.getLong("id"));
                        notelist.add(newNote);

                    }

                    //event listener
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        return notelist;
    }

    public ArrayList<Note> listNotesWithArrayByTitle(String title){
        ArrayList<Note> notelist = new ArrayList<Note>();
        RealmResults<Note> results = realm.where(Note.class).beginsWith("title", title, false).findAll();
        results.sort("updatedAt", RealmResults.SORT_ORDER_DESCENDING);
        for(Note note : results){
            Log.d("azertyRealMTest", note.getId() + " : " + note.getTitle() + " : " + note.getContent()+ " at " + note.getCreatedAt());
            notelist.add(note);
        }
        return notelist;
    }

    public boolean isPopulated(){
        return realm.where(Note.class).findAll().size()>0;
    }

    public void deleteAll(){
        realm.beginTransaction();
        realm.where(Note.class).findAll().clear();
        realm.commitTransaction();
    }

    public void deleteOnlineNote(){
        String url = "http://notes.lloyd66.fr/api/v1/note/?token="+preferences.getString("token","LOL");
        aquery.delete(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

            }
        });
    }

    public void updateSomeNotes(){
        realm.beginTransaction();

        RealmQuery<Note> query = realm.where(Note.class);
        query.contains("title", "1");

        RealmResults<Note> results = query.findAll();

        for(int i=0; i < results.size();i++){
            Note note = results.get(i);
            note.setContent(note.getContent()+" -updated");
            note.setUpdatedAt(new Date());

        }

        realm.commitTransaction();
    }

    public void saveNoteById(long id, String newTitle, String newContent, String newCity){
        realm.beginTransaction();
        RealmQuery<Note> query = realm.where(Note.class);
        query.equalTo("id", id);
        Note note = query.findFirst();

        note.setTitle(newTitle);
        note.setContent(newContent);
        note.setUpdatedAt(new Date());
        note.setCity(newCity);

        realm.commitTransaction();

        //Send note online
        noteJson = new JSONObject();
        try {
            noteJson.put("title", newTitle);
            noteJson.put("content", newContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("azertjson", noteJson.toString());

        String url = "http://notes.lloyd66.fr/api/v1/note/"+id+"/?token="+preferences.getString("token","LOL");
        Log.d("azertyid", "savenote : "+url);

        aquery.put(url, noteJson, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d("azertjson", "Reponse savenote : " + json.toString());

            }
        });

        Toast.makeText(context, "Note sauvegardée", Toast.LENGTH_LONG).show();
    }

    public void deleteOneNote(long id){
        realm.beginTransaction();
        RealmQuery<Note> query = realm.where(Note.class);
        query.equalTo("id", id);
        query.findAll().clear();

        Toast.makeText(context, "Note supprimée", Toast.LENGTH_LONG).show();
        realm.commitTransaction();
    }
}



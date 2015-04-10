package models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import junit.framework.Test;

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

    public TestRealm(Context rcontext){
        context = rcontext;
        realm = Realm.getInstance(rcontext);

    }

    public void createNotes(){
        realm.beginTransaction();
        for (int i = 0;i<=20;i++) {
            createOneNote();
        }
        realm.commitTransaction();
    }

    public Note createOneNote(){
        realm.beginTransaction();
        Note note = realm.createObject(Note.class);
        long i = 42;
        note.setId(new Date().getTime() + i);
        note.setTitle("");
        note.setContent("");
        note.setCreatedAt(new Date());
        note.setUpdatedAt(new Date());
        note.setCity("Pas de localisation actuellement sur cette note");
        realm.commitTransaction();
        return note;
    }

    public void listNotes(){
        RealmResults<Note> results = realm.where(Note.class).findAll();
        for(Note note : results){
            Log.d("azertyRealMTest", note.getId() + " : " + note.getTitle() + " : " + note.getContent()+ " at " + note.getCreatedAt());

        }

    }

    public Note getNoteById(long id){
        RealmResults<Note> results = realm.where(Note.class)
                .equalTo("id", id)
                .findAll();
        Note noteFound = results.get(0);
        return noteFound;

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

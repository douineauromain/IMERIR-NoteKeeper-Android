package models;

/**
 * Created by rcdsm on 27/04/15.
 */
public interface NoteListener {
    public void onGetTheNoteById(Note note);
    public void onCreateNote(long id);
}

package rcdsm.imerir.notekeeper;

/**
 * Created by rcdsm on 09/04/15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import models.Note;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * Created by rcdsm on 19/03/15.
 */
public class NoteAdapter extends BaseAdapter {
    Context context;
    ArrayList<Note> notes;

    LayoutInflater inflater;
    SimpleDateFormat format;

    public NoteAdapter(Context context, ArrayList<Note> notes) {
        this.context = context;
        this.notes = notes;

        inflater = LayoutInflater.from(context);

        format = new SimpleDateFormat("dd/MM");
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView==null) {
            convertView = inflater.inflate(R.layout.note_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.noteTitle);
            holder.date = (TextView) convertView.findViewById(R.id.noteDate);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note note = notes.get(position);
        holder.title.setText(note.getTitle());
        holder.date.setText(note.getUpdatedAt().toString());

        return convertView;
    }

    class ViewHolder {
        public TextView title;
        public TextView date;
    }
}


package com.iti.example.tripreminder.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.iti.example.tripreminder.R;

import java.util.ArrayList;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NotesListViewHolder> {

    Context context ;
    ArrayList<String> notesList ;

    public NotesListAdapter(Context context, ArrayList<String> notesList){
        this.context = context ;
        this.notesList = notesList ;
    }

    @NonNull
    @Override
    public NotesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = layoutInflater.inflate(R.layout.row_layout,parent,false);
        NotesListViewHolder notesListViewHolder = new NotesListViewHolder(rowView);
        return notesListViewHolder;
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull NotesListViewHolder holder, int position) {
        holder.noteTextView.setText(notesList.get(position));
        holder.rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rowLayout.setBackgroundColor(Color.GREEN);
                holder.isDoneCheckBox.setChecked(true);
            }
        });
        holder.isDoneCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rowLayout.setBackgroundColor(Color.GREEN);
                holder.isDoneCheckBox.setChecked(true);
            }
        });
    }

    class NotesListViewHolder extends RecyclerView.ViewHolder{


        CheckBox isDoneCheckBox ;
        TextView noteTextView ;
        ConstraintLayout rowLayout ;

        public NotesListViewHolder(@NonNull View rowView) {
            super(rowView);
            isDoneCheckBox = rowView.findViewById(R.id.chkBox_isDone_row);
            noteTextView = rowView.findViewById(R.id.txtView_note_row);
            rowLayout = rowView.findViewById(R.id.cl_noteRow_row);
        }
    }

}


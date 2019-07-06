package com.example.contactapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.contactapp.R;
import com.example.contactapp.model.Contact;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.RecyclerViewHolder> {
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    private List<Contact> contactList;
    private Context context;
    private static ClickListener clickListener;


    public ContactsAdapter(Context context, List<Contact> contactList) {
        this.contactList = contactList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_contact, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        holder.textViewContact.setText(contactList.get(position).getDisplayName());
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(contactList.get(position).getDisplayName().substring(0, 1), generator.getRandomColor());
        holder.imageViewContact.setImageDrawable(drawable);

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewContact;
        TextView textViewContact;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewContact = itemView.findViewById(R.id.img_contact);
            textViewContact = itemView.findViewById(R.id.tv_contact);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getAdapterPosition());

        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        ContactsAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onClick(View view, int position);
    }
}

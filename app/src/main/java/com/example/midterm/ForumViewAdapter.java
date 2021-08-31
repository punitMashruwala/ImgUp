package com.example.midterm;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ForumViewAdapter extends RecyclerView.Adapter<ForumViewAdapter.ListViewHolder> {
    ArrayList<DataServices.Comment> DataLists;
    String AuthToken;
    ForumViewAdapter.ForumViewListener forumViewListener;
    DataServices.Account acc = null;

    public ForumViewAdapter(ArrayList<DataServices.Comment> Data, String token, ForumViewAdapter.ForumViewListener forumViewListener) {
        this.DataLists = Data;
        this.AuthToken = token;
        this.forumViewListener = forumViewListener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_view_layout, parent, false);
        ForumViewAdapter.ListViewHolder viewHolder = new ForumViewAdapter.ListViewHolder(view, forumViewListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        holder.comment.setText(DataLists.get(position).getText());
        holder.authName.setText(DataLists.get(position).getCreatedBy().getName());
        holder.date.setText(DataLists.get(position).getCreatedAt().toString());

        new getAccountAsyncFunction().execute(AuthToken);

        if (acc == DataLists.get(position).getCreatedBy()) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forumViewListener.deleteComment(String.valueOf(DataLists.get(position).getCommentId()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return DataLists.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {

        TextView authName, comment, date;
        ImageView delete;
        ForumViewAdapter.ForumViewListener forumViewListener;

        public ListViewHolder(@NonNull View itemView, ForumViewListener forumViewListener) {
            super(itemView);
            this.forumViewListener = forumViewListener;
            authName = itemView.findViewById(R.id.viewCommentAuthorName);
            comment = itemView.findViewById(R.id.viewCommentDescription);
            date = itemView.findViewById(R.id.viewCommentDateAndTime);
            delete = itemView.findViewById(R.id.imageView);
        }
    }

    interface ForumViewListener {
        void deleteComment(String commentId);
    }

    class getAccountAsyncFunction extends AsyncTask<String, String, String> {



        @Override
        protected String doInBackground(String... strings) {
            try {
                acc = DataServices.getAccount(strings[0]);
                return null;
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
                return "error";
            }
        }
    }
}

package com.example.midterm;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ForumListAdapter extends RecyclerView.Adapter<ForumListAdapter.ForumListViewHolder> {
    DataServices.Account currentUserAccount;
    ArrayList<DataServices.Forum> Lists;
    String token;
    ForumListAdapterInterface listener;

    public ForumListAdapter(String token, DataServices.Account account, ArrayList<DataServices.Forum> Data, ForumListAdapterInterface AppListListener) {
        this.currentUserAccount = account;
        this.Lists = Data;
        this.listener = AppListListener;
        this.token = token;
    }

    @NonNull
    @Override
    public ForumListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_list_layout, parent, false);
        ForumListViewHolder viewHolder = new ForumListViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ForumListViewHolder holder, int position) {
        DataServices.Forum selectedForumData = Lists.get(position);
        holder.title.setText(selectedForumData.getTitle());
        holder.author.setText(selectedForumData.getCreatedBy().getName());
        if (selectedForumData.getDescription().length() > 200) {
            holder.desc.setText(selectedForumData.getDescription().substring(0, 200) + " ...");
        } else {
            holder.desc.setText(selectedForumData.getDescription());
        }
        holder.likes.setText(selectedForumData.getLikedBy().size() + " Likes |");
        if (selectedForumData.getCreatedBy() != currentUserAccount) {
            holder.delete.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.VISIBLE);
        }
        holder.date.setText(selectedForumData.getCreatedAt().toString());
        holder.position = position;
        holder.forum = selectedForumData;
        holder.currentAccount = currentUserAccount;
        holder.token = token;
        if (selectedForumData.getLikedBy().contains(currentUserAccount)) {
            holder.liked.setVisibility(View.VISIBLE);
            holder.notLiked.setVisibility(View.GONE);
        } else {
            holder.liked.setVisibility(View.GONE);
            holder.notLiked.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return Lists.size();
    }

    public static class ForumListViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, desc, likes, date;
        ImageView delete, liked, notLiked;
        DataServices.Account currentAccount;
        String token;
        int position;
        DataServices.Forum forum;
        ForumListAdapterInterface forumListListener;

        @SuppressLint("ResourceType")
        public ForumListViewHolder(@NonNull View itemView, ForumListAdapterInterface AppListListener) {
            super(itemView);
            this.forumListListener = AppListListener;
            title = itemView.findViewById(R.id.textView_forumTitle);
            author = itemView.findViewById(R.id.textView_forumAuthor);
            desc = itemView.findViewById(R.id.textView_forumDesc);
            likes = itemView.findViewById(R.id.textView_likes);
            date = itemView.findViewById(R.id.textView_date);
            delete = itemView.findViewById(R.id.imageView_delete);
            notLiked = itemView.findViewById(R.id.imageViewUnLikeButton);
            liked = itemView.findViewById(R.id.imageViewLikeClick);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    forumListListener.get_forum_details(forum);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    forumListListener.deleteForum(token, forum.getForumId());
                }
            });

            liked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    forumListListener.LikeDislikeForum("Dislike", token, forum.getForumId());
                }
            });

            notLiked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    forumListListener.LikeDislikeForum("Like", token, forum.getForumId());
                }
            });
        }

    }

    interface ForumListAdapterInterface {
        void get_forum_details(DataServices.Forum forum);

        void deleteForum(String token, Long id);

        void LikeDislikeForum(String action, String token, Long id);
    }
}

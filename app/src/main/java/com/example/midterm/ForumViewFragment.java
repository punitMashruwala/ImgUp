package com.example.midterm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;


public class ForumViewFragment extends Fragment implements ForumViewAdapter.ForumViewListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String token = "tok";

    TextView authorName, forumName, comments, description, comment_label;
    EditText addComment;
    Button post;
    ForumViewAdapter adapter;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    // TODO: Rename and change types of parameters
    private DataServices.Forum mParam1;
    private String mParamToken;

    public ForumViewFragment() {
        // Required empty public constructor
    }


    public static ForumViewFragment newInstance(String tok, DataServices.Forum data) {
        ForumViewFragment fragment = new ForumViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, data);
        args.putString(token, tok);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (DataServices.Forum) getArguments().getSerializable(ARG_PARAM1);
            mParamToken = getArguments().getString(token);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forum_view, container, false);
        getActivity().setTitle(getResources().getString(R.string.forum_label));

        recyclerView = view.findViewById(R.id.commentListRC);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        authorName = view.findViewById(R.id.viewAuthorName);
        comment_label = view.findViewById(R.id.textView);
        forumName = view.findViewById(R.id.viewTitle);
        comments = view.findViewById(R.id.viewCommentSize);
        description = view.findViewById(R.id.viewForumDescription);

        addComment = view.findViewById(R.id.addCommentInForum);

        post = view.findViewById(R.id.postButton);
        DataServices.Account acc = mParam1.getCreatedBy();
        description.setText(mParam1.getDescription());
        description.setMovementMethod(new ScrollingMovementMethod());
        authorName.setText(acc.getName());

        forumName.setText(mParam1.getTitle());


        new getCommentAsyncFunction().execute(mParamToken, String.valueOf(mParam1.getForumId()));
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addComment.getText().toString().isEmpty()) {
                    new addCommentAsyncFunction().execute(mParamToken, String.valueOf(mParam1.getForumId()), addComment.getText().toString());
                }
            }
        });

        return view;
    }

    @Override
    public void deleteComment(String commentId) {
        new deleteCommentAsyncFunction().execute(mParamToken, String.valueOf(mParam1.getForumId()), commentId);
    }

    class deleteCommentAsyncFunction extends AsyncTask<String, String, String[]> {

        @Override
        protected void onPostExecute(String[] s) {
            if (s != null)
                new getCommentAsyncFunction().execute(s[0], s[1]);
            else
                Toast.makeText(getActivity(), getResources().getString(R.string.CommonError), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String[] doInBackground(String... strings) {
            try {
                DataServices.deleteComment(strings[0], parseInt(strings[1]), parseInt(strings[2]));
                String[] s = new String[2];
                s[0] = strings[0];
                s[1] = strings[1];
                return s;
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    class getCommentAsyncFunction extends AsyncTask<String, String, ArrayList<DataServices.Comment>> {

        @Override
        protected void onPostExecute(ArrayList<DataServices.Comment> s) {
            if (s != null) {
                comments.setText(String.valueOf(s.size()));
                if (s.size() > 0) {
                    comment_label.setText(getResources().getString(R.string.comment));
                } else {
                    comment_label.setText(getResources().getString(R.string.comment_single));
                }
                adapter = new ForumViewAdapter(s, mParamToken, ForumViewFragment.this);
                recyclerView.setAdapter(adapter);
            } else
                Toast.makeText(getActivity(), getResources().getString(R.string.CommonError), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected ArrayList<DataServices.Comment> doInBackground(String... strings) {
            try {
                return DataServices.getForumComments(strings[0], parseInt(strings[1])); // token, mParam1.getForumId
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    class addCommentAsyncFunction extends AsyncTask<String, String, String[]> {

        @Override
        protected void onPostExecute(String[] s) {
            if (s != null) {
                new getCommentAsyncFunction().execute(s[0], s[1]);
            } else
                Toast.makeText(getActivity(), getResources().getString(R.string.CommonError), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String[] doInBackground(String... strings) {
            try {
                DataServices.createComment(strings[0], parseInt(strings[1]), strings[2]); //token, forumId, text
                String[] s = new String[2];
                s[0] = strings[0];
                s[1] = strings[1];
                return s;
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
                return null;
            }

        }
    }
}
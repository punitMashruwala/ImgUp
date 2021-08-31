package com.example.midterm;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ForumListFragment extends Fragment implements ForumListAdapter.ForumListAdapterInterface {

    private static final String ARG_PARAM1 = "param1";
    private String mParamToken;
    ForumListFragmentInterface forumListListener;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ForumListAdapter adapter;
    DataServices.Account accData;

    public ForumListFragment() {
        // Required empty public constructor
    }

    public static ForumListFragment newInstance(String param1) {
        ForumListFragment fragment = new ForumListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamToken = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forum_list, container, false);
        getActivity().setTitle(getResources().getString(R.string.forums_label));
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        new GetAccountAsyncFunction().execute(mParamToken);

        view.findViewById(R.id.button_createNewForum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forumListListener.createForum(mParamToken);
            }
        });
        view.findViewById(R.id.button_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), getResources().getString(R.string.LogoutSuccess), Toast.LENGTH_SHORT).show();
                forumListListener.logout();
            }
        });
        return view;
    }

    interface ForumListFragmentInterface {
        void logout();

        void createForum(String token);

        void forumDetail(DataServices.Forum f);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ForumListFragment.ForumListFragmentInterface) {
            forumListListener = (ForumListFragment.ForumListFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement IListener");
        }
    }

    class GetForumListsAsyncFunction extends AsyncTask<String, String, ArrayList<DataServices.Forum>> {
        @Override
        protected void onPostExecute(ArrayList<DataServices.Forum> forums) {
            if (forums != null) {
                adapter = new ForumListAdapter(mParamToken, accData, forums, ForumListFragment.this);
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.CommonError), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected ArrayList<DataServices.Forum> doInBackground(String... strings) {
            try {
                return DataServices.getAllForums(strings[0]);
            } catch (DataServices.RequestException e) {
                return null;
            }
        }
    }

    class GetAccountAsyncFunction extends AsyncTask<String, String, DataServices.Account> {
        @Override
        protected void onPostExecute(DataServices.Account account) {
            if (account != null) {
                accData = account;
                new GetForumListsAsyncFunction().execute(mParamToken);
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.CommonError), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected DataServices.Account doInBackground(String... strings) {
            try {
                return DataServices.getAccount(strings[0]);
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void get_forum_details(DataServices.Forum forum) {
        forumListListener.forumDetail(forum);
    }

    @Override
    public void deleteForum(String token, Long id) {
        new DeleteForumAsyncFunction().execute(token, String.valueOf(id));
    }

    @Override
    public void LikeDislikeForum(String action, String token, Long id) {
        new LikeDislikeAsyncFunction().execute(action, token, String.valueOf(id));
    }

    class DeleteForumAsyncFunction extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            if (s == null)
                new GetForumListsAsyncFunction().execute(mParamToken);
            else
                Toast.makeText(getActivity(), getResources().getString(R.string.CommonError), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                DataServices.deleteForum(strings[0], Long.valueOf(strings[1]));
                return null;
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
                return "error";
            }
        }
    }

    class LikeDislikeAsyncFunction extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            if (s == null)
                new GetForumListsAsyncFunction().execute(mParamToken);
            else
                Toast.makeText(getActivity(), getResources().getString(R.string.CommonError), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                if (strings[0].equals("Like")) {
                    DataServices.likeForum(strings[1], Long.valueOf(strings[2]));
                } else {
                    DataServices.unLikeForum(strings[1], Long.valueOf(strings[2]));
                }
                return null;
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
                return "error";
            }
        }
    }


}
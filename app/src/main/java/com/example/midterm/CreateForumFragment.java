package com.example.midterm;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class CreateForumFragment extends Fragment {
    private static final String TOKEN = "TOKEN";
    private String mParam1;
    EditText name, desc;
    String titleValue, descValue;
    NewForumFragmentInterface listener;

    public static CreateForumFragment newInstance(String param1) {
        CreateForumFragment fragment = new CreateForumFragment();
        Bundle args = new Bundle();
        args.putString(TOKEN, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_forum, container, false);
        getActivity().setTitle(getResources().getString(R.string.new_forum_label));
        name = view.findViewById(R.id.editText_forum_title);
        desc = view.findViewById(R.id.editText_forum_desc);
        view.findViewById(R.id.button_submit_new_forum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // submit button is clicked
                titleValue = name.getText().toString();
                descValue = desc.getText().toString();
                if (titleValue.isEmpty() || descValue.isEmpty()) {
                    Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_LONG).show();
                } else {
                    new CreateForumFunctionAsync().execute(mParam1, titleValue, descValue);
                }

            }
        });

        view.findViewById(R.id.textView_cancel_new_forum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancel_click_new_forum();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NewForumFragmentInterface) {
            listener = (NewForumFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString() + getResources().getString(R.string.ErrorContext));
        }
    }

    interface NewForumFragmentInterface {
        void NewForumCreatedSuccessfully(DataServices.Forum forum);
        void cancel_click_new_forum();
    }

    class CreateForumFunctionAsync extends AsyncTask<String, String, DataServices.Forum> {
        @Override
        protected void onPostExecute(DataServices.Forum forum) {
            if (forum != null) {
                listener.NewForumCreatedSuccessfully(forum);
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.CommonError), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected DataServices.Forum doInBackground(String... strings) {
            try {
                return DataServices.createForum(strings[0], strings[1], strings[2]);
            } catch (DataServices.RequestException e) {
                return null;
            }
        }
    }
}
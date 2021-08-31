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

public class LoginFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    EditText emailText, passwordText;
    String email, password;
    DataServices.Account userLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().setTitle(getResources().getString(R.string.Login));
        emailText = view.findViewById(R.id.editText_email);
        passwordText = view.findViewById(R.id.editText_password);

        view.findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailText.getText().toString();
                password = passwordText.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.NameError), Toast.LENGTH_LONG).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.PasswordError), Toast.LENGTH_LONG).show();
                } else {
                    new ThreadLoginAsyncFunction().execute(email, password);
                }

            }
        });
        view.findViewById(R.id.textViewCreateAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginListener.createNewAccount();
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LoginInterface) {
            LoginListener = (LoginInterface) context;
        } else {
            throw new RuntimeException(context.toString() + getResources().getString(R.string.ErrorContext));
        }
    }

    LoginInterface LoginListener;

    public interface LoginInterface {
        void LoginSuccess(DataServices.AuthResponse authResponse);

        void createNewAccount();
    }

    class ThreadLoginAsyncFunction extends AsyncTask<String, Integer, DataServices.AuthResponse> {

        @Override
        protected void onPostExecute(DataServices.AuthResponse authResponse) {
            if (authResponse != null) {
                Toast.makeText(getActivity(), getResources().getString(R.string.LoginSuccess), Toast.LENGTH_SHORT).show();
                LoginListener.LoginSuccess(authResponse);
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.IncorrectInput), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected DataServices.AuthResponse doInBackground(String... strings) {
            try {
                return DataServices.login(strings[0], strings[1]);
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
                return null;
            }

        }
    }
}
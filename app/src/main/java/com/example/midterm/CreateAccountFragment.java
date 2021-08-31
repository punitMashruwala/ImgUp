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


public class CreateAccountFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    EditText name, email, password;
    String emailValue, nameValue, passwordValue;
    DataServices.Account newUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);
        getActivity().setTitle(getResources().getString(R.string.CreateNewAccount));
        name = view.findViewById(R.id.editText_create_name);
        email = view.findViewById(R.id.editText_create_email);
        password = view.findViewById(R.id.editText_create_password);

        view.findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameValue = name.getText().toString();
                emailValue = email.getText().toString();
                passwordValue = password.getText().toString();

                if (nameValue.isEmpty() || emailValue.isEmpty() || passwordValue.isEmpty()) {
                    Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_LONG).show();
                } else {
                    new AsyncRegister().execute(nameValue, emailValue, passwordValue);
                }
            }
        });
        view.findViewById(R.id.textView_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAccountListener.cancelUserCreation();
            }
        });

        return view;
    }

    CreateAccountInterface registerAccountListener;

    public interface CreateAccountInterface {
        void UserCreated(DataServices.AuthResponse authResponse);

        void cancelUserCreation();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateAccountFragment.CreateAccountInterface) {
            registerAccountListener = (CreateAccountFragment.CreateAccountInterface) context;
        } else {
            throw new RuntimeException(context.toString() + getResources().getString(R.string.ErrorContext));
        }
    }

    class AsyncRegister extends AsyncTask<String, String, DataServices.AuthResponse> {
        @Override
        protected void onPostExecute(DataServices.AuthResponse authResponse) {
            if (authResponse != null) {
                registerAccountListener.UserCreated(authResponse);
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.CommonError), Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected DataServices.AuthResponse doInBackground(String... strings) {
            try {
                return DataServices.register(strings[0], strings[1], strings[2]);
            } catch (DataServices.RequestException e) {
                return null;
            }
        }
    }
}
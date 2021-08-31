// Punit Mashruwala - 801208416
package com.example.midterm;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
        implements LoginFragment.LoginInterface,
        CreateAccountFragment.CreateAccountInterface,
        CreateForumFragment.NewForumFragmentInterface,
        ForumListFragment.ForumListFragmentInterface {

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.containerView, new LoginFragment())
                .commit();
    }

    @Override
    public void LoginSuccess(DataServices.AuthResponse authResponse) {
        this.token = authResponse.getToken();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, ForumListFragment.newInstance(this.token))
                .commit();
    }

    @Override
    public void createNewAccount() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new CreateAccountFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void UserCreated(DataServices.AuthResponse authResponse) {
        this.token = authResponse.getToken();
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, ForumListFragment.newInstance(this.token))
                .commit();
    }

    @Override
    public void cancelUserCreation() {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new LoginFragment())
                .commit();
    }

    @Override
    public void NewForumCreatedSuccessfully(DataServices.Forum forum) {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, ForumListFragment.newInstance(this.token))
                .commit();
    }

    @Override
    public void cancel_click_new_forum() {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, ForumListFragment.newInstance(this.token))
                .commit();
    }


    @Override
    public void logout() {
        this.token = null;
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new LoginFragment())
                .commit();
    }

    @Override
    public void createForum(String token) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, CreateForumFragment.newInstance(token))
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void forumDetail(DataServices.Forum f) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, ForumViewFragment.newInstance(this.token, f))
                .addToBackStack(null)
                .commit();
    }
}
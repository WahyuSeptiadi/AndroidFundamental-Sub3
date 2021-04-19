package com.kevin.consumer.view.follower;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.consumer.R;
import com.kevin.consumer.helper.BaseConst;

import java.util.Objects;

public class FollowersFragment extends Fragment {

    private RecyclerView recyclerView;
    private FollowersListAdapter adapter;
    private FollowersViewModel followersViewModel;
    private ProgressBar progressBar;

    private TextView message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followers, container, false);

        recyclerView = view.findViewById(R.id.rv_followers);
        progressBar = view.findViewById(R.id.progress_circular_followers);
        message = view.findViewById(R.id.tv_message_followers);

        setRecyclerView();

        String username = Objects.requireNonNull(getActivity()).getIntent().getStringExtra(BaseConst.DATA_KEY);
        followersViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(FollowersViewModel.class);

        //setData
        followersViewModel.setFollowersData(username);

        getDataUser();

        return view;
    }

    private void getDataUser() {
        followersViewModel.getFollowersData().observe((Objects.requireNonNull(getActivity())), git_user -> {
            if (git_user.isEmpty()) {
                adapter.clearList(git_user);

                progressBar.setVisibility(View.GONE);
                message.setText(R.string.str_follower_null);
                message.setVisibility(View.VISIBLE);
            } else {
                adapter.setData(git_user);
                recyclerView.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.smoothScrollToPosition(0);
        adapter = new FollowersListAdapter(getActivity());
        adapter.notifyDataSetChanged();
    }
}
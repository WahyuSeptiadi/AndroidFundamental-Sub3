package com.kevin.provider.view.follower;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kevin.provider.R;
import com.kevin.provider.databinding.FragmentFollowersBinding;
import com.kevin.provider.helper.BaseConst;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FollowersFragment extends Fragment {
    private FollowersListAdapter adapter;
    private FollowersViewModel followersViewModel;

    private FragmentFollowersBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFollowersBinding.inflate(inflater, container, false);

        setRecyclerView();

        String username = Objects.requireNonNull(getActivity()).getIntent().getStringExtra(BaseConst.DATA_KEY);
        followersViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(FollowersViewModel.class);

        followersViewModel.setFollowersData(username);

        getData();

        return binding.getRoot();
    }

    private void getData() {
        followersViewModel.getFollowersData().observe((Objects.requireNonNull(getActivity())), git_user -> {
            if (git_user.isEmpty()) {
                adapter.clearList(git_user);

                binding.tvMessageFollowers.setText(R.string.string_follower_null);
                binding.tvMessageFollowers.setVisibility(View.VISIBLE);
            } else {
                adapter.setData(git_user);
                binding.rvFollowers.setAdapter(adapter);
            }
            binding.progressCircularFollowers.setVisibility(View.GONE);
        });
    }

    private void setRecyclerView() {
        binding.rvFollowers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFollowers.setHasFixedSize(true);
        binding.rvFollowers.smoothScrollToPosition(0);
        adapter = new FollowersListAdapter(getActivity());
        adapter.notifyDataSetChanged();
    }
}
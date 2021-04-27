package com.kevin.consumer.view.following;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kevin.consumer.R;
import com.kevin.consumer.databinding.FragmentFollowingBinding;
import com.kevin.consumer.helper.BaseConst;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FollowingFragment extends Fragment {
    private FollowingListAdapter adapter;
    private FollowingViewModel followingViewModel;

    private FragmentFollowingBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFollowingBinding.inflate(inflater, container, false);

        setRecyclerView();

        String username = Objects.requireNonNull(getActivity()).getIntent().getStringExtra(BaseConst.DATA_KEY);
        followingViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(FollowingViewModel.class);

        followingViewModel.setFollowingData(username);

        getDataFollowing();

        return binding.getRoot();
    }

    private void getDataFollowing() {
        followingViewModel.getFollowingData().observe(Objects.requireNonNull(getActivity()), git_user -> {
            if (git_user.isEmpty()) {
                adapter.clearList(git_user);

                binding.tvMessageFollowing.setText(R.string.string_following_null);
                binding.tvMessageFollowing.setVisibility(View.VISIBLE);
            } else {
                adapter.setData(git_user);
                binding.rvFollowing.setAdapter(adapter);
            }
            binding.progressCircularFollowing.setVisibility(View.GONE);
        });
    }

    private void setRecyclerView() {
        binding.rvFollowing.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFollowing.setHasFixedSize(true);
        binding.rvFollowing.smoothScrollToPosition(0);
        adapter = new FollowingListAdapter(getActivity());
        adapter.notifyDataSetChanged();
    }
}
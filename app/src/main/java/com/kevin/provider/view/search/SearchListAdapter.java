package com.kevin.provider.view.search;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.provider.R;
import com.kevin.provider.data.local.FavoriteModel;
import com.kevin.provider.data.local.MappingHelper;
import com.kevin.provider.data.remote.response.UserResultResponse;
import com.kevin.provider.databinding.ItemUserListBinding;
import com.kevin.provider.helper.BaseConst;
import com.kevin.provider.helper.CustomOnItemClickListener;
import com.kevin.provider.view.detail.DetailActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.mateware.snacky.Snacky;

import static android.content.Context.MODE_PRIVATE;
import static com.kevin.provider.data.local.DatabaseContract.FavColumns.AVATAR;
import static com.kevin.provider.data.local.DatabaseContract.FavColumns.CONTENT_URI;
import static com.kevin.provider.data.local.DatabaseContract.FavColumns.ID_USER;
import static com.kevin.provider.data.local.DatabaseContract.FavColumns.TYPE_USER;
import static com.kevin.provider.data.local.DatabaseContract.FavColumns.USERNAME;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    private List<UserResultResponse> mInfoUsers;
    private final Activity activity;

    private Cursor cursor;
    private boolean cekIdUser;

    public SearchListAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        ItemUserListBinding binding = ItemUserListBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mInfoUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mInfoUsers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ItemUserListBinding binding;

        public ViewHolder(ItemUserListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(UserResultResponse userResultResponse) {
            Picasso.get().load(userResultResponse.getAvatarUrl())
                    .placeholder(R.drawable.ic_profile)
                    .into(binding.civAvatar);
            binding.tvUsernameValueList.setText(userResultResponse.getLogin());
            binding.tvTypeUserValueList.setText(String.valueOf(userResultResponse.getType()));

            binding.cardListSearch.setOnClickListener(new CustomOnItemClickListener((view) -> {
                Intent toDetail = new Intent(activity, DetailActivity.class);
                toDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Bundle bundle = new Bundle();
                bundle.putString(BaseConst.DATA_KEY, userResultResponse.getLogin());
                toDetail.putExtras(bundle);

                activity.startActivity(toDetail);
            }));

            SharedPreferences sharedPreferences = activity.getSharedPreferences("fav", MODE_PRIVATE);
            boolean cek = sharedPreferences.getBoolean("fav" + userResultResponse.getId(), false);
            binding.imgBorderLove.setVisibility(View.VISIBLE);
            if (cek) {
                binding.imgAddToFavorite.setVisibility(View.VISIBLE);
            } else {
                binding.imgAddToFavorite.setVisibility(View.GONE);
            }

            binding.imgBorderLove.setOnClickListener(new CustomOnItemClickListener((view) -> {
                if (cursor != null) {
                    cursor.close();
                }

                cursor = activity.getContentResolver()
                        .query(CONTENT_URI, null, null, null, null);
                ArrayList<FavoriteModel> list = MappingHelper.mapCursorToArrayList(Objects.requireNonNull(cursor));

                String now = String.valueOf(userResultResponse.getId());

                for (FavoriteModel favList : list) {
                    cekIdUser = favList.getUserId().equals(now);
                    if (cekIdUser) {
                        break;
                    }
                }

                if (cekIdUser) {
                    if (binding.imgBorderLove.getVisibility() == View.VISIBLE && binding.imgAddToFavorite.getVisibility() == View.VISIBLE) {
                        Snacky.builder()
                                .setActivity(activity)
                                .centerText()
                                .setText(activity.getResources().getString(R.string.user_already))
                                .setDuration(Snacky.LENGTH_LONG)
                                .error().show();
                    } else if (binding.imgBorderLove.getVisibility() == View.VISIBLE && binding.imgAddToFavorite.getVisibility() == View.GONE) {
                        Snacky.builder()
                                .setActivity(activity)
                                .centerText()
                                .setText(activity.getResources().getString(R.string.add_by_other))
                                .setDuration(Snacky.LENGTH_LONG)
                                .warning().show();
                        binding.imgBorderLove.setVisibility(View.VISIBLE);
                        binding.imgAddToFavorite.setVisibility(View.VISIBLE);
                    } else if (binding.imgAddToFavorite.getVisibility() == View.GONE) {
                        binding.imgAddToFavorite.setVisibility(View.VISIBLE);
                    } else {
                        binding.imgAddToFavorite.setVisibility(View.VISIBLE);
                    }
                    SharedPreferences.Editor editor = activity.getSharedPreferences("fav", MODE_PRIVATE).edit();
                    editor.putBoolean("fav" + userResultResponse.getId(), true);
                    editor.apply();
                } else {
                    if (binding.imgBorderLove.getVisibility() == View.VISIBLE && binding.imgAddToFavorite.getVisibility() == View.VISIBLE) {
                        Snacky.builder()
                                .setActivity(activity)
                                .centerText()
                                .setText(activity.getResources().getString(R.string.add_user_again))
                                .setDuration(Snacky.LENGTH_LONG)
                                .success().show();
                    } else if (binding.imgBorderLove.getVisibility() == View.VISIBLE && binding.imgAddToFavorite.getVisibility() == View.GONE) {
                        Snacky.builder()
                                .setView(view)
                                .centerText()
                                .setText(activity.getResources().getString(R.string.add_successful))
                                .setDuration(Snacky.LENGTH_SHORT)
                                .success().show();
                    }

                    ContentValues values = new ContentValues();
                    values.put(AVATAR, userResultResponse.getAvatarUrl());
                    values.put(USERNAME, userResultResponse.getLogin());
                    values.put(TYPE_USER, userResultResponse.getType());
                    values.put(ID_USER, userResultResponse.getId());

                    activity.getContentResolver().insert(CONTENT_URI, values);

                    //set pref love
                    SharedPreferences.Editor editor = activity.getSharedPreferences("fav", MODE_PRIVATE).edit();
                    editor.putBoolean("fav" + userResultResponse.getId(), true);
                    editor.apply();

                    binding.imgAddToFavorite.setVisibility(View.VISIBLE);
                }
            }));
        }
    }

    public void setData(List<UserResultResponse> infoUser) {
        this.mInfoUsers = infoUser;
        notifyDataSetChanged();
    }

    public void clearList(List<UserResultResponse> clearListUser) {
        this.mInfoUsers = clearListUser;
        this.mInfoUsers.clear();
        notifyDataSetChanged();
    }
}

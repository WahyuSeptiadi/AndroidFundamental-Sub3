package com.kevin.consumer.view.follower;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.consumer.R;
import com.kevin.consumer.data.local.FavoriteModel;
import com.kevin.consumer.data.local.MappingHelper;
import com.kevin.consumer.data.remote.response.UserResultResponse;
import com.kevin.consumer.helper.BaseConst;
import com.kevin.consumer.helper.CustomOnItemClickListener;
import com.kevin.consumer.view.detail.DetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import de.mateware.snacky.Snacky;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;
import static com.kevin.consumer.data.local.DatabaseContract.FavColumns.AVATAR;
import static com.kevin.consumer.data.local.DatabaseContract.FavColumns.CONTENT_URI;
import static com.kevin.consumer.data.local.DatabaseContract.FavColumns.IDUSER;
import static com.kevin.consumer.data.local.DatabaseContract.FavColumns.TYPEUSER;
import static com.kevin.consumer.data.local.DatabaseContract.FavColumns.USERNAME;

public class FollowersListAdapter extends RecyclerView.Adapter<FollowersListAdapter.ViewHolder> {
    private List<UserResultResponse> mInfo_Users;
    private final Activity activity;

    private Cursor cursor;
    private boolean cekIdUser;

    public FollowersListAdapter(Activity activity1) {
        this.activity = activity1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.bind(mInfo_Users.get(position));
        holder.item.setOnClickListener(new CustomOnItemClickListener(position, (view, position1) -> {
            Intent toDetail = new Intent(activity, DetailActivity.class);
            toDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Bundle bundle = new Bundle();
            bundle.putString(BaseConst.DATA_KEY, mInfo_Users.get(position1).getLogin());
            toDetail.putExtras(bundle);

            activity.startActivity(toDetail);
        }));

        SharedPreferences sharedPreferences = activity.getSharedPreferences("fav", MODE_PRIVATE);
        boolean cek = sharedPreferences.getBoolean("fav" + mInfo_Users.get(position).getId(), false);
        holder.borderFavList.setVisibility(View.VISIBLE);
        if (cek) {
            holder.fillFavList.setVisibility(View.VISIBLE);
        } else {
            holder.fillFavList.setVisibility(View.GONE);
        }

        holder.borderFavList.setOnClickListener(new CustomOnItemClickListener(position, (view, position1) -> {
            if (cursor != null) {
                cursor.close();
            }

            cursor = activity.getContentResolver().query(CONTENT_URI, null, null, null, null);
            ArrayList<FavoriteModel> list = MappingHelper.mapCursorToArrayList(Objects.requireNonNull(cursor));

            String now = String.valueOf(mInfo_Users.get(position).getId());

            for (FavoriteModel favlist : list) {
                cekIdUser = favlist.getIduser().equals(now);
                if (cekIdUser) {
                    break;
                }
            }

            if (cekIdUser) {
                if (holder.borderFavList.getVisibility() == View.VISIBLE && holder.fillFavList.getVisibility() == View.VISIBLE) {
                    Toasty.error(activity, activity.getResources().getString(R.string.user_already),
                            Toast.LENGTH_SHORT, true).show();
                } else if (holder.borderFavList.getVisibility() == View.VISIBLE) {
                    Toasty.warning(activity, activity.getResources().getString(R.string.other_add),
                            Toast.LENGTH_SHORT, true).show();
                    holder.fillFavList.setVisibility(View.VISIBLE);
                } else if (holder.fillFavList.getVisibility() == View.GONE) {
                    holder.fillFavList.setVisibility(View.VISIBLE);
                } else {
                    holder.fillFavList.setVisibility(View.VISIBLE);
                }
                SharedPreferences.Editor editor = activity.getSharedPreferences("fav", MODE_PRIVATE).edit();
                editor.putBoolean("fav" + mInfo_Users.get(position).getId(), true);
                editor.apply();
            } else {
                if (holder.borderFavList.getVisibility() == View.VISIBLE && holder.fillFavList.getVisibility() == View.VISIBLE) {
                    Toasty.success(activity, activity.getResources().getString(R.string.add_again),
                            Toast.LENGTH_LONG, true).show();
                } else if (holder.borderFavList.getVisibility() == View.VISIBLE && holder.fillFavList.getVisibility() == View.GONE) {
                    Snacky.builder()
                            .setView(view)
                            .centerText()
                            .setText(activity.getResources().getString(R.string.success_add))
                            .setDuration(Snacky.LENGTH_SHORT)
                            .success().show();
                }

                ContentValues values = new ContentValues();
                values.put(AVATAR, mInfo_Users.get(position).getAvatarUrl());
                values.put(USERNAME, mInfo_Users.get(position).getLogin());
                values.put(TYPEUSER, mInfo_Users.get(position).getType());
                values.put(IDUSER, mInfo_Users.get(position).getId());

                activity.getContentResolver().insert(CONTENT_URI, values);

                //set pref love
                SharedPreferences.Editor editor = activity.getSharedPreferences("fav", MODE_PRIVATE).edit();
                editor.putBoolean("fav" + mInfo_Users.get(position).getId(), true);
                editor.apply();

                holder.fillFavList.setVisibility(View.VISIBLE);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return mInfo_Users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView imgAvatar;
        private final TextView username;
        private final TextView typeUser;
        private final ImageView fillFavList;
        private final ImageView borderFavList;
        private final CardView item;

        public ViewHolder(View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.civ_search);
            username = itemView.findViewById(R.id.usernameValue_listSearch);
            typeUser = itemView.findViewById(R.id.typeUserValue_listSearch);
            item = itemView.findViewById(R.id.cardListSearch);
            fillFavList = itemView.findViewById(R.id.addToFavList);
            borderFavList = itemView.findViewById(R.id.borderFavList);
        }

        public void bind(UserResultResponse userResultResponse) {
            Picasso.get().load(userResultResponse.getAvatarUrl())
                    .placeholder(R.drawable.ic_profile)
                    .into(imgAvatar);
            username.setText(userResultResponse.getLogin());
            typeUser.setText(String.valueOf(userResultResponse.getType()));
        }
    }

    public void setData(List<UserResultResponse> infoUser) {
        this.mInfo_Users = infoUser;
        notifyDataSetChanged();
    }

    public void clearList(List<UserResultResponse> clearListUser) {
        this.mInfo_Users = clearListUser;
        this.mInfo_Users.clear();
        notifyDataSetChanged();
    }
}

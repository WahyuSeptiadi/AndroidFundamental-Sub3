package com.wahyu.consumerapp.adapter;

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

import com.squareup.picasso.Picasso;
import com.wahyu.consumerapp.localDatabase.helper.MappingHelper;
import com.wahyu.consumerapp.model.FavoriteModel;
import com.wahyu.consumerapp.model.SearchUserInfo;
import com.wahyu.consumerapp.R;
import com.wahyu.consumerapp.view.DetailActivity;
import com.wahyu.consumerapp.constant.Base;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import de.mateware.snacky.Snacky;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;
import static com.wahyu.consumerapp.localDatabase.contract.DatabaseContract.FavColumns.AVATAR;
import static com.wahyu.consumerapp.localDatabase.contract.DatabaseContract.FavColumns.CONTENT_URI;
import static com.wahyu.consumerapp.localDatabase.contract.DatabaseContract.FavColumns.IDUSER;
import static com.wahyu.consumerapp.localDatabase.contract.DatabaseContract.FavColumns.TYPEUSER;
import static com.wahyu.consumerapp.localDatabase.contract.DatabaseContract.FavColumns.USERNAME;

public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.ViewHolder> {
//    => Penamaan class sebaiknya diperjelas

    private List<SearchUserInfo> mInfo_Users;
    private final Activity activity;

    private Cursor cursor;
    private boolean cekIdUser;

    public DetailListAdapter(Activity activity1) {
        this.activity = activity1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_detail_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.bind(mInfo_Users.get(position));
        holder.item.setOnClickListener(new CustomOnItemClickListener(position, (view, position1) -> {
            Intent toDetail = new Intent(activity, DetailActivity.class);
            toDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Bundle bundle = new Bundle();
            bundle.putString(Base.DATA_KEY, mInfo_Users.get(position1).getLogin());
            toDetail.putExtras(bundle);

            activity.startActivity(toDetail);
        }));

        SharedPreferences sharedPreferences = activity.getSharedPreferences("fav", MODE_PRIVATE);
        boolean cek = sharedPreferences.getBoolean("fav"+ mInfo_Users.get(position).getId(), false);
        holder.borderFavList.setVisibility(View.VISIBLE);
        if (cek){
            holder.fillFavList.setVisibility(View.VISIBLE);
        }else{
            holder.fillFavList.setVisibility(View.GONE);
        }

        holder.borderFavList.setOnClickListener(new CustomOnItemClickListener(position, (view, position1) -> {
            if (cursor != null){
                cursor.close();
            }

            cursor = activity.getContentResolver().query(CONTENT_URI, null, null, null, null);
            ArrayList<FavoriteModel> list = MappingHelper.mapCursorToArrayList(Objects.requireNonNull(cursor));

            String now = String.valueOf(mInfo_Users.get(position).getId());

            for (FavoriteModel favlist : list) {
                cekIdUser = favlist.getIduser().equals(now);
                if (cekIdUser){
                    break;
                }
            }

            if (cekIdUser){
                if (holder.borderFavList.getVisibility() == View.VISIBLE && holder.fillFavList.getVisibility() == View.VISIBLE){
                    Toasty.error(activity, activity.getResources().getString(R.string.user_already),
                            Toast.LENGTH_SHORT, true).show();
                }else if(holder.borderFavList.getVisibility() == View.VISIBLE){
                    Toasty.warning(activity, activity.getResources().getString(R.string.other_add),
                    Toast.LENGTH_SHORT, true).show();
                    holder.fillFavList.setVisibility(View.VISIBLE);
                }else if (holder.fillFavList.getVisibility() == View.GONE){
                    holder.fillFavList.setVisibility(View.VISIBLE);
                }else{
                    holder.fillFavList.setVisibility(View.VISIBLE);
                }
                SharedPreferences.Editor editor = activity.getSharedPreferences("fav", MODE_PRIVATE).edit();
                editor.putBoolean("fav" + mInfo_Users.get(position).getId(), true);
                editor.apply();
            }else{
                if(holder.borderFavList.getVisibility() == View.VISIBLE && holder.fillFavList.getVisibility() == View.VISIBLE){
                    Toasty.success(activity, activity.getResources().getString(R.string.add_again),
                    Toast.LENGTH_LONG, true).show();
                }else if(holder.borderFavList.getVisibility() == View.VISIBLE && holder.fillFavList.getVisibility() == View.GONE){
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
        private final TextView idUser;
        private final ImageView fillFavList;
        private final ImageView borderFavList;
        private final CardView item;

        public ViewHolder(View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.civ_search);
            username = itemView.findViewById(R.id.usernameValue_listSearch);
            typeUser = itemView.findViewById(R.id.typeUserValue_listSearch);
            idUser = itemView.findViewById(R.id.idUserValue_listSearch);
            item = itemView.findViewById(R.id.cardListSearch);
            fillFavList = itemView.findViewById(R.id.filledFavList);
            borderFavList = itemView.findViewById(R.id.borderFavList);
        }

        public void bind(SearchUserInfo searchUserInfo) {
            Picasso.get().load(searchUserInfo.getAvatarUrl())
                    .placeholder(R.drawable.ic_profile)
                    .into(imgAvatar);
            username.setText(searchUserInfo.getLogin());
            typeUser.setText(String.valueOf(searchUserInfo.getType()));
            idUser.setText(String.valueOf(searchUserInfo.getId()));
        }
    }

    public void setData(List<SearchUserInfo> infoUser){
        this.mInfo_Users = infoUser;
        notifyDataSetChanged();
    }

    public void clearList(List<SearchUserInfo> clearListUser){
        this.mInfo_Users = clearListUser;
        this.mInfo_Users.clear();
        notifyDataSetChanged();
    }
}

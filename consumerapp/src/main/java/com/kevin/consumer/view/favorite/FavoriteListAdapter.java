package com.kevin.consumer.view.favorite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.consumer.R;
import com.kevin.consumer.data.local.FavoriteModel;
import com.kevin.consumer.helper.BaseConst;
import com.kevin.consumer.helper.CustomOnItemClickListener;
import com.kevin.consumer.view.detail.DetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import de.mateware.snacky.Snacky;

import static android.content.Context.MODE_PRIVATE;
import static com.kevin.consumer.data.local.DatabaseContract.FavColumns.CONTENT_URI;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder> {

    public final ArrayList<FavoriteModel> favoriteModelArrayList = new ArrayList<>();
    private final Activity activity;

    private Uri uriWithId;

    public FavoriteListAdapter(Activity activity1) {
        this.activity = activity1;
    }

    public ArrayList<FavoriteModel> getFavoriteModelArrayList() {
        return favoriteModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteListAdapter.ViewHolder holder, int position) {

        holder.bind(favoriteModelArrayList.get(position));
        holder.item.setOnClickListener(new CustomOnItemClickListener(position, (view, position1) -> {
            Intent toDetail = new Intent(activity, DetailActivity.class);
            toDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Bundle bundle = new Bundle();
            bundle.putString(BaseConst.DATA_KEY, favoriteModelArrayList.get(position1).getUsername());
            toDetail.putExtras(bundle);

            activity.startActivity(toDetail);
        }));

        holder.deleteFromFavList.setOnClickListener(view -> {
            String idUserDelete = favoriteModelArrayList.get(position).getUserId();
            showAlertDialogDELETE(idUserDelete);

            SharedPreferences.Editor editor = activity.getSharedPreferences("fav", MODE_PRIVATE).edit();
            editor.putBoolean("fav" + favoriteModelArrayList.get(position).getUserId(), false);
            editor.apply();
        });
    }

    @Override
    public int getItemCount() {
        return favoriteModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView imgAvatar;
        private final TextView username;
        private final TextView typeUser;
        private final ImageView deleteFromFavList;
        private final CardView item;

        public ViewHolder(View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.civ_search);
            username = itemView.findViewById(R.id.tv_username_value_list);
            typeUser = itemView.findViewById(R.id.tv_type_user_value_list);
            item = itemView.findViewById(R.id.cardListSearch);
            deleteFromFavList = itemView.findViewById(R.id.img_delete_from_favorite);
        }

        public void bind(FavoriteModel bind) {
            Picasso.get().load(bind.getAvatar())
                    .placeholder(R.drawable.ic_profile)
                    .into(imgAvatar);
            username.setText(bind.getUsername());
            typeUser.setText(String.valueOf(bind.getUserType()));
        }
    }

    public void setFavoriteModelArrayList(ArrayList<FavoriteModel> listFav) {
        this.favoriteModelArrayList.clear();
        this.favoriteModelArrayList.addAll(listFav);
        notifyDataSetChanged();
    }

    private void showAlertDialogDELETE(String idUserDel) {
        String dialogMessage = activity.getResources().getString(R.string.msg_delete_user);
        String dialogTitle = activity.getResources().getString(R.string.ttl_delete_user);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton(activity.getResources().getString(R.string.positive_btn), (dialog, id) -> {
                    uriWithId = Uri.parse(CONTENT_URI + "/" + idUserDel);
                    activity.getContentResolver().delete(uriWithId, null, null);
                    Snacky.builder()
                            .setActivity(activity)
                            .centerText()
                            .setText(activity.getResources().getText(R.string.success_delete))
                            .setDuration(Snacky.LENGTH_LONG)
                            .success().show();
                })
                .setNegativeButton(activity.getResources().getString(R.string.negative_btn), (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

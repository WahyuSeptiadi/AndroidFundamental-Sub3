package com.kevin.provider.view.favorite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.provider.R;
import com.kevin.provider.data.local.FavoriteModel;
import com.kevin.provider.databinding.ItemFavoriteListBinding;
import com.kevin.provider.helper.BaseConst;
import com.kevin.provider.helper.CustomOnItemClickListener;
import com.kevin.provider.view.detail.DetailActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.mateware.snacky.Snacky;

import static android.content.Context.MODE_PRIVATE;
import static com.kevin.provider.data.local.DatabaseContract.FavColumns.CONTENT_URI;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder> {

    public final ArrayList<FavoriteModel> favoriteModelArrayList = new ArrayList<>();
    private final Activity activity;

    private Uri uriWithId;

    public FavoriteListAdapter(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<FavoriteModel> getFavoriteModelArrayList() {
        return favoriteModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        ItemFavoriteListBinding binding = ItemFavoriteListBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(FavoriteListAdapter.ViewHolder holder, int position) {
        holder.bind(favoriteModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return favoriteModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ItemFavoriteListBinding binding;

        public ViewHolder(ItemFavoriteListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FavoriteModel favoriteModel) {
            Picasso.get().load(favoriteModel.getAvatar())
                    .placeholder(R.drawable.ic_profile)
                    .into(binding.civAvatar);
            binding.tvUsernameValueList.setText(favoriteModel.getUsername());
            binding.tvTypeUserValueList.setText(String.valueOf(favoriteModel.getUserType()));

            binding.cardListFavorite.setOnClickListener(new CustomOnItemClickListener((view) -> {
                Intent toDetail = new Intent(activity, DetailActivity.class);
                toDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Bundle bundle = new Bundle();
                bundle.putString(BaseConst.DATA_KEY, favoriteModel.getUsername());
                toDetail.putExtras(bundle);

                activity.startActivity(toDetail);
            }));

            binding.imgDeleteFromFavorite.setOnClickListener(new CustomOnItemClickListener((view) -> {
                String idUserDelete = favoriteModel.getUserId();
                showAlertDialogDELETE(idUserDelete);

                SharedPreferences.Editor editor = activity.getSharedPreferences("fav", MODE_PRIVATE).edit();
                editor.putBoolean("fav" + favoriteModel.getUserId(), false);
                editor.apply();
            }));
        }
    }

    public void setFavoriteModelArrayList(ArrayList<FavoriteModel> listFav) {
        this.favoriteModelArrayList.clear();
        this.favoriteModelArrayList.addAll(listFav);
        notifyDataSetChanged();
    }

    private void showAlertDialogDELETE(String idUserDel) {
        String dialogMessage = activity.getResources().getString(R.string.msg_delete_user);
        String dialogTitle = activity.getResources().getString(R.string.title_delete_user);

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
                            .setText(activity.getResources().getString(R.string.delete_successful))
                            .setDuration(Snacky.LENGTH_LONG)
                            .success().show();
                })
                .setNegativeButton(activity.getResources().getString(R.string.negative_btn), (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

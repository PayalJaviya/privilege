package com.practicaltest_payaljaviya.view.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.practicaltest_payaljaviya.R;
import com.practicaltest_payaljaviya.common.Utils;
import com.practicaltest_payaljaviya.model.UserList;
import com.practicaltest_payaljaviya.view.activity.DetailActivity;
import com.practicaltest_payaljaviya.view.activity.UserListActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Vtpl Android on 11/7/2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.Holder> {
    Context context;
    ArrayList<UserList> list = new ArrayList<>();

    public UserListAdapter(ArrayList<UserList> userLists, UserListActivity context) {
        this.list = userLists;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final UserList userList = list.get(position);
        //if (userEmail != null && !userEmail.isEmpty() && !userEmail.equals("null"))
        if (!TextUtils.isEmpty(userList.getUser_img())) {
            Glide.with(context)
                    .asBitmap()
                    .load(userList.getUser_img())
                    .thumbnail(0.01f)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            Bitmap smallBitmap = Bitmap.createScaledBitmap(resource, 100, 100, true);
                            holder.profileImage.setImageBitmap(smallBitmap);
                        }
                    });


        }
        if (Utils.isEmptyStr(userList.getLogin())) {
            holder.txtUsernm.setText(userList.getLogin());
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View navigationBar = ((UserListActivity)context).findViewById(android.R.id.navigationBarBackground);
                View statusBar = ((UserListActivity)context).findViewById(android.R.id.statusBarBackground);

                List<Pair<View, String>> pairs = new ArrayList<>();
                pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
                pairs.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));

                Intent intent = new Intent(context, DetailActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Bundle options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs.toArray(new Pair[pairs.size()])).toBundle();
                    intent.putExtra("name", userList.getLogin());
                    intent.putExtra("id", userList.getUser_id());
                    context.startActivity(intent, options);
                } else {
                    intent.putExtra("name", userList.getLogin());
                    intent.putExtra("id", userList.getUser_id());
                    context.startActivity(intent);
                }
              /*  Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("name", userList.getLogin());
                intent.putExtra("id", userList.getUser_id());
                context.startActivity(intent);*/


            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.profile_image)
        CircleImageView profileImage;
        @BindView(R.id.txtUsernm)
        TextView txtUsernm;
        @BindView(R.id.card_view)
        CardView cardView;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

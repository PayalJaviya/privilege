package com.practicaltest_payaljaviya.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.practicaltest_payaljaviya.R;
import com.practicaltest_payaljaviya.api.RetroConfig;
import com.practicaltest_payaljaviya.common.Utils;
import com.practicaltest_payaljaviya.model.UserList;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.txtfollowers)
    TextView txtfollowers;
    @BindView(R.id.txtfollowing)
    TextView txtfollowing;
    @BindView(R.id.txtName)
    TextView txtName;
    @BindView(R.id.txtemail)
    TextView txtemail;
    @BindView(R.id.txtCompanyNm)
    TextView txtCompanyNm;
    @BindView(R.id.txtLocation)
    TextView txtLocation;
    @BindView(R.id.txtBio)
    TextView txtBio;
    @BindView(R.id.imgDetailProfile)
    CircleImageView imgDetailProfile;
    @BindView(R.id.progressDeatil)
    ProgressBar progressDeatil;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fb_blog)
    FrameLayout fbBlog;
    @BindView(R.id.detail_layout)
    RelativeLayout detailLayout;
    @BindView(R.id.layout_followers)
    LinearLayout layoutFollowers;
    @BindView(R.id.layout_following)
    LinearLayout layoutFollowing;
    String id, blog,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.excludeTarget(android.R.id.statusBarBackground, true);
            slide.excludeTarget(android.R.id.navigationBarBackground, true);
            slide.excludeTarget(toolbar, true);
            slide.setSlideEdge(Gravity.RIGHT);
            slide.setDuration(500);

            getWindow().setAllowEnterTransitionOverlap(false);
            getWindow().setAllowReturnTransitionOverlap(false);
            getWindow().setEnterTransition(slide);
            getWindow().setExitTransition(slide);
        }

        //change thr progressBar color
        progressDeatil.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            id = bundle.getString("id");
            Log.e("name", "" + name);
            getDetail(name);
            toolbarTitle.setText("Login User " + name + "'s Detail");
        }
    }

    private void getDetail(String name) {
        if (Utils.isConnectingToInternet(DetailActivity.this)) {
            progressDeatil.setVisibility(View.VISIBLE);
            Call<ResponseBody> call = RetroConfig.retrofit().getUserDetail(name);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            try {
                                progressDeatil.setVisibility(View.GONE);
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                String avatar_url = jsonObject.getString("avatar_url");
                                String id = jsonObject.getString("id");
                                String name = jsonObject.getString("name");
                                String company = jsonObject.getString("company");
                                blog = jsonObject.getString("blog");
                                String location = jsonObject.getString("location");
                                String email = jsonObject.getString("email");
                                String followers = jsonObject.getString("followers");
                                String following = jsonObject.getString("following");
                                String bio = jsonObject.getString("bio");

                                if (Utils.isEmptyStr(name)) {
                                    txtName.setText(name);
                                }
                                if (Utils.isEmptyStr(followers)) {
                                    txtfollowers.setText(followers);
                                } else {
                                    txtfollowers.setText("0");
                                }

                                if (Utils.isEmptyStr(following)) {
                                    txtfollowing.setText(following);
                                } else {
                                    txtfollowing.setText("0");
                                }
                                if (Utils.isEmptyStr(bio)) {
                                    txtBio.setText(bio);
                                }
                                if (Utils.isEmptyStr(company)) {
                                    txtCompanyNm.setText(company);
                                }
                                if (Utils.isEmptyStr(email)) {
                                    txtemail.setText(email);
                                }
                                if (Utils.isEmptyStr(location)) {
                                    txtLocation.setText(location);
                                }
                                if (Utils.isEmptyStr(name)) {
                                    txtName.setText(name);
                                }
                                if (Utils.isEmptyStr(avatar_url)) {
                                    Glide.with(DetailActivity.this)
                                            .asBitmap()
                                            .load(avatar_url)
                                            .thumbnail(0.01f)
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                                    Bitmap smallBitmap = Bitmap.createScaledBitmap(resource, 100, 100, true);
                                                    imgDetailProfile.setImageBitmap(smallBitmap);
                                                }
                                            });



                                }

                                Realm realm = Realm.getDefaultInstance();
                                UserList currentUser = realm.where(UserList.class).equalTo("user_id", id).findFirst();
                                if (currentUser != null) {
                                    realm.beginTransaction();
                                    currentUser.setName(name);
                                    currentUser.setCompany(company);
                                    currentUser.setBlog(blog);
                                    currentUser.setLocation(location);
                                    currentUser.setEmail(email);
                                    currentUser.setFollowers(followers);
                                    currentUser.setFollowing(following);
                                    currentUser.setBio(bio);

                                    realm.copyToRealmOrUpdate(currentUser);
                                    realm.commitTransaction();
                                }

                            } catch (Exception e) {

                            }
                        }
                    } else {
                        try {
                            progressDeatil.setVisibility(View.GONE);
                            JSONObject object = new JSONObject(response.errorBody().string());
                            String msg = object.getString("message");
                            Snackbar snackbar = Snackbar.make(detailLayout, msg, Snackbar.LENGTH_LONG);
                            snackbar.show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDeatil.setVisibility(View.GONE);
                }
            });
        } else {
            Snackbar snackbar = Snackbar.make(detailLayout, "Oops!! No Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.show();

            Realm realm = Realm.getDefaultInstance();
            UserList currentUser = realm.where(UserList.class).equalTo("user_id", id).findFirst();
            if (currentUser != null) {

                if (Utils.isEmptyStr(name)) {
                    txtName.setText(name);
                }
                if (Utils.isEmptyStr(currentUser.getFollowers())) {
                    txtfollowers.setText(currentUser.getFollowers());
                } else {
                    txtfollowers.setText("0");
                }

                if (Utils.isEmptyStr(currentUser.getFollowing())) {
                    txtfollowing.setText(currentUser.getFollowing());
                } else {
                    txtfollowing.setText("0");
                }
                if (Utils.isEmptyStr(currentUser.getBio())) {
                    txtBio.setText(currentUser.getBio());
                }
                if (Utils.isEmptyStr(currentUser.getCompany())) {
                    txtCompanyNm.setText(currentUser.getCompany());
                }
                if (Utils.isEmptyStr(currentUser.getEmail())) {
                    txtemail.setText(currentUser.getEmail());
                }
                if (Utils.isEmptyStr(currentUser.getLocation())) {
                    txtLocation.setText(currentUser.getLocation());
                }
                if (Utils.isEmptyStr(currentUser.getName())) {
                    txtName.setText(currentUser.getName());
                }
                if (Utils.isEmptyStr(currentUser.getUser_img())) {
                    Glide.with(DetailActivity.this).load(currentUser.getUser_img()).thumbnail(0.01f).into(imgDetailProfile);
                }

                currentUser.getBlog();
            }
        }
    }

    @OnClick({R.id.layout_followers, R.id.layout_following, R.id.fb_blog})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_followers:
                Intent intent = new Intent(DetailActivity.this, UserListActivity.class);
                intent.putExtra("other_user", "");
                intent.putExtra("login_nm", name);
                intent.putExtra("id", id);
                intent.putExtra("followers", "1");
                startActivity(intent);
                break;
            case R.id.layout_following:
                Intent intent1 = new Intent(DetailActivity.this, UserListActivity.class);
                intent1.putExtra("other_user", txtName.getText().toString());
                intent1.putExtra("login_nm", name);
                intent1.putExtra("id", id);
                intent1.putExtra("followers", "0");
                startActivity(intent1);
                break;
            case R.id.fb_blog:
                if (Utils.isConnectingToInternet(DetailActivity.this)) {
                    if (TextUtils.isEmpty(blog)) {
                        Snackbar snackbar = Snackbar.make(detailLayout, "No Blog Found", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {
                        if (!blog.startsWith("http://") && !blog.startsWith("https://"))
                            blog = "http://" + blog;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(blog));
                        startActivity(browserIntent);
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(detailLayout, "Oops!! No Internet Connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
        }
    }

}

package com.practicaltest_payaljaviya.view.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.practicaltest_payaljaviya.R;
import com.practicaltest_payaljaviya.api.RetroConfig;
import com.practicaltest_payaljaviya.common.Utils;
import com.practicaltest_payaljaviya.model.UserList;
import com.practicaltest_payaljaviya.view.adapter.UserListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.alexbykov.nopaginate.callback.OnLoadMore;
import ru.alexbykov.nopaginate.paginate.Paginate;
import ru.alexbykov.nopaginate.paginate.PaginateBuilder;

public class UserListActivity extends AppCompatActivity {

    @BindView(R.id.recycleview_userlist)
    RecyclerView recycleviewUserlist;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    String name, id, other_user;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txt_NoUser)
    TextView txtNoUser;
    @BindView(R.id.edt_serch)
    EditText edtSerch;
    @BindView(R.id.img_search)
    ImageView imgSearch;
    @BindView(R.id.relative_main)
    RelativeLayout relativeMain;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    ArrayList<UserList> userLists = new ArrayList<>();
    UserListAdapter adapter;
    LinearLayoutManager layoutManager;
    String followers;
    private Paginate paginate;
    int since = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);

        layoutManager = new LinearLayoutManager(UserListActivity.this);
        recycleviewUserlist.setLayoutManager(layoutManager);
        adapter = new UserListAdapter(userLists, UserListActivity.this);
        recycleviewUserlist.setAdapter(adapter);
        paginate = new PaginateBuilder()
                .with(recycleviewUserlist)
                .setCallback(new OnLoadMore() {
                    @Override
                    public void onLoadMore() {
                        // http or db request
                        GetAllUser("", "");
                    }
                })
                .setLoadingTriggerThreshold(1)
                .build();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            followers = bundle.getString("followers");
            name = bundle.getString("login_nm");
            other_user = bundle.getString("other_user");
            id = bundle.getString("id");
            imgSearch.setImageResource(R.mipmap.icn_search);
            edtSerch.setVisibility(View.GONE);
            edtSerch.setText(null);
            toolbarTitle.setVisibility(View.VISIBLE);
            GetAllUser(name, other_user);
        } else {
            GetAllUser("", "");
        }
        //change thr progressBar color
        progressbar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);

        edtSerch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                Log.e("ccc", "" + c.toString());
                ArrayList<UserList> lists = new ArrayList<UserList>();
                for (UserList list : userLists) {
                    String name = list.getLogin().toLowerCase();

                    if (name.contains(c.toString())) {
                        lists.add(list);
                        Log.e("name", "" + name);
                    }
                    adapter = new UserListAdapter(lists, UserListActivity.this);
                    recycleviewUserlist.setAdapter(adapter);
                }
            }


            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });

       /* //Search filter
        recycleviewUserlist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy >0) {
                    // Scroll Down
                    if (fab.isShown()) {
                        fab.hide();
                    }
                }
                else if (dy <0) {
                    // Scroll Up
                    if (!fab.isShown()) {
                        fab.show();
                    }
                }
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (totalItemCount == (lastVisibleItem + 1)) {
                    GetAllUser("", "");
                }
            }
        });*/

    }

    //webservice Call
    private void GetAllUser(final String name, String other_user) {
        if (Utils.isConnectingToInternet(UserListActivity.this)) {
            progressbar.setVisibility(View.GONE);
            Call<ResponseBody> call;
            if (!TextUtils.isEmpty(followers)) {
                toolbar.setNavigationIcon(R.mipmap.back);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                });
                if (followers.equals("1")) {
                    //api call for the followers
                    toolbarTitle.setText("Followers of the " + name);
                    call = RetroConfig.retrofit().getUserfollowers(name, "followers");
                } else {
                    //api call for the Following
                    toolbarTitle.setText("Following of the " + name);
                    call = RetroConfig.retrofit().getUserfollowing(name, other_user);
                }
            } else {
                //api call for the All Data
                toolbarTitle.setText("List Of User");
                call = RetroConfig.retrofit().getAllUser(since);
            }

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            try {
                                progressbar.setVisibility(View.GONE);
                                JSONArray jsonArray = new JSONArray(response.body().string());
                                if (jsonArray.length() == 0) {
                                    txtNoUser.setVisibility(View.VISIBLE);
                                } else {
                                    txtNoUser.setVisibility(View.GONE);
                                }
                                Realm realm = Realm.getDefaultInstance();
                                if (realm.isInTransaction()) {
                                    realm.commitTransaction();
                                }
                                if (!TextUtils.isEmpty(followers)) {
                                    userLists = new ArrayList<>();
                                    if (followers.equals("1")) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            UserList userModel = new UserList();
                                            userModel.setLogin(jsonObject.getString("login"));
                                            userModel.setUser_id(jsonObject.getString("id"));
                                            userModel.setUser_img(jsonObject.getString("avatar_url"));
                                            userLists.add(userModel);
                                        }

                                        Log.e("since", since + "");

                                  /* RealmList<UserList> listRealmList = new RealmList<>();
                                    for (int i = 0; i < userLists.size(); i++) {
                                        UserList userList = userLists.get(i);
                                        listRealmList.add(userList);
                                    }
                                    UserList userList = realm.where(UserList.class).equalTo("user_id", id).findFirst();
                                    if (userList != null) {
                                        realm.beginTransaction();
                                        userList.setFollowersList(listRealmList);
                                        realm.copyToRealmOrUpdate(userList);
                                        realm.commitTransaction();
                                    }*/

                                    } else {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            UserList userModel = new UserList();
                                            userModel.setLogin(jsonObject.getString("login"));
                                            userModel.setUser_id(jsonObject.getString("id"));
                                            userModel.setUser_img(jsonObject.getString("avatar_url"));
                                            userLists.add(userModel);
                                        }
                                    }
                                } else {
                                    int oldPos = userLists.size();
                                    realm.beginTransaction();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        UserList userModel = new UserList();
                                        userModel.setLogin(jsonObject.getString("login"));
                                        userModel.setUser_id(jsonObject.getString("id"));
                                        userModel.setUser_img(jsonObject.getString("avatar_url"));
                                        userLists.add(userModel);
                                        since = jsonObject.getInt("id");
                                        realm.copyToRealmOrUpdate(userModel);
                                    }
                                    realm.commitTransaction();
                                }
                                adapter.notifyDataSetChanged();

                               /* adapter = new UserListAdapter(userLists, UserListActivity.this);
                                recycleviewUserlist.setAdapter(adapter);*/
                            } catch (Exception e) {
                                Log.e("e", e.getLocalizedMessage());
                            }
                        }
                    } else {
                        try {
                            progressbar.setVisibility(View.GONE);
                            JSONObject object = new JSONObject(response.errorBody().string());
                            String msg = object.getString("message");
                            Snackbar snackbar = Snackbar.make(relativeMain, msg, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressbar.setVisibility(View.GONE);
                    Log.e("err", t.getMessage());
                }
            });


        } else {
            //offline
            userLists = new ArrayList<>();
            if (TextUtils.isEmpty(followers)) {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<UserList> listRealmResults = realm.where(UserList.class).findAll();
                for (int i = 0; i < listRealmResults.size(); i++) {
                    UserList userList = listRealmResults.get(i);
                    userLists.add(userList);
                }
            } else {
                if (followers.equals("1")) {
                    Realm realm = Realm.getDefaultInstance();
                    UserList userList = realm.where(UserList.class).equalTo("user_id", id).findFirst();
                    if (userList != null) {
                        RealmList<UserList> realmfollowersList = userList.getFollowersList();
                        for (int i = 0; i < realmfollowersList.size(); i++) {
                            userLists.add(realmfollowersList.get(i));
                        }
                    }
                }
            }
            adapter = new UserListAdapter(userLists, UserListActivity.this);
            recycleviewUserlist.setAdapter(adapter);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        imgSearch.setImageResource(R.mipmap.icn_search);
        edtSerch.setVisibility(View.GONE);
        edtSerch.setText(null);
        toolbarTitle.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.img_search, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_search:
                if (imgSearch.getDrawable().getConstantState().equals(getResources().getDrawable(R.mipmap.icn_search).getConstantState())) {
                    imgSearch.setImageResource(R.mipmap.icn_cross);
                    edtSerch.setVisibility(View.VISIBLE);
                    toolbarTitle.setVisibility(View.GONE);
                } else {
                    imgSearch.setImageResource(R.mipmap.icn_search);
                    edtSerch.setVisibility(View.GONE);
                    edtSerch.setText(null);
                    toolbarTitle.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.fab:
                startActivity(new Intent(UserListActivity.this, PortfoliyoActivity.class));
                break;
        }
    }
}

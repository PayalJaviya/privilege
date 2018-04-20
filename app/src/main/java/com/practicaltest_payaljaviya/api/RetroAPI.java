package com.practicaltest_payaljaviya.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Vtpl Android on 11/7/2017.
 */

public interface RetroAPI {

    @GET("users")
    Call<ResponseBody> getAllUser(@Query("since") int since);

    @GET("users/{username}")
    Call<ResponseBody> getUserDetail(@Path("username") String username);

    @GET("users/{name}/{followers}")
    Call<ResponseBody> getUserfollowers(@Path("name") String name,
                                        @Path("followers") String followers);

    @GET("users/{name}/following?")
    Call<ResponseBody> getUserfollowing(@Path("name") String name,
                                        @Query("other_user") String otherUser);


}

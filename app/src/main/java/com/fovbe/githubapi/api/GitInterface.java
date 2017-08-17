package com.fovbe.githubapi.api;

import com.fovbe.githubapi.model.Item;
import com.fovbe.githubapi.model.Qresult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;


public interface GitInterface {
    @GET("/users/{login}")
    Call<Item> getUsersInfo(@Path("login") String login);

    @GET("/search/users")
    Call<Qresult> getAllUsers(@QueryMap Map<String, String> options);
}

package com.example.frontend.api


import com.example.frontend.api.models.Trashbin
import com.example.frontend.api.models.TrashbinResponse
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface TrashbinService {
    @POST("/api/trashbin")
    fun createBin(@Body trashbin: Trashbin): Call<TrashbinResponse> ;
}
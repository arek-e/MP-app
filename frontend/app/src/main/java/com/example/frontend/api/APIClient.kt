package com.example.frontend.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIClient {
    val service: TrashbinService = initService()

    private fun initService(): TrashbinService =
        Retrofit.Builder()
            .baseUrl("https://binhunter.up.railway.app")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrashbinService::class.java)
}


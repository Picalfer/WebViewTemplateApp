package com.template.api

import com.template.models.Link
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("api?format=json")
    suspend fun getLink(): Response<Link>
}
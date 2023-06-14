package com.example.randomjoke.models

import com.google.gson.annotations.SerializedName

data class Link(
    @SerializedName("link")
    var link: String
)

package com.example.frontend.api.models

import com.google.gson.annotations.SerializedName

data class Wastetypes(
    @SerializedName("paper")
    var paper: Boolean = false,
    @SerializedName("liquid")
    var liquid: Boolean = false,
    @SerializedName("glass")
    var glass: Boolean = false,
    @SerializedName("organic")
    var organic: Boolean = false,
    @SerializedName("plastic")
    var plastic: Boolean = false,
    @SerializedName("metal")
    var metal: Boolean = false
)

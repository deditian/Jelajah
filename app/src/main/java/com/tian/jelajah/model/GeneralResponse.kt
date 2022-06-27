package com.tian.jelajah.model

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

open class GeneralResponse() {
    @SerializedName(value = "response_code", alternate = ["code"])
    var responseCode: String? = null

    @SerializedName("midware_timestamp")
    var midwareTimestamp: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var status: String? = null
}
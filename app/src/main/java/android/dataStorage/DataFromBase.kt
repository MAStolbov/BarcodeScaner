package android.dataStorage

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DataFromBase(
    @Json(name ="Code") var code: Int = 0,
    @Json(name ="Name") var name: String = "",
    @Json(name ="User")var user: String = "")
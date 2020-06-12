package android.barcodescanner

import android.dataStorage.DataFromBase
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ClinicApi {
    @GET("GetCustomerInfo/{barcode}")
    fun getCustomerInfoAsync(@Header("Authorization") credentials:String, @Path("barcode") barcode:String):Call<DataFromBase>

    companion object Factory {
        fun create(): ClinicApi {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl("http://31.40.62.188:56280/ClinicWork/hs/ClinicRoot/")
                .build()

            return retrofit.create(ClinicApi::class.java)
        }
    }
}
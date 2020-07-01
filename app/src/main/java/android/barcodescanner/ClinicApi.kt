package android.barcodescanner

import android.dataStorage.Account
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ClinicApi {
    @GET("GetCustomerInfo/{barcode}")
    fun getCustomerInfoAsync(
        @Header("Authorization") credentials: String,
        @Path("barcode") barcode: String
    ): Call<Account>

    companion object Factory {
        fun create(): ClinicApi {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(TikXmlConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl("http://31.40.62.188:56280/ClinicWork/hs/ClinicRoot/")
                .build()

            return retrofit.create(ClinicApi::class.java)
        }
    }
}
package android.barcodescanner

import android.dataStorage.Account
import android.util.Util
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ClinicApi {

    @GET("{barcode}")
    fun getCustomerInfo(
        @Header("Authorization") credentials: String,
        @Path("barcode") barcode: String
    ): Call<Account>



    companion object Factory {
        fun create(serverAddress:String): ClinicApi {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(TikXmlConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(serverAddress)
                .build()

            return retrofit.create(ClinicApi::class.java)
        }
    }
}
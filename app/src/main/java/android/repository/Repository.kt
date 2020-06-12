package android.repository

import android.barcodescanner.ClinicApi
import android.dataStorage.DataFromBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.Credentials

class Repository {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val clinicApi = ClinicApi.create()

     fun getDataFromBase(barcode: String): DataFromBase? {
        val logPass = Credentials.basic("goblin", "123123")
        val data = clinicApi.getCustomerInfoAsync(logPass, barcode).execute().body()
        clinicApi.getCustomerInfoAsync(logPass,barcode).cancel()
        return data
    }
}
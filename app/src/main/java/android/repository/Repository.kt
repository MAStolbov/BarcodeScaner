package android.repository

import android.barcodescanner.ClinicApi
import android.dataStorage.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.Credentials

class Repository {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val clinicApi = ClinicApi.create()

    fun getDataFromBase(barcode: String): Account? {
        val logPass = Credentials.basic("goblin", "123123")
        var data: Account? = Account()
        clinicApi.getCustomerInfoAsync(logPass, barcode).apply {
            if (execute().isSuccessful) {
                data = execute().body()
                cancel()
            }
            else{

            }
        }
        return data
    }
}
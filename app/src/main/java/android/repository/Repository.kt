package android.repository

import android.barcodescanner.ClinicApi
import android.dataStorage.Account
import android.util.Util
import okhttp3.Credentials
import java.lang.Exception

class Repository {

    fun getDataFromBase(serverAddress:String, login:String, password:String, barcode: String): Account? {
        val clinicApi = ClinicApi.create(serverAddress)
        val logPass = Credentials.basic(login, password)
        var data: Account? = null
        try {
            clinicApi.getCustomerInfo(logPass, barcode).apply {
                execute().apply {
                    if (code() in 400..500) {
                        setResultOfConnection(
                            "Проблема с подключением к серверу ${code()} ${message()}",
                            false
                        )
                    }
                    if (body() == null) {
                        setResultOfConnection("Данные не получены ${code()} ${message()}", false)
                    }
                    if (body()?.errorFromServer != null) {
                        setResultOfConnection("Код не найден", false)
                    } else {
                        setResultOfConnection("", true)
                        data = body()
                    }
                }
                cancel()
            }
        }catch (e:Exception){
            setResultOfConnection("Проблема с подключением к серверу: ${e.message}",false)
        }
        return data
    }

    private fun setResultOfConnection(errorText: String, resultOfConnection: Boolean) {
        Util.dataReceivedSuccessful = resultOfConnection
        Util.errorText = errorText
    }
}
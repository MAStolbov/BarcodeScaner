package android.barcodescanner

import android.content.Context
import android.dataStorage.Account
import android.dataStorage.Service
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var account: Account? = Account()
    var servicesList = MutableLiveData<List<Service?>>()

    fun setServicesList(){
        servicesList.value = account?.services
    }

    fun getTotalPrice():Int{
        var totalPrice = 0
        account?.services?.forEach {
            totalPrice += it.price.toInt()
        }
        return  totalPrice
    }

    fun verifyAvailableNetwork(activity: AppCompatActivity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
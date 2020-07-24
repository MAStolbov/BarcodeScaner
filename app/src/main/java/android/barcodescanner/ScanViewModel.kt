package android.barcodescanner

import android.content.Context
import android.dataStorage.Account
import android.dataStorage.Service
import android.net.ConnectivityManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.zip.CRC32

class ScanViewModel : ViewModel() {
    private val barcodeFormat = Regex("""\d{1,9}-\d{4,15}""")
    private val priceFormat = Regex("""\d*(?:\.[0-9]+)?""")

    val endLoading = MutableLiveData<Boolean>()
    var account: Account? = Account()
    var servicesList = MutableLiveData<List<Service?>>()

    fun checkBarcodeCRC(barcode: String): Boolean {
        val crc32 = CRC32()
        return if (barcodeFormat.matches(barcode)) {
            val (notCrc, crc) = barcode.split('-')
            crc32.update(notCrc.toByteArray())
            crc32.value == crc.toLong()
        } else {
            false
        }
    }

    fun openSettings(barcode: String): Boolean {
        return barcode == "settings"
    }

    fun setServicesList() {
        servicesList.value = account?.services
    }

//    fun getTotalPrice(): Int {
//        return account?.services?.map { it.price.toInt() }?.sum() ?: 0
//    }

    fun getTotalPrice(): Double {
        var totalPrice = 0.0
        account?.services?.forEach {
            if (checkStringForNumbers(it.price) && preparePriceString(it.price).isNotEmpty()){
                totalPrice += preparePriceString(it.price).toDouble()
            }
        }
        return totalPrice
    }

    fun verifyAvailableNetwork(context: Context?): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun checkStringForNumbers(stringWithPrice: String): Boolean {
        val stringForCheck = preparePriceString(stringWithPrice)
        return priceFormat.matches(stringForCheck)
    }

    private fun preparePriceString(priceString: String): String {
        return priceString
            .replace(" ", "")
            .replace(",", ".")
    }
}
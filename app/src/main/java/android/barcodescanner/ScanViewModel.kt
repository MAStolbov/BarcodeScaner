package android.barcodescanner

import android.content.Context
import android.dataStorage.Account
import android.dataStorage.Service
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.Exception
import java.util.zip.CRC32

class ScanViewModel : ViewModel() {
    private val barcodeFormat = Regex("""\d{1,9}-\d{4,15}""")

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

    fun setServicesList() {
        servicesList.value = account?.services
    }

    fun getTotalPrice(): Int {
        return account?.services?.map { it.price.toInt() }?.sum() ?: 0
    }

    fun verifyAvailableNetwork(context: Context?): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
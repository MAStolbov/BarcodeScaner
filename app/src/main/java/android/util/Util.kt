package android.util

import androidx.lifecycle.MutableLiveData
import java.lang.Exception
import java.util.zip.Adler32
import java.util.zip.CRC32

object Util {
    val endLoading = MutableLiveData<Boolean>()

    fun checkBarcodeCRC(barcode: String): Boolean {
        val crc32 = CRC32()
        return try {
            val (notCrc, crc) = barcode.split('-')
            crc32.update(notCrc.toByteArray())
            crc32.value == crc.toLong()
        } catch (e: Exception) {
            false
        }
    }
}
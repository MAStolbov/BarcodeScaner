package android.repository

import android.dataStorage.DataFromBase
import android.stubs.DataBaseStub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class Repository {
    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun getDataFromBase(barcode: String): DataFromBase {
        return DataFromBase(DataBaseStub.returnDataFromBase(barcode))
    }
}
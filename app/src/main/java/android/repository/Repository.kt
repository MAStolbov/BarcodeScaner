package android.repository

import android.dataStorage.DataFromBase
import android.stubs.DataBaseStub

class Repository {
    fun getDataFromBase(barcode: String): DataFromBase {
        return DataFromBase(DataBaseStub.returnDataFromBase(barcode))
    }
}
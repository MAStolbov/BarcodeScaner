package android.repository

import android.stubs.DataBaseStub

class Repository {
    fun getDataFromBase(barcode: String): String {
        return DataBaseStub.returnDataFromBase(barcode)
    }
}
package android.dataStorage

import android.repository.Repository
import android.util.Util

class DataStorage private constructor() {

    private object Holder {
        val INSTANCE = DataStorage()
    }

    companion object {
        val instance = Holder.INSTANCE
    }

    private val repository = Repository()

    var dataFromBase: String = ""

    fun loadData(barcode: String) {
        dataFromBase = repository.getDataFromBase(barcode)
        Util.endLoading.value = true
    }
}
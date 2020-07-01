package android.util

import androidx.lifecycle.MutableLiveData

object Util {
    val endLoading = MutableLiveData<Boolean>()

    var serverAnswer = ""
}
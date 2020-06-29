package android.barcodescanner

import android.dataStorage.Account
import android.dataStorage.Service
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var account: Account? = Account()

}
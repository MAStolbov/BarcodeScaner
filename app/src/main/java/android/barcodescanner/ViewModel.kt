package android.barcodescanner

import android.dataStorage.DataFromBase
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel(){
    var dataFromBase:DataFromBase? = DataFromBase()

    fun returnResult():String{
        return "Name:${dataFromBase?.name}  Code:${dataFromBase?.code}  User:${dataFromBase?.user}"
    }
}
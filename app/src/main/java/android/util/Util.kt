package android.util

import android.dataStorage.Account
import android.dataStorage.Service

object Util {
    var serverAddress = "http://31.40.62.188:56280/ClinicWork/hs/ClinicRoot/GetCustomerInfo/"
    var serverLogin = "goblin"
    var serverPassword = "123123"

    var errorText = ""
    var dataReceivedSuccessful: Boolean = false

    fun setTextAccount():Account{
        val testAccount = Account()
        testAccount.apply {
            dateOfVisit = "some date"
            surname = "some surname"
            name = "some name"
            middleName = "some middle name"
            birthday = "some birthday"
            services = listOf(Service("услуга1","1.1"),Service("услуга2","2,2"),Service("услуга3","30-000"),
                Service("услуга4",""),Service("услуга5","f"),Service("услуга6"," "),Service("услуга7","1f"),
                Service("услуга8","9"),Service("услуга10","10"),Service("услуга11","11"),Service("услуга12","12"),
                Service("услуга12","12"),Service("услуга12","12"),Service("услуга12","12"),Service("услуга12","12"))
//            services = listOf(Service("услуга1","1"))
        }
        return testAccount
    }
}
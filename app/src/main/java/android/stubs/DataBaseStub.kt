package android.stubs

object DataBaseStub {
    fun returnDataFromBase(barcode:String):String{
        return when(barcode){
            "Petrov" -> "Petrov"
            "Ivanov" -> "Ivanov"
            else -> "Data not found or wrong barcode"
        }
    }
}
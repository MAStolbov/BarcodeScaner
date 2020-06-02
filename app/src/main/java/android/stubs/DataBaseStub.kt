package android.stubs

object DataBaseStub {
    fun returnDataFromBase(barcode:String):String{
        return when(barcode){
            "Petrov" -> "Petrov"
            "Ivanov" -> "Ivanov"
            "Wikipedia" -> "Wikipedia barcode"
            else -> "Data not found or wrong barcode"
        }
    }



}
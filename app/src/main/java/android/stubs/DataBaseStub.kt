package android.stubs

object DataBaseStub {
    fun returnDataFromBase(barcode:String):String{
        return when(barcode){
            "Petrov" -> "Petrov"
            "Ivanov" -> "Ivanov"
            "Wikipedia" -> "Wikipedia barcode"
            "1111" -> "Some Numbers"
            else -> "Data not found or wrong barcode"
        }
    }



}
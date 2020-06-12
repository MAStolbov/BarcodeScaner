package android.stubs

object DataBaseStub {
    fun returnDataFromBase(barcode:String):String{
        return when(barcode){
            "Petrov" -> "Petrov"
            "Ivanov" -> "Ivanov"
            "Wikipedia" -> "Wikipedia barcode"
            "48" -> "Some Numbers"
            else -> "Data not found"
        }
    }



}
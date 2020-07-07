package android.dataStorage

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "Account")
data class Account(
    @Attribute(name = "AccountDate")
    var dateOfVisit: String? = "",
    @Attribute(name = "Surname")
    var surname: String = "",
    @Attribute(name = "Name")
    var name: String = "",
    @Attribute(name = "MiddleName")
    var middleName: String = "",
    @Attribute(name = "Birthday")
    var birthday: String = "",
    @Element
    var services: List<Service> = listOf()
)

@Xml(name = "Service")
data class Service(
    @Attribute(name = "Name")
    var name: String = "",
    @Attribute(name = "Price")
    var price: String = ""
)
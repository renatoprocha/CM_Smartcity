package ipvc.estg.cm_smartcity.api

class loginInfo (
    val status: String,
    val MSG: String,
    val data: Data
)

class Data (
    val id: Int,
    val nome: String,
    val password: String
)
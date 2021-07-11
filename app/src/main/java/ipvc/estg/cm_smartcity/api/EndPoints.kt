package ipvc.estg.cm_smartcity.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {
    @GET("users")
    fun getUsers(): Call<List<User>>

    @GET("/users/{id}")
    fun getUserById(@Path("id") id: Int): Call<User>

    @FormUrlEncoded
    @POST("reports/insert")
    fun insertReport(@Field("id_user") userId: Int,
                    @Field("tipo") tipo: String,
                    @Field("descricao") descricao: String,
                    @Field("lat") lat: Double,
                    @Field("longi") longi: Double,
                    @Field("image") image: String
                    ): Call<reportInsertInfo>

    @FormUrlEncoded
    @POST("users/login")
    fun login(@Field("nome") nome: String,
              @Field("password") password: String)
            : Call<loginInfo>

    @GET("reports")
    fun getReports(): Call<List<Report>>

    @GET("reports/{id}")
    fun getReport(@Path("id") id: Int): Call<Report>

    @GET("report/{tipo}")
    fun getReportTipo(@Path("tipo") tipo: String): Call<List<Report>>
}

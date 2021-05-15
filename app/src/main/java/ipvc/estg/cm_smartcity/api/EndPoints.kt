package ipvc.estg.cm_smartcity.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {
    @GET("users")
    fun getUsers(): Call<List<User>>

    @GET("/users/{id}")
    fun getUserById(@Path("id") id: Int): Call<User>

    @FormUrlEncoded
    @POST("ponto/insert")
    fun insertPonto(@Field("userId") userId: Int,
                    @Field("lat") lat: Int,
                    @Field("longi") longi: Int,
                    @Field("tipo") tipo: Int): Call<loginInfo>

    @FormUrlEncoded
    @POST("users/login")
    fun login(@Field("nome") nome: String,
                    @Field("password") password: String): Call<loginInfo>
}
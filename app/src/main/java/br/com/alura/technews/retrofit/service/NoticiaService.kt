package br.com.alura.technews.retrofit.service

import br.com.alura.technews.model.Noticia
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface NoticiaService {

  @GET("noticias")
  fun buscaTodasWebCliente(): Call<List<Noticia>>

  @POST("noticias")
  fun salvaWebCliente(@Body noticia: Noticia): Call<Noticia>

  @PUT("noticias/{id}")
  fun editaWebCliente(@Path("id") id: Long, @Body noticia: Noticia): Call<Noticia>

  @DELETE("noticias/{id}")
  fun removeWebCliente(@Path("id") id: Long): Call<Void>

  @GET("noticias")
  suspend fun buscaTodas(): Response<List<Noticia>>

  @POST("noticias")
  suspend fun salva(@Body noticia: Noticia): Response<Noticia>

  @PUT("noticias/{id}")
  suspend fun edita(@Path("id") id: Long, @Body noticia: Noticia): Response<Noticia>

  @DELETE("noticias/{id}")
  suspend fun remove(@Path("id") id: Long): Response<Void>
}
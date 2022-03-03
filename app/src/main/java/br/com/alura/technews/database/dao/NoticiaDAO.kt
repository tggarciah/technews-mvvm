package br.com.alura.technews.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import br.com.alura.technews.model.Noticia

@Dao
interface NoticiaDAO {

  @Query("SELECT * FROM Noticia ORDER BY id DESC")
  fun buscaTodos(): LiveData<List<Noticia>>

  @Query("SELECT * FROM Noticia WHERE id = :id")
  fun buscaPorId(id: Long): LiveData<Noticia?>

  @Insert(onConflict = REPLACE)
  suspend fun salva(noticia: Noticia)

  @Insert(onConflict = REPLACE)
  suspend fun salva(noticias: List<Noticia>)

  @Delete
  suspend fun remove(noticia: Noticia)
}

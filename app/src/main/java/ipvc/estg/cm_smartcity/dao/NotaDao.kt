package ipvc.estg.cm_smartcity.dao

import androidx.lifecycle.LiveData;
import androidx.room.*
import ipvc.estg.cm_smartcity.entities.Nota
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {

    @Query("SELECT * FROM nota_table ORDER BY id ASC")
    fun getAlphabetizedNotes(): Flow<List<Nota>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(nota: Nota)
    @Delete
    suspend fun delete(nota: Nota)
    @Update
    suspend fun update(nota: Nota)

    @Query("DELETE FROM nota_table")
    suspend fun deleteAll()
}

package ipvc.estg.cm_smartcity.dao

import androidx.lifecycle.LiveData;
import androidx.room.Dao
import androidx.room.Insert;
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ipvc.estg.cm_smartcity.entities.Nota
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {

    @Query("SELECT * FROM nota_table ORDER BY titulo ASC")
    fun getAlphabetizedNotes(): Flow<List<Nota>>

    @Query("SELECT * FROM nota_table WHERE titulo==:name")
    fun getNotasFromTitulo(name: String): LiveData<List<Nota>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(nota: Nota)

    @Query("DELETE FROM nota_table")
    suspend fun deleteAll()
}

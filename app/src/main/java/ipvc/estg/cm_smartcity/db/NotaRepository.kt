package ipvc.estg.cm_smartcity.db

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import ipvc.estg.cm_smartcity.dao.NotaDao
import ipvc.estg.cm_smartcity.entities.Nota
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class NotaRepository(private val NotaDao: NotaDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allNotas: Flow<List<Nota>> = NotaDao.getAlphabetizedNotes()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(nota: Nota) {
        NotaDao.insert(nota)
    }
}
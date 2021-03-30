package ipvc.estg.cm_smartcity

import android.app.Application
import ipvc.estg.cm_smartcity.db.NotaDB
import ipvc.estg.cm_smartcity.db.NotaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NotasApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { NotaDB.getDatabase(this, applicationScope) }
    val repository by lazy { NotaRepository(database.NotaDao()) }
}
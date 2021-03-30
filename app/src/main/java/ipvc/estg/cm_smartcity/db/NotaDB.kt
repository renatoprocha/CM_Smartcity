package ipvc.estg.cm_smartcity.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ipvc.estg.cm_smartcity.dao.NotaDao
import ipvc.estg.cm_smartcity.entities.Nota
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Nota::class), version = 1, exportSchema = false)
public abstract class NotaDB : RoomDatabase() {

    abstract fun NotaDao(): NotaDao



    private class NotaDBCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var NotaDao = database.NotaDao()

                    // Delete all content here.
                    NotaDao.deleteAll()

                    // Add sample words.
                    var nota = Nota(0,"nota0","ola")
                    NotaDao.insert(nota)

                    nota = Nota(1,"nota2", "ok")
                    NotaDao.insert(nota)

                    // TODO: Add your own words!
                    nota = Nota(2, "nota3", "claro")
                    NotaDao.insert(nota)

                    nota = Nota(3, "notaTeste", "teste")
                    NotaDao.insert(nota)


                }
            }
        }
    }



    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NotaDB? = null

        fun getDatabase(context: Context,
                        scope: CoroutineScope): NotaDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotaDB::class.java,
                    "nota_database"
                )
                    //.fallbackToDestructiveMigration()
                    .addCallback(NotaDBCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
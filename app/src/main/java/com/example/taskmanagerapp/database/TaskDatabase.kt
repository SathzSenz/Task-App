package com.example.taskmanagerapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskmanagerapp.model.Task
import java.util.concurrent.locks.Lock

@Database(entities = [Task::class], version = 2)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun getTaskDao(): TaskDao

    companion object{
        @Volatile
        private var instance: TaskDatabase? = null
        private val LOCK = Any()  //one create at a time

        operator fun invoke(context: Context) = instance ?:
        synchronized(LOCK){
            instance ?:
            createDatabase(context).also{
                instance = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TaskDatabase::class.java,
                "task_db"
            ).addMigrations(MIGRATION_1_2) // Changed: Added migration here
                .build()

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS tasks_new (" +
                            "id INTEGER PRIMARY KEY NOT NULL," +
                            "title TEXT NOT NULL," +
                            "description TEXT NOT NULL," +
                            "date TEXT NOT NULL," +
                            "time TEXT NOT NULL," +
                            "priority TEXT NOT NULL," +
                            "category TEXT NOT NULL DEFAULT ''" +  // New column with a default value
                            ")"
                )

                // Copy the data from the old table to the new table
                database.execSQL(
                    "INSERT INTO tasks_new (id, title, description, date, time, priority)" +
                            "SELECT id, title, description, date, time, priority FROM tasks"
                )

                // Remove the old table
                database.execSQL("DROP TABLE tasks")

                // Rename the new table to the original table name
                database.execSQL("ALTER TABLE tasks_new RENAME TO tasks")
                // Migration logic to migrate from version 1 to version 2
            }
        }
    }
}
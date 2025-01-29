package uk.ac.aber.dcs.cs31620.quizapp.saving

import android.app.Application
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Representing a Quiz with a title, subject, and description
data class Quiz(
    val id: Int,
    val title: String,
    val subject: String,
    val description: String,
    val questions: List<Question>
)

data class Question(
    val question: String,
    val answers: List<String>,
    val correctAnswer: String
)

// Entity for Quiz in Room Database
@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subject: String,
    val description: String
)

@Entity(tableName = "scores")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizId: Int,
    val score: Int,
    val date: Long
)

// Entity for Questions in Room Database
@Entity(
    tableName = "questions",
    foreignKeys = [ForeignKey(
        entity = QuizEntity::class,
        parentColumns = ["id"],
        childColumns = ["quizId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("quizId")]
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizId: Int,
    val question: String,
    val answers: String,  // Stored as JSON
    val correctAnswer: String
)

// TypeConverter to handle list of answers (for JSON or String format)
class Converters {

    // Convert list of answers into a comma-separated string
    @TypeConverter
    fun fromAnswersList(answers: List<String>): String {
        return answers.joinToString(",")
    }

    // Convert comma-separated string back to list of answers
    @TypeConverter
    fun toAnswersList(answers: String): List<String> {
        return answers.split(",")
    }
}



// DAO for Quiz and Question operations
@Dao
interface QuizDao {
    // --- Quiz Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizEntity): Long

    @Update
    suspend fun updateQuiz(quiz: QuizEntity)

    @Query("DELETE FROM quizzes")
    suspend fun deleteAllQuizzes()

    @Query("SELECT * FROM quizzes WHERE id = :quizId")
    suspend fun getQuizById(quizId: Int): QuizEntity?

    @Query("SELECT * FROM quizzes")
    suspend fun getAllQuizzes(): List<QuizEntity>

    @Query("SELECT COUNT(*) FROM quizzes")
    suspend fun getQuizCount(): Int

    @Query("SELECT COUNT(*) FROM quizzes WHERE title = '' AND subject = '' AND description = ''")
    suspend fun getEmptyQuizCount(): Int



    // --- Question Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleQuestion(question: QuestionEntity)

    @Query("SELECT * FROM questions WHERE quizId = :quizId")
    suspend fun getQuestionsForQuiz(quizId: Int): List<QuestionEntity>

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()

    @Query("DELETE FROM questions WHERE quizId = :quizId")
    suspend fun deleteAllQuestionsForQuiz(quizId: Int)

    @Query("DELETE FROM questions WHERE id = :questionId")
    suspend fun deleteQuestionById(questionId: Int)

    @Query("DELETE FROM questions WHERE quizId = :quizId")
    suspend fun deleteQuestionsForQuiz(quizId: Int)


    // --- Score Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: ScoreEntity)

    @Query("SELECT * FROM scores WHERE quizId = :quizId ORDER BY date DESC")
    suspend fun getScoresForQuiz(quizId: Int): List<ScoreEntity>

    @Query("DELETE FROM scores WHERE quizId = :quizId")
    suspend fun deleteScoresForQuiz(quizId: Int)
}

val MIGRATION_1_2 = object : Migration(1,2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `scores` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `quizId` INTEGER NOT NULL, `score` INTEGER NOT NULL, `date` INTEGER NOT NULL)")
    }

}

// Database class
@Database(entities = [QuizEntity::class, QuestionEntity::class, ScoreEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao

    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null

        fun getDatabase(context: Application): QuizDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

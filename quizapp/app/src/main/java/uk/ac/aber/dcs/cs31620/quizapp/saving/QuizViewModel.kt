package uk.ac.aber.dcs.cs31620.quizapp.saving

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// ViewModel Class with Room Integration
class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val _quizzes = MutableLiveData<List<Quiz>>(emptyList())
    val quizzes: LiveData<List<Quiz>> get() = _quizzes

    var currentQuizIndex by mutableStateOf(0)
    var currentQuestionIndex by mutableStateOf(0)
    var selectedAnswer by mutableStateOf("")
    var score by mutableStateOf(0)
    var pastScores by mutableStateOf(listOf<Int>())
    var currentQuizQuestions = mutableStateOf(listOf<Question>())

    //editing
    var isEditingQuiz by mutableStateOf(false)
    var editedQuizId by mutableStateOf(0)
    var editedQuizTitle by mutableStateOf("")
    var editedQuizSubject by mutableStateOf("")
    var editedQuizDescription by mutableStateOf("")


    private val quizDao = QuizDatabase.getDatabase(application).quizDao()

    init {
        viewModelScope.launch {
            clearDatabase() // Clear existing quizzes and questions
            insertDefaultQuiz()
            createEmptyQuizIfNeeded() // Create an empty quiz if needed
            loadQuizzesFromDatabase()
        }
    }

    fun getQuizById(quizId: Int): Quiz? {
        return quizzes.value?.find { it.id == quizId }
    }

    // Load quizzes and their questions from Room database
    private suspend fun loadQuizzesFromDatabase() {
        val savedQuizzes = quizDao.getAllQuizzes()
        val loadedQuizzes = savedQuizzes.map { quizEntity ->
            val questions = quizDao.getQuestionsForQuiz(quizEntity.id).map {
                Question(it.question, it.answers.split(","), it.correctAnswer)
            }
            Quiz(
                id = quizEntity.id,
                quizEntity.title,
                quizEntity.subject,
                quizEntity.description,
                questions
            )
        }
        _quizzes.postValue(loadedQuizzes)
    }

    private suspend fun loadPastScores() {
        val currentQuiz = quizzes.value?.get(currentQuizIndex)
        currentQuiz?.let {
            val scores = quizDao.getScoresForQuiz(it.id)
            pastScores = scores.map { it.score }
        }
    }

    // Save quiz and its questions to Room database
    fun saveQuiz(title: String, subject: String, description: String) {
        val newQuiz = Quiz(id = 0, title, subject, description, currentQuizQuestions.value)

        viewModelScope.launch {
            val quizEntity = QuizEntity(title = title, subject = subject, description = description)
            val quizId = quizDao.insertQuiz(quizEntity)  // Get the inserted quiz ID

            val questionEntities = currentQuizQuestions.value.map {
                QuestionEntity(
                    quizId = quizId.toInt(),
                    question = it.question,
                    answers = it.answers.joinToString(","),
                    correctAnswer = it.correctAnswer
                )
            }
            quizDao.insertQuestions(questionEntities)

            loadQuizzesFromDatabase()
        }
    }

    private suspend fun insertDefaultQuiz() {
        val defaultQuizEntity = QuizEntity(
            title = "General Knowledge Quiz",
            subject = "General Knowledge",
            description = "A quiz to test your general knowledge"
        )
        val quizId = quizDao.insertQuiz(defaultQuizEntity)

        val defaultQuestions = listOf(
            QuestionEntity(quizId = quizId.toInt(), question = "What is the capital of France?", answers = "Paris,London,Berlin,Madrid", correctAnswer = "Paris"),
            QuestionEntity(quizId = quizId.toInt(), question = "Which planet is known as the Red Planet?", answers = "Earth,Mars,Venus,Jupiter", correctAnswer = "Mars"),
            QuestionEntity(quizId = quizId.toInt(), question = "Who wrote 'Hamlet'?", answers = "Shakespeare,Chaucer,Dickens,Austen", correctAnswer = "Shakespeare"),
            QuestionEntity(quizId = quizId.toInt(), question = "What is 2 + 2?", answers = "3,4,5,6", correctAnswer = "4"),
            QuestionEntity(quizId = quizId.toInt(), question = "Which gas do plants use for photosynthesis?", answers = "Oxygen,Nitrogen,Carbon Dioxide,Hydrogen", correctAnswer = "Carbon Dioxide"),
            QuestionEntity(quizId = quizId.toInt(), question = "What is the largest ocean on Earth?", answers = "Atlantic,Indian,Arctic,Pacific", correctAnswer = "Pacific"),
            QuestionEntity(quizId = quizId.toInt(), question = "Who painted the Mona Lisa?", answers = "Da Vinci,Van Gogh,Picasso,Michelangelo", correctAnswer = "Da Vinci"),
            QuestionEntity(quizId = quizId.toInt(), question = "What is the tallest mountain in the world?", answers = "K2,Everest,Kilimanjaro,Denali", correctAnswer = "Everest"),
            QuestionEntity(quizId = quizId.toInt(), question = "Which element has the symbol 'O'?", answers = "Oxygen,Gold,Silver,Iron", correctAnswer = "Oxygen"),
            QuestionEntity(quizId = quizId.toInt(), question = "How many continents are there?", answers = "5,6,7,8", correctAnswer = "7")
        )

        quizDao.insertQuestions(defaultQuestions)
    }


    private suspend fun createEmptyQuizIfNeeded() {
        val emptyQuizExists = quizDao.getEmptyQuizCount() > 0
        if (!emptyQuizExists) {
            val quizEntity = QuizEntity(title = "", subject = "", description = "")
            quizDao.insertQuiz(quizEntity)
        }
    }

    private suspend fun clearDatabase() {
        quizDao.deleteAllQuizzes()
        quizDao.deleteAllQuestions()
    }

    suspend fun cancelEditing() {
        isEditingQuiz = false
        loadQuizzesFromDatabase()
    }

    fun addQuestion(question: Question) {
        currentQuizQuestions.value = currentQuizQuestions.value + question
    }

    fun removeQuestion(question: Question) {
        currentQuizQuestions.value = currentQuizQuestions.value - question
    }

    fun nextQuestion() {
        val currentQuiz = quizzes.value?.get(currentQuizIndex)
        if (currentQuiz != null && selectedAnswer == currentQuiz.questions[currentQuestionIndex].correctAnswer) {
            score++
        }
        if (currentQuestionIndex < currentQuiz?.questions?.size?.minus(1) ?: 0) {
            currentQuestionIndex++
        } else {
            pastScores = pastScores + score
            resetQuiz()
        }
    }

    fun updateQuiz(quizIndex: Int, title: String, subject: String, description: String) {
        val quiz = quizzes.value?.find { it.id == quizIndex }
        val updatedQuiz = quiz?.copy(title = title, subject = subject, description = description)

        if (updatedQuiz != null) {
            // Create a new list by applying the updated quiz
            _quizzes.value = _quizzes.value.orEmpty().toMutableList().apply {
                val index = indexOfFirst { it.id == quizIndex }
                if (index != -1) {
                    set(index, updatedQuiz)
                }
            }
            viewModelScope.launch {
                val quizEntity = QuizEntity(
                    id = updatedQuiz.id,
                    title = updatedQuiz.title,
                    subject = updatedQuiz.subject,
                    description = updatedQuiz.description
                )
                quizDao.updateQuiz(quizEntity)
            }
        }
    }

    fun resetQuiz() {
        currentQuestionIndex = 0
        selectedAnswer = ""
        score = 0
    }

    fun finishQuiz() {
        val currentQuiz = quizzes.value?.get(currentQuizIndex)
        val updatedPastScores = pastScores.takeLast(4)
        currentQuiz?.let {
            viewModelScope.launch {
                val scoreEntity = ScoreEntity(quizId = it.id, score = score, date = System.currentTimeMillis())
                quizDao.insertScore(scoreEntity)
                loadPastScores()
            }
        }
        pastScores = updatedPastScores // Update the past scores list
    }

    fun updateQuestionsForQuiz(quizId: Int, updatedQuestions: List<Question>) {
        viewModelScope.launch {
            val quizEntity = quizDao.getQuizById(quizId)
            if (quizEntity != null) {
                quizDao.deleteQuestionsForQuiz(quizId)
                val questionEntities = updatedQuestions.map {
                    QuestionEntity(
                        quizId = quizId,
                        question = it.question,
                        answers = it.answers.joinToString(","),
                        correctAnswer = it.correctAnswer
                    )
                }
                quizDao.insertQuestions(questionEntities)
                loadQuizzesFromDatabase()
            }
        }
    }

    fun addQuestionToQuiz(quizId: Int, newQuestion: Question) {
        val currentQuiz = quizzes.value?.find { it.id == quizId }
        currentQuiz?.let {
            val updatedQuestions = it.questions + newQuestion
            updateQuestionsForQuiz(quizId, updatedQuestions)
        }
    }
    fun deleteQuestionFromQuiz(quizId: Int, questionIndex: Int) {
        val currentQuiz = quizzes.value?.find { it.id == quizId }
        currentQuiz?.let {
            val updatedQuestions = it.questions.toMutableList().apply {
                removeAt(questionIndex)
            }
            updateQuestionsForQuiz(quizId, updatedQuestions)
        }
    }
}

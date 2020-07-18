package com.hegdeapps.quizapp

/**
 * Created by usha on 16-07-2015.
 * Common constants used in the project.
 * TODO how many should go to string res
 */
internal object MyConstants {
    val DB_SQL_FILE_NAME = "questionsdb"
    val OBJ_QNS = "objectivequestions"
    val ALL = "All"
    val ID = "_id"
    val QUIZ = ""
    val CURSOR_POSITION = "cursor position"
    val QUESTION_NUM_LIST = "question number list"
    val QUIZ_SCORE = "quiz score"
    val QUIZ_CORRECT_ANSWERED = "quiz correct answered"
    val QUIZ_WRONG_ANSWERED = "quiz wrong answered"
    val SOUNDCORRECT = 1
    val SOUNDWRONG = 2
    val YOUR_ANSWER_LIST = "your answers list"

    val SEARCH_CURSOR = 1
    val ALL_CURSOR = 3
    val TOPIC_CURSOR = 2//TODO complete the link below
    val linkToMyApp =
        "Find all C interview questions with solutions in the app 'C Questions'. Download now: https://play.google.com/store/apps/details?id=com.hegdeapps.cquestions"

    val OBJ_DETAILS_ROW = "CurrentRowOfObjectiveDetails"//used for saving state

}

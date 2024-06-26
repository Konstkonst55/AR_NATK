package com.example.ar_natk.utils

object Constants {
    private const val logPath = "my_app"

    const val RUN_PREFS_KEY = "first_run_prefs_key"
    const val RUN_KEY = "first_run_key"

    const val MODEL_ITEM_FILE_PATH = "model_item.json"

    const val USER_PREFS_KEY = "user_prefs_key"
    const val USER_NAME_KEY = "user_name_key"
    const val USER_ID_KEY = "user_id_key"
    const val USER_COLLECTION_KEY = "user_collection_key"

    const val DEF_TYPE_DRAWABLE = "drawable"

    const val LOG_FIREBASE = "$logPath.firebase"
    const val LOG_APP = logPath

    const val FIRE_DOC_USER_NAME = "name"
    const val FIRE_DOC_USER_COLLECTION = "collection"
    const val FIRE_DOC_USER_SCORE = "count"
    const val FIRE_DOC_USER_DATE = "date"
    const val FIRE_COLLECTION_USER = "users"

    const val VALIDATION_EMPTY = "Поле должно быть заполнено"
    const val VALIDATION_VALID = "Имя введено верно"

    const val AUTH_FAILURE = "Что-то пошло не так"
    const val AUTH_SUCCES = "Авторизация успешна"
}
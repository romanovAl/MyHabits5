package ru.romanoval.domain.model

import ru.romanoval.domain.model.restful.Uid

open class ResponseData(
    val errorMessage: String?,
    val isSuccessful: Boolean
) {

    class GetResponseData(
        val habitsList: List<Habit>?,
        errorMessage: String?,
        isSuccessful: Boolean
    ) : ResponseData(errorMessage, isSuccessful)

    class PutResponseData(
        val uid: Uid?,
        errorMessage: String?,
        isSuccessful: Boolean
    ) : ResponseData(errorMessage, isSuccessful)

}
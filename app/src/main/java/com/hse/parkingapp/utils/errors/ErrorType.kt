package com.hse.parkingapp.utils.errors

/**
 * Represents a sealed class for defining different types of errors with associated data.
 */
sealed class ErrorType {
    /**
     * Represents the error type for when the user does not have a car.
     */
    object NoCar: ErrorType()

    /**
     * Represents an unknown error type.
     */
    object UnknownError: ErrorType()

    /**
     * Represent no error state.
     */
    object NoError: ErrorType()
}

/**
 * Represents the current error state.
 * @property error The error object. Default value is ErrorType.NoError.
 */
data class CurrentError(
    val error: ErrorType = ErrorType.NoError
)

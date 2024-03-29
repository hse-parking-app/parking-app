package com.hse.parkingapp.utils.auth

import com.hse.parkingapp.model.Employee

sealed class AuthResult<T>(val employee: Employee? = null) {
    class Prepared<T>(employee: Employee? = null) : AuthResult<T>(employee)
    class Authorized<T>(employee: Employee? = null) : AuthResult<T>(employee)
    class Unauthorized<T> : AuthResult<T>()
    class WrongCredentials<T> : AuthResult<T>()
    class UnknownError<T> : AuthResult<T>()
    class WrongTime<T> : AuthResult<T>()
}

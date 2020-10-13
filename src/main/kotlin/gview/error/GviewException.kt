package gview.error

import java.lang.Exception

open class GviewException(message: String?): Exception(message) {
    constructor(e: Exception) : this(e.message)
}
package com.ryzingtitan.cashcub.presentation.configuration

import com.ryzingtitan.cashcub.domain.transactions.exceptions.TransactionDoesNotExistException
import com.ryzingtitan.cashcub.shared.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    private val log = getLogger<GlobalExceptionHandler>()

    @ExceptionHandler(TransactionDoesNotExistException::class)
    fun handleTransactionDoesNotExistException(ex: TransactionDoesNotExistException): ResponseEntity<String> {
        log.error("TransactionDoesNotExistException: ${ex.message}")
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<String> {
        log.error("IllegalArgumentException: ${ex.message}")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
    }
}

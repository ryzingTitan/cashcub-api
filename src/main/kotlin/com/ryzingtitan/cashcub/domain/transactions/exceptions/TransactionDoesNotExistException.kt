package com.ryzingtitan.cashcub.domain.transactions.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class TransactionDoesNotExistException(
    message: String,
) : Exception(message)

package com.ryzingtitan.cashcub.domain.budgetitems.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class BudgetItemDoesNotExistException(
    message: String,
) : Exception(message)

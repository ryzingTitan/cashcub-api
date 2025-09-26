package com.ryzingtitan.cashcub.domain.budgets.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.CONFLICT)
class DuplicateBudgetException(
    message: String,
) : Exception(message)

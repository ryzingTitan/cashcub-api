package com.ryzingtitan.cashcub.domain.budgetitems.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.CONFLICT)
class DuplicateBudgetItemException(
    message: String,
) : Exception(message)

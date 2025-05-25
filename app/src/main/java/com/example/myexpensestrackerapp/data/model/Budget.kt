package com.example.myexpensestrackerapp.data.model

data class Budget(
    val category: ExpenseCategory,
    val amount: Double,
    val monthYear: String
)
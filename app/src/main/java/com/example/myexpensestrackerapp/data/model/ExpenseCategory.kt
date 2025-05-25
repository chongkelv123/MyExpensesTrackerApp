// 1. Create ExpenseCategory.kt in com.example.expensetrackerapp.data.model package
package com.example.myexpensetrackerapp.data.model

enum class ExpenseCategory(val displayName: String) {
    NTUC("NTUC"),
    MEAL("Meal"),
    FUEL("Fuel"),
    JL_JE("JL & JE"),
    OTHERS("Others"),
    CASH("Cash"),
    CREDIT_CARD("Credit Card")
}
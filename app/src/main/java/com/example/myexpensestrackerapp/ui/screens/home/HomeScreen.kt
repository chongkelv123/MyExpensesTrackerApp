package com.example.myexpensetrackerapp.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myexpensetrackerapp.data.model.Expense
import com.example.myexpensetrackerapp.data.model.ExpenseCategory
import com.example.myexpensetrackerapp.ui.components.CategoryCard
import com.example.myexpensetrackerapp.ui.components.MonthYearSelector
import com.example.myexpensetrackerapp.ui.components.MonthlySummaryCard
import com.example.myexpensetrackerapp.ui.components.formatCurrency
import com.example.myexpensetrackerapp.ui.components.getCategoryColor
import com.example.myexpensetrackerapp.ui.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch
import org.threeten.bp.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: ExpenseViewModel,
    onNavigateToCategory: (ExpenseCategory) -> Unit,
    onNavigateToTransactionDetail: (Long) -> Unit
) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val monthlySummary by viewModel.monthlySummary.collectAsState()
    val monthlyExpenses by viewModel.monthlyExpenses.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Month selector at the top
        MonthYearSelector(
            currentMonth = currentMonth,
            onPreviousMonth = { viewModel.previousMonth() },
            onNextMonth = { viewModel.nextMonth() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Monthly spending summary
        MonthlySummaryCard(
            totalSpent = monthlySummary.totalSpent,
            totalBudget = monthlySummary.totalBudget,
            percentage = if (monthlySummary.totalBudget > 0) {
                (monthlySummary.totalSpent / monthlySummary.totalBudget).toFloat().coerceAtMost(1.0f)
            } else 0f
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category cards
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(monthlySummary.categorySummaries) { categorySummary ->
                CategoryCard(
                    categorySummary = categorySummary,
                    onAddExpense = { onNavigateToCategory(categorySummary.category) }
                )
            }

            // Recent transactions section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Show recent transactions or a message if none exist
            if (monthlyExpenses.isNotEmpty()) {
                items(monthlyExpenses.take(10)) { expense ->
                    SwipeableTransactionItem(
                        expense = expense,
                        onItemClick = { onNavigateToTransactionDetail(expense.id) },
                        onDelete = { viewModel.deleteExpense(expense) }
                    )
                }
            } else {
                item {
                    EmptyTransactionsMessage()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTransactionItem(
    expense: Expense,
    onItemClick: () -> Unit,
    onDelete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                scope.launch {
                    onDelete()
                }
                true
            } else {
                false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        dismissContent = {
            TransactionItem(
                expense = expense,
                onClick = onItemClick
            )
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionItem(
    expense: Expense,
    onClick: () -> Unit
) {
    val categoryColor = getCategoryColor(expense.category)
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = categoryColor,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.description.ifEmpty { "(No description)" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "${expense.category.displayName} • ${expense.date.format(dateFormatter)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = formatCurrency(expense.amount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EmptyTransactionsMessage() {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No expenses yet for this month",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Add your first expense by selecting a category",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
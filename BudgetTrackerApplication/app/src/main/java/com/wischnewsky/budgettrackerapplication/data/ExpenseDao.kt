package com.wischnewsky.budgettrackerapplication.data

import android.arch.persistence.room.*

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses")
    fun getAllExpenses(): List<Expense>

    @Insert
    fun insertExpense(expense: Expense): Long

    @Insert
    fun insertExpenses(vararg expense: Expense): List<Long>

    @Delete
    fun deleteExpense(expense: Expense)

    @Update
    fun updateExpense(expense: Expense)

    @Query("DELETE FROM expenses")
    fun deleteAll()
}
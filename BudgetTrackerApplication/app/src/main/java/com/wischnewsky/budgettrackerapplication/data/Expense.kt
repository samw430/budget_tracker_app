package com.wischnewsky.budgettrackerapplication.data

import java.io.Serializable
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) var expenseId : Long?,
    @ColumnInfo(name = "itemName") var itemName: String,
    @ColumnInfo(name = "purchaseDate") var purchaseDate: String,
    @ColumnInfo(name = "cost") var cost: Long,
    @ColumnInfo(name = "category") var category: String
) : Serializable
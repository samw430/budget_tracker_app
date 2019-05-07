package com.wischnewsky.budgettrackerapplication.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wischnewsky.budgettrackerapplication.R
import com.wischnewsky.budgettrackerapplication.ScrollingActivity
import com.wischnewsky.budgettrackerapplication.data.AppDatabase
import com.wischnewsky.budgettrackerapplication.data.Expense
import kotlinx.android.synthetic.main.expense_row.view.*
import java.util.*

class ExpenseAdapter : RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {


    var expenseItems = mutableListOf<Expense>()

    private val context: Context


    constructor(context: Context, listTodos: List<Expense>) : super() {
        this.context = context
        expenseItems.addAll(listTodos)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val expenseRowView = LayoutInflater.from(context).inflate(
            R.layout.expense_row, viewGroup, false
        )
        return ViewHolder(expenseRowView)
    }

    override fun getItemCount(): Int {
        return expenseItems.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val expense  = expenseItems.get(viewHolder.adapterPosition)

        viewHolder.tvDate.text = expense.purchaseDate
        viewHolder.tvName.text = expense.itemName
        viewHolder.tvCost.text = expense.cost.toString()
        viewHolder.tvCategory.text = expense.catego ry
        when(viewHolder.tvCategory.text){


            "Groceries" -> viewHolder.ivIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_food_icon))
            "Clothing" -> viewHolder.ivIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_drink_icon))
            "Lodging" -> viewHolder.ivIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_drink_icon))
            "Transportation" -> viewHolder.ivIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_transportation_icon))
            else -> viewHolder.ivIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_other_icon))


        }
        viewHolder.ivIcon.setImageDrawable()


        viewHolder.btnDelete.setOnClickListener {
            deleteExpense(viewHolder.adapterPosition)
        }

        viewHolder.btnEdit.setOnClickListener {
            (context as ScrollingActivity).showEditExpenseDialog(expense,
                viewHolder.adapterPosition)
        }
    }

    fun updateExpense(expense: Expense) {
        Thread{
            AppDatabase.getInstance(context).expenseDao().updateExpense(expense)


        }.start()
    }

    fun updateExpense(expense: Expense, editIndex: Int) {
        expenseItems.set(editIndex, expense)
        notifyItemChanged(editIndex)
    }


    fun addExpense(expense: Expense) {
        expenseItems.add(0, expense)
        //notifyDataSetChanged()
        notifyItemInserted(0)
    }

    fun deleteExpense(deletePosition: Int) {
        Thread{
            AppDatabase.getInstance(context).expenseDao().deleteExpense(expenseItems.get(deletePosition))

            (context as ScrollingActivity).runOnUiThread {
                expenseItems.removeAt(deletePosition)
                notifyItemRemoved(deletePosition)
            }
        }.start()
    }

    fun removeAll(){
        expenseItems.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName = itemView.tvName
        var tvDate = itemView.tvDate
        var tvCost = itemView.tvCost
        var btnDelete = itemView.btnDelete
        var btnEdit = itemView.btnEdit
        var tvCategory = itemView.tvCategory
        var ivIcon = itemView.ivIcon
    }

}
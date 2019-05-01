package com.wischnewsky.budgettrackerapplication

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.wischnewsky.budgettrackerapplication.data.Expense
import kotlinx.android.synthetic.main.new_expense_dialog.view.*
import java.lang.RuntimeException
import java.util.*

class ExpenseDialog : DialogFragment() {

    interface ExpenseHandler {
        fun expenseCreated(item: Expense)
        fun expenseUpdated(item: Expense)
    }

    private lateinit var expenseHandler: ExpenseHandler

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is ExpenseHandler) {
            expenseHandler = context
        } else {
            throw RuntimeException(
                "The activity does not implement the TodoHandlerInterface")
        }
    }

    private lateinit var etExpenseName: EditText
    private lateinit var etExpenseDate: EditText
    private lateinit var etExpenseCost: EditText
    private lateinit var spinnerCategory: Spinner

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("New Expense")

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.new_expense_dialog, null
        )
        //etTodoDate = rootView.findViewById(R.id.etTodoText)

        val categoriesAdapter = ArrayAdapter.createFromResource(
            context,
            R.array.array_categories, android.R.layout.simple_spinner_item
        )
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rootView.categorySpinner.adapter = categoriesAdapter

        spinnerCategory = rootView.categorySpinner
        etExpenseName = rootView.etExpense
        etExpenseDate = rootView.etDate
        etExpenseCost = rootView.etCost

        builder.setView(rootView)

        val arguments = this.arguments

        // IF I AM IN EDIT MODE
        if (arguments != null && arguments.containsKey(
                ScrollingActivity.KEY_ITEM_TO_EDIT)) {

            val expenseItem = arguments.getSerializable(
                ScrollingActivity.KEY_ITEM_TO_EDIT
            ) as Expense

            etExpenseName.setText(expenseItem.itemName)
            etExpenseDate.setText(expenseItem.purchaseDate)
            etExpenseCost.setText(expenseItem.cost.toString())

            builder.setTitle("Edit Expense")
        }

        builder.setPositiveButton("OK") {
                dialog, witch -> // empty
        }

        return builder.create()
    }


    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etExpenseName.text.isNotEmpty()) {
                val arguments = this.arguments
                // IF EDIT MODE
                if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_ITEM_TO_EDIT)) {
                    handleTodoEdit()
                } else {
                    handleTodoCreate()
                }

                dialog.dismiss()
            } else {
                etExpenseName.error = "This field can not be empty"
            }
        }
    }

    private fun handleTodoCreate() {
        expenseHandler.expenseCreated(
            Expense(
                null,
                etExpenseName.text.toString(),
                etExpenseDate.text.toString(),
                etExpenseCost.text.toString().toLong(),
                spinnerCategory.selectedItem.toString()
            )
        )
    }

    private fun handleTodoEdit() {
        val expenseToEdit = arguments?.getSerializable(
            ScrollingActivity.KEY_ITEM_TO_EDIT
        ) as Expense

        expenseToEdit.itemName = etExpenseName.text.toString()
        expenseToEdit.purchaseDate = etExpenseDate.text.toString()
        expenseToEdit.cost = etExpenseCost.text.toString().toLong()
        expenseToEdit.category = spinnerCategory.selectedItem.toString()

        expenseHandler.expenseUpdated(expenseToEdit)
    }

}
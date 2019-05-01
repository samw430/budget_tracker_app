package com.wischnewsky.budgettrackerapplication

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.wischnewsky.budgettrackerapplication.adapter.ExpenseAdapter
import com.wischnewsky.budgettrackerapplication.data.AppDatabase
import com.wischnewsky.budgettrackerapplication.data.Expense
import kotlinx.android.synthetic.main.activity_scrolling.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.io.Serializable

class ScrollingActivity : AppCompatActivity(), ExpenseDialog.ExpenseHandler {


    companion object {
        val KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT"
    }

    lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)

        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            showAddExpenseDialog()
        }

        if (!wasOpenedEarlier()) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.fab)
                .setPrimaryText("New TODO")
                .setSecondaryText("Click here to create new todo items")
                .show()
        }

        saveFirstOpenInfo()

        initRecyclerViewFromDB()
    }

    fun saveFirstOpenInfo() {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()
        editor.putBoolean("KEY_WAS_OPEN", true)
        editor.apply()
    }

    fun wasOpenedEarlier() : Boolean {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        return sharedPref.getBoolean("KEY_WAS_OPEN", false)
    }



    private fun initRecyclerViewFromDB() {
        Thread {
            var expenseList = AppDatabase.getInstance(this@ScrollingActivity).expenseDao().getAllExpenses()

            runOnUiThread {
                // Update UI

                expenseAdapter = ExpenseAdapter(this, expenseList)

                recyclerTodo.layoutManager = LinearLayoutManager(this)

                //recyclerTodo.layoutManager = GridLayoutManager(this, 2)
                //recyclerTodo.layoutManager = StaggeredGridLayoutManager(2,
                //    StaggeredGridLayoutManager.VERTICAL)

                recyclerTodo.adapter = expenseAdapter

                val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
                recyclerTodo.addItemDecoration(itemDecoration)
            }

        }.start()
    }

    private fun showAddExpenseDialog() {
        ExpenseDialog().show(supportFragmentManager, "TAG_TODO_DIALOG")
    }

    var editIndex: Int = -1

    public fun showEditExpenseDialog(expenseToEdit: Expense, idx: Int) {
        editIndex = idx
        val editItemDialog = ExpenseDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM_TO_EDIT, expenseToEdit)
        editItemDialog.arguments = bundle

        editItemDialog.show(supportFragmentManager,
            "EDITITEMDIALOG")
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_delete_all){
            Thread{
                AppDatabase.getInstance(this@ScrollingActivity).expenseDao().deleteAll()
                runOnUiThread {
                    expenseAdapter.removeAll()
                }

            }.start()
        } else if (item.itemId == R.id.action_graph) {
            val startIntent = Intent(this@ScrollingActivity, ChartActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("DATA_SET", expenseAdapter.expenseItems as Serializable)
            startIntent.putExtras(bundle)
            startActivity(startIntent)
        }else if (item.itemId == R.id.action_map) {

        }


        return super.onOptionsItemSelected(item)
    }



    override fun expenseCreated(item: Expense) {
        Thread {
            var newId = AppDatabase.getInstance(this).expenseDao().insertExpense(item)

            item.expenseId = newId

            runOnUiThread {
                expenseAdapter.addExpense(item)
            }
        }.start()
    }

    override fun expenseUpdated(item: Expense) {
        Thread {
            AppDatabase.getInstance(this).expenseDao().updateExpense(item)

            runOnUiThread {
                expenseAdapter.updateExpense(item, editIndex)
            }
        }.start()
    }
}

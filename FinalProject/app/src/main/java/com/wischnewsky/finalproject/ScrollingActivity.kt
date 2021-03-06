package com.wischnewsky.finalproject

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.wischnewsky.finalproject.adapter.ExpenseAdapter
import com.wischnewsky.finalproject.data.AppDatabase
import com.wischnewsky.finalproject.touch.SwipeToDeleteCallback
import com.wischnewsky.finalproject.data.Expense
import com.wischnewsky.finalproject.touch.SwipeToEditCallback
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


        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerTodo.adapter as ExpenseAdapter
                if (direction == 8){ //if swiped RIGHT
                    adapter.deleteExpense(viewHolder.adapterPosition)
                }
                else if (direction == 4) //if swiped LEFT
                    showEditExpenseDialog(adapter.expenseItems[viewHolder.adapterPosition], viewHolder.adapterPosition)
                adapter.updateExpense(adapter.expenseItems[viewHolder.adapterPosition], viewHolder.adapterPosition)

            }
        }
        val swipeHandler2 = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerTodo.adapter as ExpenseAdapter
                if (direction == 8){ //if swiped RIGHT
                    adapter.deleteExpense(viewHolder.adapterPosition)
                }
                else if (direction == 4) //if swiped LEFT
                    showEditExpenseDialog(adapter.expenseItems[viewHolder.adapterPosition], viewHolder.adapterPosition)
                adapter.updateExpense(adapter.expenseItems[viewHolder.adapterPosition], viewHolder.adapterPosition)

            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        val itemTouchHelper2 = ItemTouchHelper(swipeHandler2)
        itemTouchHelper2.attachToRecyclerView(recyclerTodo)
        itemTouchHelper.attachToRecyclerView(recyclerTodo)

        if (!wasOpenedEarlier()) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.fab)
                .setPrimaryText("New Expense")
                .setSecondaryText("Click here to create new expense items")
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
        } else if (item.itemId == R.id.action_time_graph) {
            val startIntent = Intent(this@ScrollingActivity, TimeLineActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("DATA_SET", expenseAdapter.expenseItems as Serializable)
            startIntent.putExtras(bundle)
            startActivity(startIntent)
        } else if (item.itemId == R.id.action_category_graph) {
            val startIntent = Intent(this@ScrollingActivity, ChartActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("DATA_SET", expenseAdapter.expenseItems as Serializable)
            startIntent.putExtras(bundle)
            startActivity(startIntent)
        }else if (item.itemId == R.id.action_map) {
            val startIntent = Intent(this@ScrollingActivity, MapsActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("DATA_SET", expenseAdapter.expenseItems as Serializable)
            startIntent.putExtras(bundle)
            startActivity(startIntent)
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


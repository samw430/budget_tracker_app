package com.wischnewsky.finalproject

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.DropBoxManager
import android.util.Log
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.wischnewsky.finalproject.data.AppDatabase
import com.wischnewsky.finalproject.data.Expense
import kotlinx.android.synthetic.main.activity_chart.*

class ChartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        pieChart.setUsePercentValues(true)

        var expenseList = intent.extras!!["DATA_SET"] as MutableList<Expense>


        val categoryBreakdown: MutableMap<String, Long> = mutableMapOf()
        var totalSpent = 0L

        expenseList.forEach {
            if (categoryBreakdown.containsKey(it.category)) {
                val previous = categoryBreakdown.get(it.category)
                categoryBreakdown.replace(it.category, previous!! + it.cost)
            } else {
                categoryBreakdown.put(it.category, it.cost)
            }

            totalSpent = totalSpent + it.cost
        }

        val yVals = ArrayList<PieEntry>()

        categoryBreakdown.forEach {
            val percentage = (it.value*100F)*totalSpent
            yVals.add(PieEntry(percentage, it.key))
        }


        val dataSet = PieDataSet(yVals, "Expense Breakdown")
        dataSet.valueTextSize = 0f
        val colors = java.util.ArrayList<Int>()
        colors.add(Color.MAGENTA)
        colors.add(Color.BLUE)
        colors.add(Color.RED)
        colors.add(Color.GREEN)
        colors.add(Color.GRAY)

        dataSet.setColors(colors)
        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.centerTextRadiusPercent = 0f
        pieChart.isDrawHoleEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.description.isEnabled = false
        pieChart.invalidate()

    }
}
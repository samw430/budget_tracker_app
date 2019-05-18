package com.wischnewsky.finalproject

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.wischnewsky.finalproject.data.Expense
import kotlinx.android.synthetic.main.activity_chart.*
import kotlinx.android.synthetic.main.activity_time_line.*

class TimeLineActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_line)

        //line_chart.setUsePercentValues(true)

        var expenseList = intent.extras!!["DATA_SET"] as MutableList<Expense>


        val dateBreakDown: MutableMap<String, Long> = mutableMapOf()
        var totalSpent = 0L

        expenseList.forEach {
            if (dateBreakDown.containsKey(it.purchaseDate)) {
                val previous = dateBreakDown.get(it.purchaseDate)
                dateBreakDown.replace(it.purchaseDate, previous!! + it.cost)
            } else {
                dateBreakDown.put(it.purchaseDate, it.cost)
            }
        }
        Log.d("Num Dates", dateBreakDown.size.toString())

        val xVals = ArrayList<String>()
        val yVals = ArrayList<BarEntry>()

        var entryCounter = 0f
        val results = dateBreakDown.toSortedMap(naturalOrder())
        results.forEach {
            xVals.add(it.key)
            yVals.add(BarEntry(entryCounter, it.value.toFloat()))
            entryCounter = entryCounter + 1f
        }

        val xAxis = line_chart.getXAxis()
        xAxis.setGranularity(1f)
        xAxis.setGranularityEnabled(true)
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 9f

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setValueFormatter(IndexAxisValueFormatter(xVals))

        val dataSet = BarDataSet(yVals, "Expense Breakdown")
        dataSet.valueTextSize = 0f
        val colors = java.util.ArrayList<Int>()
        colors.add(Color.MAGENTA)
        colors.add(Color.BLUE)
        colors.add(Color.RED)
        colors.add(Color.GREEN)
        colors.add(Color.GRAY)

        dataSet.setColors(colors)
        val data = BarData(dataSet)
        line_chart.data = data
        line_chart.description.isEnabled = false
        line_chart.invalidate()

    }
}

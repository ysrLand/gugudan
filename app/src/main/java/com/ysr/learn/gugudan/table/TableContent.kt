package com.ysr.learn.gugudan.table

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.ysr.learn.gugudan.R
import com.ysr.learn.gugudan.viewmodels.CentralViewModel
import java.util.ArrayList

class TableContent(
    private val context: Context,
    private val store: ViewModelStore,
    private val tableIndex: Int
) {

    val items: MutableList<TableItem> = ArrayList()
    private val itemCount = 9

    init {
        // Add some sample items.
        for (index in 1..itemCount) {
            addItem(
                createTableItem(
                    index,
                    null
                )
            )
        }
        addItem(createTableItem(itemCount + 1, View.OnClickListener {
            val viewModel = ViewModelProvider(store, ViewModelProvider.NewInstanceFactory()).get(
                CentralViewModel::class.java
            )
            viewModel.quizIndex(tableIndex)
        }))
    }

    private fun addItem(item: TableItem) {
        items.add(item)
    }

    private fun createTableItem(position: Int, listener: View.OnClickListener?): TableItem {
        return TableItem(
            if (position > itemCount) {
                context.getString(R.string.go_to_game)
            } else {
                "$tableIndex * $position = " + (tableIndex * position)
            },

            listener
        )
    }

    data class TableItem(val text: String, val listener: View.OnClickListener?) {
        override fun toString(): String = text
    }
}
package com.ysr.learn.gugudan.entry

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.ysr.learn.gugudan.R
import com.ysr.learn.gugudan.viewmodels.CentralViewModel
import java.util.ArrayList

class EntryContent(private val context: Context, private val store: ViewModelStore) {

    val items: MutableList<EntryItem> = ArrayList()
    private val itemCount = 9

    init {
        for (i in 2..itemCount) {
            addItem(createEntryItem(i))
        }
    }

    private fun addItem(item: EntryItem) {
        items.add(item)
    }

    private fun createEntryItem(position: Int): EntryItem {
        return EntryItem(
            context.getString(R.string.table, position.toString()),
            View.OnClickListener {
                val viewModel: CentralViewModel =
                    ViewModelProvider(store, ViewModelProvider.NewInstanceFactory()).get(
                        CentralViewModel::class.java
                    )
                viewModel.tableIndex(position)
            }
        )
    }

    data class EntryItem(
        val text: String,
        val listener: View.OnClickListener
    ) {
        override fun toString(): String = (text)
    }
}

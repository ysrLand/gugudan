package com.ysr.learn.gugudan.quiz

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.ysr.learn.gugudan.viewmodels.CentralViewModel
import java.util.ArrayList

class KeypadContent(private val context: Context, private val store: ViewModelStore) {

    val items: MutableList<QuizItem> = ArrayList()
    private val itemCount = 9

    init {
        for (i in 1..itemCount) {
            addItem(createEntryItem(i))
        }
        addItem(createEntryItem(0))
        addItem(createEntryItem(-1))
        addItem(createEntryItem(-2))
    }

    private fun addItem(item: QuizItem) {
        items.add(item)
    }

    private fun createEntryItem(digit: Int): QuizItem {
        val s = when (digit) {
            -2 -> {
                context.getString(android.R.string.ok)
            }
            -1 -> {
                "\u232b"
            }
            else -> {
                digit.toString()
            }
        }
        return QuizItem(
            s,
            View.OnClickListener {
                val viewModel: CentralViewModel =
                    ViewModelProvider(store, ViewModelProvider.NewInstanceFactory()).get(
                        CentralViewModel::class.java
                    )
                viewModel.inputDigit(digit)
            }
        )
    }

    data class QuizItem(
        val text: String,
        val listener: View.OnClickListener
    ) {
        override fun toString(): String = (text)
    }
}

package com.ysr.learn.gugudan.table

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import com.ysr.learn.gugudan.R
import kotlin.properties.Delegates

class TableFragment : Fragment() {

    private var tableIndex by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            tableIndex = it.getInt(TABLE_INDEX)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_table, container, false)
        val grid = view.findViewById<GridLayout>(R.id.grid)
        val tableContent = TableContent(
            requireContext(),
            requireActivity().viewModelStore,
            tableIndex
        )
        for (index in 0 until tableContent.items.size) {
            val textView = inflater.inflate(R.layout.item_table, grid, false)
                .findViewById<TextView>(R.id.content)
            val item = tableContent.items[index]
            textView.text = item.text
            textView.setOnClickListener(item.listener)
            val params = GridLayout.LayoutParams(
                GridLayout.spec(index, 1, 1.0f),
                GridLayout.spec(0, 1, 1.0f)
            )
            textView.layoutParams = params
            grid.addView(textView)
        }
        return view
    }

    companion object {

        const val TABLE_INDEX = "table-index"

        @JvmStatic
        fun newInstance(tableIndex: Int) =
            TableFragment().apply {
                arguments = Bundle().apply {
                    putInt(TABLE_INDEX, tableIndex)
                }
            }
    }
}
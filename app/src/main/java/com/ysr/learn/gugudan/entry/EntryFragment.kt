package com.ysr.learn.gugudan.entry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.ysr.learn.gugudan.R
import kotlin.properties.Delegates

private val TAG = EntryFragment::class.simpleName

class EntryFragment : Fragment() {

    private var columnCount by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_entry, container, false)
        val grid = view.findViewById<GridLayout>(R.id.grid)
        val entryContent = EntryContent(requireContext(), requireActivity().viewModelStore)
        for (index in 0 until entryContent.items.size) {
            val layout = View.inflate(context, R.layout.item_entry, null)
                .findViewById<ConstraintLayout>(R.id.layout)
            val textView = layout.findViewById<TextView>(R.id.content)
            val item = entryContent.items[index]
            textView.text = item.text
            textView.setOnClickListener(item.listener)
            val params = GridLayout.LayoutParams(
                GridLayout.spec(index / columnCount, 1, 1f),
                GridLayout.spec(index % columnCount, 1, 1f)
            )
            layout.layoutParams = params
            grid.addView(layout)
        }
        return view
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            EntryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
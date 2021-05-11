package com.ysr.learn.gugudan.quiz

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ysr.learn.gugudan.R
import com.ysr.learn.gugudan.viewmodels.CentralViewModel
import java.lang.StringBuilder
import kotlin.random.Random

private const val QUIZ_INDEX = "quiz_index"

class QuizFragment : Fragment() {
    private var quizIndex: Int = 0
    private var multiplicand: Int = 0
    private var multiplier: Int = 0
    private var result: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            quizIndex = it.getInt(QUIZ_INDEX)
            multiplicand = quizIndex
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val columnCount = 3
        val viewModelStore = requireActivity().viewModelStore
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)
        val equation = view.findViewById<TextView>(R.id.eval)
        val grid = view.findViewById<GridLayout>(R.id.keypad)
        val keypadContent = KeypadContent(requireContext(), requireActivity().viewModelStore)
        for (index in 0 until keypadContent.items.size) {
            val layout = View.inflate(context, R.layout.item_keypad, null)
                .findViewById<ConstraintLayout>(R.id.layout)
            val textView = layout.findViewById<TextView>(R.id.content)
            val item = keypadContent.items[index]
            textView.text = item.text
            textView.setOnClickListener(item.listener)
            val params = GridLayout.LayoutParams(
                GridLayout.spec(index / columnCount, 1, 1f),
                GridLayout.spec(index % columnCount, 1, 1f)
            )
            layout.layoutParams = params
            grid.addView(layout)
        }
        val viewModel =
            ViewModelProvider(viewModelStore, ViewModelProvider.NewInstanceFactory()).get(
                CentralViewModel::class.java
            )
        viewModel.digit.observe(viewLifecycleOwner, Observer {
            when (it) {
                -2 -> {
                    if (multiplicand * multiplier == result) {
                        showToast(getString(R.string.correct))
                        generateEquation()
                        putEquation(equation)
                    } else {
                        showToast(getString(R.string.wrong))
                        putEquation(equation)
                    }
                    clearResult()

                }
                -1 -> {
                    result = result / 10
                    putEquation(equation, result)
                }
                else -> {
                    result = result * 10 + it
                    putEquation(equation, result)
                }
            }
        })
        generateEquation()
        putEquation(equation)
        return view
    }

    private fun generateEquation() {
        val rand = Random(System.currentTimeMillis())
        multiplier = rand.nextInt(1, 10)
    }

    private fun putEquation(textView: TextView, r: Int = 0) {
        textView.text = StringBuilder()
            .append(multiplicand)
            .append(" X ")
            .append(multiplier)
            .append(" = ")
            .append(
                if (r > 0) {
                    r
                } else {
                    ""
                }
            )
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        //TODO: fix not toast but popup
        ((toast.view as ViewGroup).getChildAt(0) as TextView).setTextAppearance(R.style.DefaultTextAppearance)
        toast.show()
    }

    private fun clearResult() {
        result = 0
    }

    companion object {
        @JvmStatic
        fun newInstance(quizIndex: Int) =
            QuizFragment().apply {
                arguments = Bundle().apply {
                    putInt(QUIZ_INDEX, quizIndex)
                }
            }
    }
}

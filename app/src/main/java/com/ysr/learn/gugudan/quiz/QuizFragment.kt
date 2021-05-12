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

private val TAG = QuizFragment::class.simpleName
private const val QUIZ_TABLE = "quiz_table"
private const val questionSet = 9

class QuizFragment : Fragment() {
    private var table: Int = 0
    private var multiplicand: Int = 0
    private var multipliers: MutableList<Int> = mutableListOf()
    private var multiplier: Int = 0
    private var answer: Int = 0
    private var step: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            table = it.getInt(QUIZ_TABLE)
            multiplicand = table
            with(multipliers) {
                addAll((1..questionSet).toMutableList())
                addAll((1..questionSet).toMutableList().shuffled())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val columnCount = 3
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)
        val equationView = view.findViewById<TextView>(R.id.eval)
        val gridView = view.findViewById<GridLayout>(R.id.keypad)
        val progressView = view.findViewById<TextView>(R.id.progress)
        val viewModelStore = requireActivity().viewModelStore
        val keypadContent = KeypadContent(requireContext(), viewModelStore)
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
            gridView.addView(layout)
        }
        val viewModel =
            ViewModelProvider(viewModelStore, ViewModelProvider.NewInstanceFactory()).get(
                CentralViewModel::class.java
            )
        with(viewModel) {
            digit.observe(viewLifecycleOwner, Observer {
                when (it) {
                    -2 -> {
                        if (answer == multiplicand * multiplier) {
                            if (step == multipliers.size) {
                                showToast(getString(R.string.clear, this@QuizFragment.table))
                                removeThis()
                            } else {
                                showToast(getString(R.string.correct))
                                generateEquation()
                                updateProgress(progressView)
                                putEquation(equationView)
                            }
                        } else {
                            showToast(getString(R.string.wrong))
                            putEquation(equationView)
                        }
                        clearAnswer()
                    }
                    -1 -> {
                        answer /= 10
                        putEquation(equationView, answer)
                    }
                    else -> {
                        answer = answer * 10 + it
                        putEquation(equationView, answer)
                    }
                }
            })
        }
        generateEquation()
        updateProgress(progressView)
        putEquation(equationView)
        return view
    }

    private fun removeThis() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .remove(this)
            .commitAllowingStateLoss()
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun generateEquation() {
        multiplier = try {
            multipliers[step++]
        } catch (e:IndexOutOfBoundsException) {
            0
        }
    }

    private fun updateProgress(textView: TextView) {
        textView.text = getString(R.string.progress, step, multipliers.size)
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

    private fun clearAnswer() {
        answer = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val viewModel =
            ViewModelProvider(
                requireActivity().viewModelStore,
                ViewModelProvider.NewInstanceFactory()
            ).get(
                CentralViewModel::class.java
            )
        with(viewModel) {
            digit.removeObservers(viewLifecycleOwner)
            digit.value = 0
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(quizIndex: Int) =
            QuizFragment().apply {
                arguments = Bundle().apply {
                    putInt(QUIZ_TABLE, quizIndex)
                }
            }
    }
}

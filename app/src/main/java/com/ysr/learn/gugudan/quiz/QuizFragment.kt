package com.ysr.learn.gugudan.quiz

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.GridLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ysr.learn.gugudan.R
import com.ysr.learn.gugudan.viewmodels.CentralViewModel
import java.lang.StringBuilder

private val TAG = QuizFragment::class.simpleName
private const val QUIZ_TABLE = "quiz_table"
private const val questionSet = 9
private const val OK = -2
private const val BACK = -1

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
        val equationView = view.findViewById<TextView>(R.id.equation)
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
                    OK -> {
                        if (answer == multiplicand * multiplier) {
                            if (step == multipliers.size) {
                                AlertDialog.Builder(context)
                                    .setMessage(getString(R.string.clear, this@QuizFragment.table))
                                    .setPositiveButton(
                                        android.R.string.ok
                                    ) { _, _ -> removeThis() }
                                    .show()
                            } else {
                                val animation =
                                    AnimationUtils.loadAnimation(context, R.anim.disappear)
                                animation.setAnimationListener(object :
                                    Animation.AnimationListener {
                                    override fun onAnimationRepeat(p0: Animation?) {
                                    }

                                    override fun onAnimationEnd(p0: Animation?) {
                                        generateEquation()
                                        updateProgress(progressView)
                                        putEquation(equationView)
                                        equationView.startAnimation(
                                            AnimationUtils.loadAnimation(
                                                context,
                                                R.anim.appear
                                            )
                                        )
                                    }

                                    override fun onAnimationStart(p0: Animation?) {
                                    }
                                })
                                equationView.startAnimation(animation)
                            }
                        } else {
                            val animation = AnimationUtils.loadAnimation(context, R.anim.wrong)
                            animation.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationRepeat(p0: Animation?) {

                                }

                                override fun onAnimationEnd(p0: Animation?) {
                                    equationView.startAnimation(
                                        AnimationUtils.loadAnimation(
                                            context,
                                            R.anim.wrong
                                        )
                                    )
                                    putEquation(equationView)
                                }

                                override fun onAnimationStart(p0: Animation?) {

                                }
                            })
                            equationView.startAnimation(animation)
                        }
                        clearAnswer()
                    }
                    BACK -> {
                        answer /= 10
                        putEquation(equationView, answer)
                    }
                    else -> {
                        answer = (answer * 10 + it) % 100
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
        } catch (e: IndexOutOfBoundsException) {
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

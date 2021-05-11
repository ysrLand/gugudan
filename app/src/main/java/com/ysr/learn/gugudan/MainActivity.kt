package com.ysr.learn.gugudan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ysr.learn.gugudan.entry.EntryFragment
import com.ysr.learn.gugudan.quiz.QuizFragment
import com.ysr.learn.gugudan.table.TableFragment
import com.ysr.learn.gugudan.viewmodels.CentralViewModel

private var TAG = MainActivity::class.simpleName

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //TODO: use factory by reflection
        if (supportFragmentManager.findFragmentByTag(EntryFragment::class.simpleName) == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.root,
                    EntryFragment.newInstance(3),
                    EntryFragment::class.simpleName
                )
                .addToBackStack(EntryFragment::class.simpleName)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss()
        }
        val viewModel: CentralViewModel =
            ViewModelProvider(viewModelStore, ViewModelProvider.NewInstanceFactory()).get(
                CentralViewModel::class.java
            )
        viewModel.table.observe(this,
            Observer { idx ->
                if (supportFragmentManager.findFragmentByTag(TableFragment::class.simpleName) == null) {
                    supportFragmentManager.beginTransaction()
                        .add(
                            R.id.root,
                            TableFragment.newInstance(idx),
                            TableFragment::class.simpleName
                        )
                        .addToBackStack(TableFragment::class.simpleName)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commitAllowingStateLoss()
                }
            }
        )
        viewModel.quiz.observe(this,
            Observer { idx ->
                if (supportFragmentManager.findFragmentByTag(QuizFragment::class.simpleName) == null) {
                    supportFragmentManager.beginTransaction()
                        .add(
                            R.id.root,
                            QuizFragment.newInstance(idx),
                            QuizFragment::class.simpleName
                        )
                        .addToBackStack(QuizFragment::class.simpleName)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commitAllowingStateLoss()
                }
            })
    }
}
package dev.forcecodes.hov.ui.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.forcecodes.hov.binding.viewBinding
import dev.forcecodes.hov.databinding.ActivityDetailsBinding
import dev.forcecodes.hov.extensions.updateForTheme
import dev.forcecodes.hov.theme.ThemeViewModel
import kotlinx.coroutines.launch

/**
 * DetailsActivity for showing the detailed information a specific Github user.
 */
@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityDetailsBinding::inflate)
    private val viewModel by viewModels<DetailsViewModel>()
    private val subViewModel by viewModels<DetailsSubViewModel>()
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        updateForTheme(themeViewModel.currentTheme)
        
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        viewModel.getDetails(userExtras)
        subViewModel.sendEvent(LoadUiActions.LoadAll(userExtras.second))

        lifecycleScope.launch {
            viewModel.finishWhenError.collect { error ->
                if (error.isNullOrEmpty()) {
                    return@collect
                }
                Toast.makeText(applicationContext, error, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        // adjust scrim trigger offset based on the height of coordinator layout.
        binding.constraintLayout.doOnLayout {
            binding.coordinatorLayout.scrimVisibleHeightTrigger = it.height * 2
        }

        val statePagerAdapter = DetailsStatePagerAdapter(supportFragmentManager)

        binding.viewPager.adapter = statePagerAdapter
        binding.tablayout.setupWithViewPager(binding.viewPager, true)

        binding.viewPager.addDetailsChildListener(
            object: DetailsWrapContentViewPager.OnChildChangeListener {
            override fun onDetailsChildSelected(position: Int) {
                subViewModel.currentItem(position)
            }
        })

        binding.nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, _, scrollY, _, oldScrollY ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight)) &&
                    scrollY > oldScrollY
                ) {
                    // end of paging reload all
                    subViewModel.sendEvent(LoadUiActions.Refresh)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val userExtras: Pair<Int, String>
        get() = intent.getSerializableExtra(EXTRAS_NAME)
                as Pair<Int, String>

    companion object {

        private const val EXTRAS_NAME = "extras_name"

        fun createIntent(context: Context, pair: Pair<Int, String>) = Intent(
            context, DetailsActivity::class.java
        ).apply {
            putExtra(EXTRAS_NAME, pair)
        }
    }
}

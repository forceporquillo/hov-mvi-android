package dev.forcecodes.hov.ui.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
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
    private val themeViewModel by viewModels<ThemeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
       // WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        updateForTheme(themeViewModel.currentTheme)
        
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        viewModel.getDetails(userExtras)
        subViewModel.sendEvent(LoadUiActions.LoadAll(userExtras.second))

//        binding.appbar.doOnApplyWindowInsets { view, windowInsetsCompat, viewPaddingState ->
//            val paddingTop = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.statusBars())
//            binding.appbar.updatePadding(top = paddingTop.top + viewPaddingState.top)
//        }

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
            binding.coordinatorLayout.scrimVisibleHeightTrigger = it.height
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

        binding.nestedScrollView.onNextRefresh { refreshAction ->
            // end of paging reload all
            subViewModel.sendEvent(refreshAction)
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

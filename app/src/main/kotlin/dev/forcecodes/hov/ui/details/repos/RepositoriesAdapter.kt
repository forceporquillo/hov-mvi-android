package dev.forcecodes.hov.ui.details.repos

import android.graphics.Color
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.forcecodes.hov.adapter.AbstractPaginatedViewHolder
import dev.forcecodes.hov.adapter.PaginatedAdapter
import dev.forcecodes.hov.binding.viewBinding
import dev.forcecodes.hov.core.internal.Logger
import dev.forcecodes.hov.databinding.ItemReposLayoutBinding
import dev.forcecodes.hov.domain.usecase.details.UserRepoUiModel

class RepositoriesAdapter : ListAdapter<UserRepoUiModel,
        RepositoriesAdapter.RepositoriesViewHolder>(RepositoriesUiModelDiff()) {

    class RepositoriesViewHolder(
        binding: ItemReposLayoutBinding
    ) : AbstractPaginatedViewHolder<ItemReposLayoutBinding, UserRepoUiModel>(binding) {

        override fun bind(data: UserRepoUiModel) {
            binding.apply {
                repositoryTitle.text = data.name
                stargazer.text = data.starred
                tagStateDescription.text = data.description
                language.text = data.language
                imageColorIcon.isVisible = !data.language.isNullOrEmpty()
                try {
                    imageColorIcon.setColorFilter(Color.parseColor(data.color))
                } catch (e: Exception) { }
            }
        }
    }

    private class RepositoriesUiModelDiff : DiffUtil.ItemCallback<UserRepoUiModel>() {
        override fun areItemsTheSame(oldItem: UserRepoUiModel, newItem: UserRepoUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: UserRepoUiModel,
            newItem: UserRepoUiModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoriesViewHolder {
        return RepositoriesViewHolder(parent.viewBinding(ItemReposLayoutBinding::inflate))
    }

    override fun onBindViewHolder(holder: RepositoriesViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }
//    override fun viewHolder(parent: ViewGroup): RepositoriesViewHolder {
//
//    }
}


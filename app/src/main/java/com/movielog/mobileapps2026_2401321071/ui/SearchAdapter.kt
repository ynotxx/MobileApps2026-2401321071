package com.movielog.mobileapps2026_2401321071.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.movielog.mobileapps2026_2401321071.R
import com.movielog.mobileapps2026_2401321071.data.network.OmdbSearchResult
import com.movielog.mobileapps2026_2401321071.databinding.ItemSearchMovieBinding

class SearchAdapter(
    private val onAddWatchlistClick: (OmdbSearchResult) -> Unit,
    private val onAddCollectionClick: (OmdbSearchResult) -> Unit
) : ListAdapter<OmdbSearchResult, SearchAdapter.SearchViewHolder>(SearchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val result = getItem(position)
        holder.bind(result)
    }

    inner class SearchViewHolder(
        private val binding: ItemSearchMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(result: OmdbSearchResult) {
            binding.tvTitle.text = result.title
            binding.tvYear.text = result.year

            if (result.posterUrl.isNotEmpty() && result.posterUrl != "N/A") {
                binding.ivPoster.load(result.posterUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_collection)
                    error(R.drawable.ic_collection)
                }
            } else {
                binding.ivPoster.setImageResource(R.drawable.ic_collection)
            }

            binding.btnWatchlist.setOnClickListener {
                onAddWatchlistClick(result)
            }

            binding.btnCollection.setOnClickListener {
                onAddCollectionClick(result)
            }
        }
    }

    private class SearchDiffCallback : DiffUtil.ItemCallback<OmdbSearchResult>() {
        override fun areItemsTheSame(oldItem: OmdbSearchResult, newItem: OmdbSearchResult): Boolean {
            return oldItem.imdbId == newItem.imdbId
        }

        override fun areContentsTheSame(oldItem: OmdbSearchResult, newItem: OmdbSearchResult): Boolean {
            return oldItem == newItem
        }
    }
}

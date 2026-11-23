package com.movielog.mobileapps2026_2401321071.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.movielog.mobileapps2026_2401321071.databinding.ItemHistoryEntryBinding
import com.movielog.mobileapps2026_2401321071.databinding.ItemHistoryYearBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : ListAdapter<HistoryListItem, RecyclerView.ViewHolder>(HistoryDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HistoryListItem.YearHeader -> TYPE_YEAR
            is HistoryListItem.ViewingEntry -> TYPE_ENTRY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_YEAR) {
            val binding = ItemHistoryYearBinding.inflate(inflater, parent, false)
            YearViewHolder(binding)
        } else {
            val binding = ItemHistoryEntryBinding.inflate(inflater, parent, false)
            EntryViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HistoryListItem.YearHeader -> (holder as YearViewHolder).bind(item)
            is HistoryListItem.ViewingEntry -> (holder as EntryViewHolder).bind(item)
        }
    }

    class YearViewHolder(
        private val binding: ItemHistoryYearBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryListItem.YearHeader) {
            binding.tvYearHeader.text = item.year.toString()
        }
    }

    class EntryViewHolder(
        private val binding: ItemHistoryEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

        fun bind(item: HistoryListItem.ViewingEntry) {
            val dateStr = dateFormat.format(Date(item.history.watchedAt)).uppercase(Locale.getDefault())
            binding.tvDate.text = dateStr
            binding.tvMovieTitle.text = item.movieTitle

            val rating = item.history.rating
            if (rating != null) {
                binding.tvRating.visibility = View.VISIBLE
                binding.tvRating.text = "Rating: $rating / 10"
            } else {
                binding.tvRating.visibility = View.GONE
            }
        }
    }

    private class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryListItem>() {
        override fun areItemsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem): Boolean {
            return when {
                oldItem is HistoryListItem.YearHeader && newItem is HistoryListItem.YearHeader ->
                    oldItem.year == newItem.year
                oldItem is HistoryListItem.ViewingEntry && newItem is HistoryListItem.ViewingEntry ->
                    oldItem.history.historyId == newItem.history.historyId
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val TYPE_YEAR = 0
        private const val TYPE_ENTRY = 1
    }
}

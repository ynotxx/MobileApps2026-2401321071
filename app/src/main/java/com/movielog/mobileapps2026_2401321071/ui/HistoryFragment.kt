package com.movielog.mobileapps2026_2401321071.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.movielog.mobileapps2026_2401321071.data.AppDatabase
import com.movielog.mobileapps2026_2401321071.data.MovieRepository
import com.movielog.mobileapps2026_2401321071.databinding.FragmentHistoryBinding
import com.movielog.mobileapps2026_2401321071.viewmodel.MovieViewModel
import com.movielog.mobileapps2026_2401321071.viewmodel.MovieViewModelFactory
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext().applicationContext)
        val repository = MovieRepository(db.movieDao(), db.movieHistoryDao())
        MovieViewModelFactory(repository)
    }

    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyAdapter = HistoryAdapter()
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(viewModel.viewingHistory, viewModel.allMovies) { historyList, moviesList ->
                    val movieMap = moviesList.associateBy { it.localId }
                    val calendar = Calendar.getInstance()

                    val groupedByYear = historyList
                        .sortedByDescending { it.watchedAt }
                        .groupBy { history ->
                            calendar.timeInMillis = history.watchedAt
                            calendar.get(Calendar.YEAR)
                        }

                    val listItems = mutableListOf<HistoryListItem>()
                    groupedByYear.keys.sortedDescending().forEach { year ->
                        listItems.add(HistoryListItem.YearHeader(year))
                        val entriesInYear = groupedByYear[year] ?: emptyList()
                        entriesInYear.forEach { history ->
                            val title = movieMap[history.movieId]?.title ?: "Unknown Movie"
                            listItems.add(HistoryListItem.ViewingEntry(history, title))
                        }
                    }
                    listItems.toList()
                }.collect { items ->
                    if (items.isEmpty()) {
                        binding.rvHistory.visibility = View.GONE
                        binding.tvEmptyState.visibility = View.VISIBLE
                    } else {
                        binding.rvHistory.visibility = View.VISIBLE
                        binding.tvEmptyState.visibility = View.GONE
                        historyAdapter.submitList(items)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

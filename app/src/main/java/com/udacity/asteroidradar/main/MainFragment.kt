package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = DataBindingUtil.inflate<FragmentMainBinding>(inflater, R.layout.fragment_main, container, false)
        binding.lifecycleOwner = this

        val viewModelFactory = MainViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        binding.viewModel = viewModel

        // Setup Recycler View
        val adapter = AsteroidAdapter(AsteroidAdapter.AsteroidClickListener { asteroid ->
            viewModel.onSleepNightClicked(asteroid)
        })
        binding.asteroidRecycler.adapter = adapter

        viewModel.navigateToAsteroidDetail.observe(viewLifecycleOwner, Observer { asteroid ->
            asteroid?.let {
                // clear the event
                viewModel.onSleepDataQualityNavigated()

                // navigate to detail screen
                this.findNavController().navigate(
                        MainFragmentDirections.actionShowDetail(it))
            }
        })

        binding.swiperefresh.setOnRefreshListener(this);

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.show_week_asteroids_menu -> viewModel.daysIncluded.value = MainViewModel.PeriodDays.SEVEN
            R.id.show_today_asteroids_menu -> viewModel.daysIncluded.value = MainViewModel.PeriodDays.ONE
        }

        return true
    }

    override fun onRefresh() {
        viewModel.refreshData()
    }
}

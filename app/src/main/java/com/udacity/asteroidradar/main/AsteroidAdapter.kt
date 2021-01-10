package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.RowItemBinding


class AsteroidAdapter(val clickListener: AsteroidClickListener): ListAdapter<Asteroid, AsteroidAdapter.AsteroidAdapterViewHolder>(DiffCallback){
    companion object DiffCallback: DiffUtil.ItemCallback<Asteroid>() {
        // Same item if the items share the same id
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid)    = (oldItem.id == newItem.id)

        // Same contents if dataclasses' equality is the same
        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid) = (oldItem == newItem)
    }

    class AsteroidClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidAdapter.AsteroidAdapterViewHolder {
        return AsteroidAdapterViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AsteroidAdapter.AsteroidAdapterViewHolder, position: Int) {
        val asteroid = getItem(position)
        holder.bind(clickListener, asteroid)
    }

    class AsteroidAdapterViewHolder private constructor (private val binding: RowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: AsteroidClickListener, asteroid: Asteroid){
            binding.asteroid = asteroid
            binding.clickListener = clickListener

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): AsteroidAdapterViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowItemBinding.inflate(layoutInflater, parent, false)
                return AsteroidAdapterViewHolder(binding)
            }
        }
    }
}

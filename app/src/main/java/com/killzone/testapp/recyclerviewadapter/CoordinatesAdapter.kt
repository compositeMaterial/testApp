package com.killzone.testapp.recyclerviewadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.killzone.testapp.databinding.RecyclerViewItemBinding
import com.killzone.testapp.network.Point

class CoordinatesAdapter : ListAdapter<Point, CoordinatesAdapter.ViewHolder>(CoordinatesDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class ViewHolder private constructor(
        val binding: RecyclerViewItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Point, position: Int) {

            if (position == 0) {
                binding.countValue.text = ""
                binding.xValue.text = "x"
                binding.yValue.text = "y"
            } else {
                binding.countValue.text = (position).toString()
                binding.xValue.text = item.x.toString().substring(0, 6)
                binding.yValue.text = item.y.toString().substring(0,6)
            }

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerViewItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class CoordinatesDiffUtil : DiffUtil.ItemCallback<Point>() {
    override fun areItemsTheSame(oldItem: Point, newItem: Point): Boolean {
        return oldItem.x == newItem.x
    }

    override fun areContentsTheSame(oldItem: Point, newItem: Point): Boolean {
        return oldItem == newItem
    }
}

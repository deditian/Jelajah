package com.tian.jelajah.ui.menu

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tian.jelajah.databinding.ItemMenuBinding
import com.tian.jelajah.model.Menus

class MainMenuAdapter : RecyclerView.Adapter<MainMenuAdapter.ViewHolder>() {

    private var list = ArrayList<Menus>()

    fun submitList(list: List<Menus>) {
        this.list.clear()
        this.list.addAll(list)
    }

    var listener: RecyclerViewClickListener? = null

    inner class  ViewHolder(val binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return  ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            val data = list[position]
            binding.data = data
            binding.crItemMenu.setOnClickListener { listener?.onItemClicked(it, data) }

        }
    }

    override fun getItemCount(): Int = list.size

    interface RecyclerViewClickListener {
        fun onItemClicked(view: View, item: Menus)
    }
}
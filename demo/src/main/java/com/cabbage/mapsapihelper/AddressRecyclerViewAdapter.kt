package com.cabbage.mapsapihelper

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cabbage.mylibrary.geocoding.AddressEntity

class AddressRecyclerViewAdapter(
        private val mListener: ResultListFragment.FragmentInteraction?)
    : RecyclerView.Adapter<AddressRecyclerViewAdapter.ViewHolder>() {

    var items: List<AddressEntity> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvAddress.text = item.formattedAddress
        holder.tvTypes.text = item.types?.reduce { acc, s -> "$acc, $s" }
        mListener?.let { listener ->
            holder.itemView.setOnClickListener { listener.onListFragmentInteraction(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_address, parent, false)
        return ViewHolder(view)
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        val tvTypes: TextView = view.findViewById(R.id.tvTypes)
    }
}
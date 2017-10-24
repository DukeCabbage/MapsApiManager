package com.cabbage.mapsapihelper

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.cabbage.mylibrary.manager.MapsApiManager
import com.cabbage.mylibrary.place.PredictionsItem
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_auto_complete.*
import timber.log.Timber

class AutoCompleteFragment : Fragment() {

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.v("onCreateView")
        return inflater?.inflate(R.layout.fragment_auto_complete, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvAutoComplete.layoutManager = LinearLayoutManager(context)
        rvAutoComplete.adapter = mAdapter

        btnRequest.setOnClickListener {
            val input = etAutoComplete.text.toString()
            if (input.isNotBlank()) autoComplete(input)
        }
    }

    private fun autoComplete(input: String) = MapsApiManager.placeService.autoComplete(input)
            .map { it.results ?: emptyList() }
            .compose { applyIoSchedulers(it) }
            .subscribeBy(
                    onError = { Timber.e(it); Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show() },
                    onNext = { mAdapter.items = it }
            )

    private fun itemOnClick(item: PredictionsItem) {
        Toast.makeText(context, "${item.description} on click", Toast.LENGTH_SHORT).show()
    }

    private val mAdapter = object : RecyclerView.Adapter<ViewHolder>() {

        var items: List<PredictionsItem> = emptyList()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_address, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvAddress.text = item.description
            holder.tvTypes.text = item.types?.reduce { acc, s -> "$acc, $s" }
            holder.itemView.setOnClickListener { itemOnClick(item) }
        }

        override fun getItemCount(): Int = items.size
    }

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        val tvTypes: TextView = view.findViewById(R.id.tvTypes)
    }
}
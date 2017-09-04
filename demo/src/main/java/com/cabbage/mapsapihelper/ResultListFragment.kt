package com.cabbage.mapsapihelper

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cabbage.mylibrary.geocoding.model.AddressEntity
import kotlinx.android.synthetic.main.fragment_result_list.*
import timber.log.Timber

class ResultListFragment : Fragment() {

    private var mInteraction: FragmentInteraction? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mInteraction = context as? FragmentInteraction
                ?: throw RuntimeException("$context must implement FragmentInteraction")

        mInteraction?.fragAttached(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Timber.v("onCreateView")
        val view = inflater?.inflate(R.layout.fragment_result_list, container, false)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the adapter
        rvAddresses.layoutManager = LinearLayoutManager(rvAddresses.context)
        rvAddresses.adapter = AddressRecyclerViewAdapter(mInteraction)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        Timber.d("onSaveInstanceState")
        super.onSaveInstanceState(outState)
    }

    override fun onDetach() {
        super.onDetach()
        Timber.v("onDetach")
        mInteraction = null
    }

    fun showData(response: List<AddressEntity>) {
        Timber.d("showData size: ${response.size}")
        (rvAddresses.adapter as AddressRecyclerViewAdapter).items = response
    }

    interface FragmentInteraction {
        fun fragAttached(frag: ResultListFragment)

        fun onListFragmentInteraction(item: AddressEntity)
    }
}

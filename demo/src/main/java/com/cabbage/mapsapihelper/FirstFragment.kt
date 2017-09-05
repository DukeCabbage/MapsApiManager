package com.cabbage.mapsapihelper

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.cabbage.mylibrary.manager.AuthMethod
import com.cabbage.mylibrary.manager.MapsApiManager
import com.cabbage.mylibrary.manager.ReqBounds
import kotlinx.android.synthetic.main.fragment_first.*
import timber.log.Timber

class FirstFragment : Fragment() {

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
        return inflater?.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preference = context.getSharedPreferences("default", Context.MODE_PRIVATE)
        preference.getString("CACHE_LAT", "49.190658").let { etLatitude.setText(it) }
        preference.getString("CACHE_LNG", "-123.149168").let { etLongitude.setText(it) }

        btnRequest.setOnClickListener {
            val lat = etLatitude.text.toString().toDoubleOrNull()
            val lng = etLongitude.text.toString().toDoubleOrNull()
            if (lat == null || lng == null) {
                Toast.makeText(context, "Invalid", Toast.LENGTH_SHORT).show()
            } else {
                preference.edit().putString("CACHE_LAT", lat.toString())
                        .putString("CACHE_LNG", lng.toString())
                        .apply()

                mInteraction?.locationLookup(lat, lng,
                        if (etLanguage.text.toString().isBlank()) null else etLanguage.text.toString(),
                        if (etResultType.text.toString().isBlank()) null else etResultType.text.toString(),
                        if (etLocationType.text.toString().isBlank()) null else etLocationType.text.toString())
            }
        }

        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(context, R.array.spinner_content,
                android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAuthMethod.adapter = adapter
        spinnerAuthMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                when (pos) {
                    0 -> MapsApiManager.geocodingService.configure(AuthMethod.None())
                    1 -> MapsApiManager.geocodingService.configure(AuthMethod.ApiKey("AIzaSyDz1nfM_Y8cUNJNbo-0kECHGmbqyaTDrpI"))
                    2 -> MapsApiManager.geocodingService.configure(AuthMethod.ClientId("gme-digitaldispatch", "cu-hCsv4xZUpySVcGrMJGHunzZw=", null))
                }
            }

        }
    }

    override fun onDetach() {
        super.onDetach()
        Timber.v("onDetach")
        mInteraction = null
    }

    interface FragmentInteraction {
        fun fragAttached(frag: FirstFragment)

        fun locationLookup(lat: Double, lng: Double,
                           language: String?,
                           resultType: String?,
                           locationType: String?)

        fun addressLookup(address: String,
                          language: String?,
                          bounds: ReqBounds?)
    }
}

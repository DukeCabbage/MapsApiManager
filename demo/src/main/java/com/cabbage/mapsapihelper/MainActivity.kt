package com.cabbage.mapsapihelper

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.cabbage.mapsapihelper.detail.DetailActivity
import com.cabbage.mylibrary.geocoding.model.AddressEntity
import com.cabbage.mylibrary.geocoding.model.DefaultResultToAddressParser
import com.cabbage.mylibrary.manager.MapsApiManager
import com.cabbage.mylibrary.manager.ReqBounds
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity(),
                     ResultListFragment.FragmentInteraction,
                     FirstFragment.FragmentInteraction {

    private var firstFragment: FirstFragment? = null
    private var resultListFragment: ResultListFragment? = null

    //region Life cycle callbacks
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.v("onCreate")

        setContentView(R.layout.activity_main)

        viewPager.adapter = MainPagerAdapter(supportFragmentManager)

        viewPager.addOnPageChangeListener(pageChangeListener)
        bottomNavigationView.setOnNavigationItemSelectedListener(navOnSelectListener)
        bottomNavigationView.setOnNavigationItemReselectedListener(navOnReselectListener)
    }

    //endregion

    private fun onDataReady(data: List<AddressEntity>) {
        resultListFragment?.showData(data)
        viewPager.currentItem = PAGE_INDEX_RESULT_LIST
    }

    //region Fragment callbacks
    override fun fragAttached(frag: ResultListFragment) {
        resultListFragment = frag
    }

    override fun onListFragmentInteraction(item: AddressEntity) {
        Toast.makeText(this, item.formattedAddress, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("placeId", item.placeId)
        startActivity(intent)
    }

    override fun fragAttached(frag: FirstFragment) {
        firstFragment = frag
    }

    override fun locationLookup(lat: Double, lng: Double,
                                language: String?,
                                resultType: String?,
                                locationType: String?) {
        Observable.fromArray(1, 2, 3)
        MapsApiManager.geocodingService.queryByLocation(lat, lng, language, resultType, locationType)
                .flatMap { it.results!!.toObservable() }
                .map { DefaultResultToAddressParser().apply(it) }
                .compose { applyIoSchedulers(it) }
                .toList()   // Concat the results to a single list
                .subscribeBy(
                        onError = { Timber.e(it); Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show() },
                        onSuccess = { onDataReady(it) }
                )
    }

    override fun addressLookup(address: String, language: String?, bounds: ReqBounds?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    //endregion

    private val pageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            Timber.v("onPageSelected: $position")
            bottomNavigationView.selectedItemId = index2NavId[position] ?: throw UnsupportedOperationException()
        }
    }
    //
    private val navOnSelectListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        Timber.v("onNavigationItemSelected: ${item.title}")
        viewPager.setCurrentItem(navId2Index[item.itemId] ?: PAGE_INDEX_GEO_CODING, true)
        return@OnNavigationItemSelectedListener true
    }

    private val navOnReselectListener = BottomNavigationView.OnNavigationItemReselectedListener { item ->
        when (item.itemId) {
            R.id.navigation_geo_code -> Timber.v("GeoCoding reselected")
            R.id.navigation_geo_code_results -> Timber.v("Result list reselected")
        }
    }

    companion object {
        val PAGE_INDEX_GEO_CODING = 0
        val PAGE_INDEX_RESULT_LIST = 1

        val navId2Index = mapOf(R.id.navigation_geo_code to PAGE_INDEX_GEO_CODING,
                                R.id.navigation_geo_code_results to PAGE_INDEX_RESULT_LIST)

        val index2NavId = mapOf(PAGE_INDEX_GEO_CODING to R.id.navigation_geo_code,
                                PAGE_INDEX_RESULT_LIST to R.id.navigation_geo_code_results)
    }
}

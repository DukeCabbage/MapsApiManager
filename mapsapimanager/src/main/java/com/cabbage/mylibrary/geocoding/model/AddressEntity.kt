package com.cabbage.mylibrary.geocoding.model

import java.io.Serializable

class AddressEntity {

    var houseNumber: String? = null
    var streetNameShort: String? = null
    var streetName: String? = null
    var district: String? = null
    var city: String? = null
    var postalCode: String? = null

    var state: String? = null
    var stateCode: String? = null
    var country: String? = null
    var countryCode: String? = null

    // This is for other info that might be of some interest
    var extra: MutableMap<String, String> = mutableMapOf()

    var formattedAddress: String? = null
    var viewport: Bounds? = null
    var location: Location? = null
    var placeId: String? = null
    var types: List<String>? = null
}
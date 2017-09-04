package com.cabbage.mylibrary.geocoding.model

import com.cabbage.mylibrary.GenericItem
import com.cabbage.mylibrary.GenericRes
import com.google.gson.annotations.SerializedName
import io.reactivex.functions.Function

data class GeocodingResponse(
        override val results: List<ResultsItem>? = null,
        override val status: String? = null,
        @SerializedName("error_message")
        override val errorMessage: String? = null
): GenericRes<ResultsItem>

data class ResultsItem(
        @SerializedName("formatted_address")
        val formattedAddress: String? = null,
        val types: List<String>? = null,
        val geometry: Geometry? = null,
        @SerializedName("address_components")
        val addressComponents: List<AddressComponentsItem>? = null,
        @SerializedName("place_id")
        val placeId: String? = null
): GenericItem

data class AddressComponentsItem(
        val types: List<String>? = null,
        @SerializedName("short_name")
        val shortName: String? = null,
        @SerializedName("long_name")
        val longName: String? = null
)

data class Geometry(
        val viewport: Bounds? = null,
        val bounds: Bounds? = null,
        val location: Location? = null,
        @SerializedName("location_type")
        val locationType: String? = null
)

data class Bounds(
        val southwest: Location? = null,
        val northeast: Location? = null
)

data class Location(
        val lng: Double? = null,
        val lat: Double? = null
)

abstract class GeoCodingParser<R> : Function<ResultsItem, R>

abstract class ResultToAddressParser : GeoCodingParser<AddressEntity>()

class DefaultResultToAddressParser : ResultToAddressParser() {
    override fun apply(result: ResultsItem): AddressEntity {
        if (result.addressComponents == null) throw InvalidAddressException("No address components")

        val address = AddressEntity()

        for ((types, shortName, longName) in result.addressComponents) {
            if (types == null || types.isEmpty()) continue
            val safeName = longName ?: shortName ?: throw RuntimeException("No value")

            // House number
            if (types.contains("street_number")) {
                address.houseNumber = safeName
                continue
            }

            // Street name
            if (types.contains("route")) {
                address.streetNameShort = shortName
                address.streetName = longName
                continue
            }

            // City
            if (types.contains("neighborhood") && address.district == null) {
                // Set but do not override as district
                address.district = safeName
                continue
            }

            if (types.contains("sublocality")) {
                // Set or override as district
                address.district = safeName
                continue
            }

            if (types.contains("locality")) {
                // Use locality as city, if city is not already set
                // Otherwise, set as district if it is not already set
                if (address.city == null) {
                    address.city = safeName
                } else if (address.district == null) {
                    address.district = safeName
                }
                continue
            }

            if (types.contains("postal_town")) {
                // Set and override as city
                // If city is already set, override district with old city value
                if (address.city != null) {
                    address.district = address.city
                }
                address.city = safeName
                continue
            }

            // State
            if (types.contains("administrative_area_level_1")) {
                address.state = longName
                address.stateCode = shortName
                continue
            } else if (address.state == null && types.contains("administrative_area_level_2")) {
                // Backup as state
                address.state = longName
                address.stateCode = shortName
                continue
            }

            if (types.contains("postal_code")) {
                address.postalCode = safeName
                continue
            }

            if (types.contains("country")) {
                address.countryCode = shortName
                address.country = longName
                continue
            }

            // Extra
            if (types.contains("premise")) {
                address.extra.put("premise", safeName)
                continue
            }

            if (types.contains("park")) {
                address.extra.put("park", safeName)
                continue
            }

            if (types.contains("airport")) {
                address.extra.put("airport", safeName)
                continue
            }

            if (types.contains("establishment")) {
                address.extra.put("establishment", safeName)
                continue
            }

            if (types.contains("ward")) {
                address.extra.put("ward", safeName)
                continue
            }
        }

        address.viewport = result.geometry?.viewport
        address.location = result.geometry?.location
        address.placeId = result.placeId
        address.types = result.types

        address.formattedAddress = result.formattedAddress
        // Todo: back up for formatted Address

        return address
    }
}

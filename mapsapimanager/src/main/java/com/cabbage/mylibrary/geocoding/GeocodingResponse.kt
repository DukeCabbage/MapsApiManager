package com.cabbage.mylibrary.geocoding

import com.cabbage.mylibrary.common.*
import com.google.gson.annotations.SerializedName
import io.reactivex.functions.Function

data class GeocodingResponse(
        override val results: List<GeocodingItem>? = null,
        override val status: String? = null,
        @SerializedName("error_message")
        override val errorMessage: String? = null
) : GenericRes<GeocodingItem>

data class GeocodingItem(
        @SerializedName("formatted_address")
        val formattedAddress: String? = null,
        val types: List<String>? = null,
        val geometry: Geometry? = null,
        @SerializedName("address_components")
        val addressComponents: List<AddressComponent>? = null,
        @SerializedName("place_id")
        val placeId: String? = null
) : GenericItem

abstract class GeoCodingParser<R> : Function<GeocodingItem, R>

abstract class ResultToAddressParser : GeoCodingParser<AddressEntity>()

class DefaultResultToAddressParser : ResultToAddressParser() {
    override fun apply(result: GeocodingItem): AddressEntity {
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

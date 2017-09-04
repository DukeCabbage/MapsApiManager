package com.cabbage.mylibrary.geocoding.model

class InvalidAddressException : RuntimeException {

    constructor() : super()
    constructor(message: String) : super(message)
}
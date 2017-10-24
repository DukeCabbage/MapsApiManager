@file:Suppress("unused")

package com.cabbage.mylibrary.common

class InvalidAddressException : RuntimeException {

    constructor() : super()
    constructor(message: String) : super(message)
}
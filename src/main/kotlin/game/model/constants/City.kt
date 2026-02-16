package dev.gangster.game.model.constants

import dev.gangster.game.model.protobuf.common.PBCity
import dev.gangster.game.model.protobuf.common.PBCityConstants

/**
 * none = -1, NewYork = 0
 */
enum class City {
    NewYork, Miami, Hongkong, Sicily
}

fun PBCity.toCity(): City {
    return when (this) {
        PBCityConstants.NEW_YORK -> City.NewYork
        PBCityConstants.MIAMI -> City.Miami
        PBCityConstants.HONG_KONG -> City.Hongkong
        PBCityConstants.CITY_EVENT -> City.Sicily
        else -> City.NewYork
    }
}

package hu.androidworkshop.places.model

import com.google.gson.annotations.SerializedName

data class CoordinatesModel(@SerializedName("coords") val coordinates: List<Coordinate>)
package com.lmu.setupmanager.data.static

import com.lmu.setupmanager.domain.model.Car
import com.lmu.setupmanager.domain.model.ParameterOverride

val lmgt3Cars: List<Car> = listOf(
    Car(
        id = "bmw-m4-gt3",
        name = "BMW M4 GT3",
        manufacturer = "BMW",
        carClass = "LMGT3",
        parameterOverrides = mapOf(
            "frontWing" to ParameterOverride(min = 0f, max = 12f),
            "rearWing" to ParameterOverride(min = 2f, max = 14f)
        )
    ),
    Car(
        id = "mclaren-720s-gt3-evo",
        name = "McLaren 720S GT3 Evo",
        manufacturer = "McLaren",
        carClass = "LMGT3",
        parameterOverrides = mapOf(
            "frontWing" to ParameterOverride(min = 1f, max = 14f),
            "diffPreload" to ParameterOverride(min = 10f, max = 120f)
        )
    ),
    Car(
        id = "ferrari-296-gt3",
        name = "Ferrari 296 GT3",
        manufacturer = "Ferrari",
        carClass = "LMGT3",
        parameterOverrides = mapOf(
            "rideHeightFront" to ParameterOverride(min = 55f, max = 110f),
            "rideHeightRear" to ParameterOverride(min = 60f, max = 120f)
        )
    ),
    Car(
        id = "porsche-911-gt3-r",
        name = "Porsche 911 GT3 R",
        manufacturer = "Porsche",
        carClass = "LMGT3",
        parameterOverrides = mapOf(
            "caster" to ParameterOverride(min = 4.0f, max = 11.0f, defaultValue = 8.0f),
            "rearWing" to ParameterOverride(min = 1f, max = 13f)
        )
    ),
    Car(
        id = "ford-mustang-gt3",
        name = "Ford Mustang GT3",
        manufacturer = "Ford",
        carClass = "LMGT3",
        parameterOverrides = mapOf(
            "antiRollBarFront" to ParameterOverride(defaultValue = 6f),
            "antiRollBarRear" to ParameterOverride(defaultValue = 5f)
        )
    ),
    Car(
        id = "mercedes-amg-gt3-evo",
        name = "Mercedes-AMG GT3 Evo",
        manufacturer = "Mercedes-AMG",
        carClass = "LMGT3",
        parameterOverrides = mapOf(
            "rideHeightFront" to ParameterOverride(min = 52f, max = 115f, defaultValue = 62f),
            "rideHeightRear" to ParameterOverride(min = 58f, max = 125f, defaultValue = 72f),
            "diffPreload" to ParameterOverride(min = 10f, max = 130f, defaultValue = 55f),
            "caster" to ParameterOverride(min = 3.5f, max = 11.5f, defaultValue = 7.0f)
        )
    )
)

val carById: Map<String, Car> = lmgt3Cars.associateBy { it.id }

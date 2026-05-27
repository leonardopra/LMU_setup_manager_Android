package com.lmu.setupmanager.domain.usecase

import com.lmu.setupmanager.data.static.allParameters
import com.lmu.setupmanager.data.static.carById
import javax.inject.Inject

private val CORNERS = listOf("FL", "FR", "RL", "RR")

class BuildDefaultValuesUseCase @Inject constructor() {

    /**
     * Generates a Map<String, Float> for all parameters.
     * - Non-corner-specific params: key → defaultValue
     * - Corner-specific params: key_FL / key_FR / key_RL / key_RR → defaultValue
     * Car overrides are applied before expanding corners.
     */
    operator fun invoke(carId: String): Map<String, Float> {
        val car = carById[carId]
        return buildMap {
            for (param in allParameters) {
                val override = car?.parameterOverrides?.get(param.key)
                val defaultVal = override?.defaultValue ?: param.defaultValue

                if (param.cornerSpecific) {
                    for (corner in CORNERS) {
                        put("${param.key}_$corner", defaultVal)
                    }
                } else {
                    put(param.key, defaultVal)
                }
            }
        }
    }
}

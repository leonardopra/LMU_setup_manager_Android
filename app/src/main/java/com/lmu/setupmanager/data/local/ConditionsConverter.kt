package com.lmu.setupmanager.data.local

import androidx.room.TypeConverter
import com.lmu.setupmanager.domain.model.Conditions

/**
 * Room TypeConverter for [Conditions].
 *
 * Storage format: the enum's [Conditions.name] string ("DRY" | "WET" | "MIXED").
 * This matches the values already on disk, so no migration is required.
 * Making the conversion explicit here means any future rename of the enum constants
 * surfaces as a compile-time or single-file change rather than a silent runtime failure.
 */
class ConditionsConverter {

    @TypeConverter
    fun fromConditions(conditions: Conditions): String = conditions.name

    @TypeConverter
    fun toConditions(value: String): Conditions = Conditions.valueOf(value)
}

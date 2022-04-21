package com.github.squirrelgrip.plugin.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.github.squirrelgrip.extension.time.toOffsetDateTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField

data class Versioning(
    @JsonProperty("latest")
    val latest: String? = null,
    @JsonProperty("release")
    val release: String? = null,
    @JsonProperty("versions")
    @JacksonXmlElementWrapper(useWrapping = true)
    val versions: List<String> = emptyList(),
    @JsonProperty("lastUpdated")
    val lastUpdated: String? = null,
) {
    companion object {
        val dateTimeFormatter = DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR, 4, 4, SignStyle.EXCEEDS_PAD)
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .toFormatter()
    }

    fun updateTime(): Versioning =
        copy(lastUpdated = getCurrentTimeStamp())

    private fun getCurrentTimeStamp(): String =
        dateTimeFormatter.format(Instant.now().toOffsetDateTime(ZoneOffset.UTC))

    @get:JsonIgnore
    val updatedDateTime: Instant
        get() {
            return try {
                dateTimeFormatter.parse(lastUpdated, LocalDateTime::from).toInstant(ZoneOffset.UTC)
            } catch (e: Exception) {
                Instant.MIN
            }
        }
}

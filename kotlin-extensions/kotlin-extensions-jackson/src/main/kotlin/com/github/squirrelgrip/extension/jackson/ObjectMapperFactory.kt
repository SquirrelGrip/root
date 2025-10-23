package com.github.squirrelgrip.extension.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.cfg.MapperBuilder
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.github.squirrelgrip.format.ThrowableMixIn

interface ObjectMapperFactory<M : ObjectMapper, B : MapperBuilder<M, B>> {
    fun createObjectMapper(): M =
        builder()
            .addModule(JavaTimeModule())
            .addModule(
                kotlinModule {
                    enable(KotlinFeature.StrictNullChecks)
                }
            ).addModule(Jdk8Module())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .addMixIn(Throwable::class.java, ThrowableMixIn::class.java)
            .build()

    fun builder(): B
}

package com.github.squirrelgrip.format

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties("stackTrace")
internal class ThrowableMixIn
    @JsonCreator
    constructor(
        @JsonProperty("message") message: String?
    ) : Throwable(message)


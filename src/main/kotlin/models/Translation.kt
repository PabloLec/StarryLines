package models

import com.fasterxml.jackson.annotation.JsonProperty

data class Translation(
    @JsonProperty("detected_source_language")
    val detectedSourceLanguage: String,
    val text: String
)

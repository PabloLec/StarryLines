package translation

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import db.MongoManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import models.DeeplResponse
import models.Language
import models.TopRepository
import mu.KotlinLogging
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class TranslationManager(private val mongoManager: MongoManager, val languages: Set<Language>) {
    private val apiClient = OkHttpClient()
    private val deeplUrl = "https://api-free.deepl.com/v2/translate"
    private val deeplToken = System.getenv("DEEPL_TOKEN")!!
    private val logger = KotlinLogging.logger {}

    suspend fun run() = coroutineScope {
        languages.forEach { language ->
            mongoManager.getTop(language)
                .forEach { repo ->
                    val isLanguageToBeDetermined = repo.descriptionLanguage.isNullOrEmpty() ||
                        (repo.descriptionLanguage != "EN" && repo.translatedDescription.isNullOrEmpty())

                    if (repo.description.isNotEmpty() && isLanguageToBeDetermined) {
                        getTranslation(repo.description)?.let {
                            launch { processTranslationResponse(repo, language, it) }
                        }
                    }
                }
        }
    }

    private suspend fun processTranslationResponse(repo: TopRepository, language: Language, response: DeeplResponse) = coroutineScope {
        var descriptionLanguage = response.translations.first().detectedSourceLanguage
        var translatedDescription: String? = response.translations.first().text
        if (descriptionLanguage != "EN" && translatedDescription?.trim() == repo.description.trim()) {
            descriptionLanguage = "EN"
        }
        if (descriptionLanguage == "EN") {
            translatedDescription = null
        }
        mongoManager.updateTranslation(
            repo,
            language,
            descriptionLanguage,
            translatedDescription
        )
        logger.info { "Updated ${repo.url} with language $descriptionLanguage: $translatedDescription" }
    }

    private fun getTranslation(payload: String): DeeplResponse? {
        val body = FormBody.Builder()
            .add("text", payload)
            .add("target_lang", "EN")
            .build()

        val request = Request.Builder()
            .method("POST", body)
            .addHeader("Authorization", "DeepL-Auth-Key $deeplToken")
            .url(deeplUrl)
            .build()

        apiClient.newCall(request).execute().let {
            if (it.isSuccessful) {
                return jacksonObjectMapper().readValue(it.body!!.string(), DeeplResponse::class.java)
            } else {
                logger.error { "Translation failed" }
                return null
            }
        }
    }
}

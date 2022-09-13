package db

import com.mongodb.client.MongoDatabase
import kotlinx.coroutines.coroutineScope
import models.Repository
import mu.KotlinLogging
import org.litote.kmongo.*

object MongoClient {
    private val user = System.getenv("MONGO_USER")!!
    private val secret = System.getenv("MONGO_SECRET")!!
    private val connectionString =
        "mongodb+srv://$user:$secret@cluster-mongodb.yfa5utj.mongodb.net/?retryWrites=true&w=majority"

    private val client = KMongo.createClient(connectionString)
    private val database: MongoDatabase = client.getDatabase("StarryLines")
    private val logger = KotlinLogging.logger {}

    fun close() {
        client.close()
    }

    suspend fun upsertFromGHApi(repository: Repository, language: String) {
        coroutineScope {
            val col = database.getCollection<Repository>(language)
            client.startSession().use { session ->
                session.startTransaction()
                col.findOne(Repository::url eq repository.url)?.let {
                    repository.loc = it.loc
                }
                col.updateOne(
                    Repository::url eq repository.url,
                    set(
                        SetTo(Repository::name, repository.name),
                        SetTo(Repository::description, repository.description),
                        SetTo(Repository::createdAt, repository.createdAt),
                        SetTo(Repository::stargazers, repository.stargazers),
                        SetTo(Repository::defaultBranch, repository.defaultBranch),
                        SetTo(Repository::githubUpdateDate, repository.githubUpdateDate),
                        SetTo(Repository::mStarsPerLine, repository.getMilliStarsPerLine())
                    ),
                    upsert()
                )
                session.commitTransaction()
                logger.info("Upserted $repository to $language")
            }
        }
    }

    suspend fun updateFromLoc(repository: Repository, language: String) {
        coroutineScope {
            val col = database.getCollection<Repository>(language)
            client.startSession().use { session ->
                session.startTransaction()
                col.updateOne(
                    Repository::url eq repository.url,
                    set(
                        SetTo(Repository::loc, repository.loc),
                        SetTo(Repository::locUpdateDate, repository.locUpdateDate),
                        SetTo(Repository::mStarsPerLine, repository.getMilliStarsPerLine())
                    ),
                    upsert()
                )
                session.commitTransaction()
                logger.info("Updated $repository to $language")
            }
        }
    }

    fun getAllRepositoriesByLanguage(language: String): List<Repository> =
        database.getCollection<Repository>(language).find().toList()
}

package db

import com.mongodb.client.MongoDatabase
import kotlinx.coroutines.coroutineScope
import models.BlacklistUrl
import models.Language
import models.Repository
import models.TopRepository
import mu.KotlinLogging
import org.litote.kmongo.*

object MongoClient {
    private val user = System.getenv("MONGO_USER")!!
    private val secret = System.getenv("MONGO_SECRET")!!
    private val cluster = System.getenv("MONGO_CLUSTER")!!
    private val databaseName = System.getenv("MONGO_DATABASE")!!
    private val connectionString =
        "mongodb+srv://$user:$secret@$cluster/?retryWrites=true&w=majority"

    private val client = KMongo.createClient(connectionString)
    private val database: MongoDatabase = client.getDatabase(databaseName)
    private val logger = KotlinLogging.logger {}

    fun close() {
        client.close()
    }

    fun getAllCollections(): List<String> {
        return database.listCollectionNames().toList()
    }

    suspend fun insertOne(repo: Repository, collectionName: String) = coroutineScope {
        database.getCollection<Repository>(collectionName)
            .insertOne(repo)
        logger.debug { "Inserted $repo into $collectionName" }
    }

    suspend fun insertOneToBlacklist(url: String, reason: String?) = coroutineScope {
        database.getCollection<BlacklistUrl>("blacklist")
            .insertOne(BlacklistUrl(url, reason))
        logger.debug { "Inserted $url into blacklist" }
    }

    suspend fun insertMany(repos: List<TopRepository>, collectionName: String) = coroutineScope {
        database.getCollection<TopRepository>(collectionName)
            .insertMany(repos)
        logger.debug { "Inserted ${repos.size} repos into $collectionName" }
    }

    suspend fun deleteCollection(collectionName: String) = coroutineScope {
        database.getCollection<TopRepository>(collectionName).drop()
        logger.debug { "Deleted collection $collectionName" }
    }

    suspend fun deleteByUrl(url: String, collectionName: String) = coroutineScope {
        database.getCollection<Repository>(collectionName)
            .deleteOne(Repository::url eq url)
        logger.debug { "Deleted $url from $collectionName" }
    }

    suspend fun deleteFromTopByUrl(url: String, collectionName: String) = coroutineScope {
        database.getCollection<TopRepository>(collectionName)
            .deleteOne(TopRepository::url eq url)
        logger.debug { "Deleted $url from $collectionName" }
    }


    suspend fun deleteManyByUrl(urls: List<String>, collectionName: String) = coroutineScope {
        val result = database.getCollection<Repository>(collectionName)
            .deleteMany(Repository::url `in` urls.toSet())
        logger.debug { "Removed $result from $collectionName" }
    }

    suspend fun upsertFromGHApi(repository: Repository, language: Language) =
        coroutineScope {
            val col = database.getCollection<Repository>(language.toString())
            client.startSession().use { session ->
                session.startTransaction()
                col.findOne(Repository::url eq repository.url)?.let {
                    repository.loc = it.loc
                    repository.parsedLength = it.parsedLength
                }
                col.updateOne(
                    Repository::url eq repository.url,
                    set(
                        SetTo(Repository::ghid, repository.ghid),
                        SetTo(Repository::name, repository.name),
                        SetTo(Repository::description, repository.description),
                        SetTo(Repository::createdAt, repository.createdAt),
                        SetTo(Repository::updatedAt, repository.updatedAt),
                        SetTo(Repository::stargazers, repository.stargazers),
                        SetTo(Repository::defaultBranch, repository.defaultBranch),
                        SetTo(Repository::githubUpdateDate, repository.githubUpdateDate),
                        SetTo(Repository::milliStarsPerLine, repository.computeMilliStarsPerLine()),
                        SetTo(Repository::diskUsage, repository.diskUsage),
                        SetTo(Repository::languagePercent, repository.languagePercent)
                    ),
                    upsert()
                )
                session.commitTransaction()
                logger.debug("Upserted $repository to $language")
            }
        }

    suspend fun updateFromLoc(repository: Repository, language: Language) {
        coroutineScope {
            val col = database.getCollection<Repository>(language.toString())
            client.startSession().use { session ->
                session.startTransaction()
                col.updateOne(
                    Repository::url eq repository.url,
                    set(
                        SetTo(Repository::loc, repository.loc),
                        SetTo(Repository::parsedLength, repository.parsedLength),
                        SetTo(Repository::locUpdateDate, repository.locUpdateDate),
                        SetTo(Repository::milliStarsPerLine, repository.computeMilliStarsPerLine())
                    ),
                    upsert()
                )
                session.commitTransaction()
                logger.debug("Updated $repository to $language")
            }
        }
    }

    suspend fun updateTranslation(
        repository: TopRepository,
        language: Language,
        descriptionLanguage: String,
        translatedDescription: String?
    ) =
        coroutineScope {
            val col = database.getCollection<Repository>(language.toString())
            client.startSession().use { session ->
                session.startTransaction()
                col.updateOne(
                    Repository::url eq repository.url,
                    set(
                        SetTo(Repository::descriptionLanguage, descriptionLanguage),
                        SetTo(Repository::translatedDescription, translatedDescription)
                    )
                )
                session.commitTransaction()
            }
            val topCol = database.getCollection<TopRepository>(language.toString().lowercase() + "_top")
            client.startSession().use { session ->
                session.startTransaction()
                topCol.updateOne(
                    TopRepository::url eq repository.url,
                    set(
                        SetTo(TopRepository::descriptionLanguage, descriptionLanguage),
                        SetTo(TopRepository::translatedDescription, translatedDescription)
                    )
                )
                session.commitTransaction()
            }
            logger.debug("Updated translation for $repository")
        }

    fun getCollection(collectionName: String): List<Repository> =
        database.getCollection<Repository>(collectionName).find().toList()

    fun getTopCollection(collectionName: String): List<TopRepository> =
        database.getCollection<TopRepository>(collectionName).find().toList()

    fun getBlacklistCollection(): List<String> =
        database.getCollection<BlacklistUrl>("blacklist").find().toList().map { it.url }
}

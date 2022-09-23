package db

import com.mongodb.client.MongoDatabase
import kotlinx.coroutines.coroutineScope
import models.BlacklistUrl
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
        val col = database.getCollection<Repository>(collectionName)
        col.insertOne(repo)
        logger.debug { "Inserted $repo into $collectionName" }
    }

    suspend fun insertOneToBlacklist(url: String, reason: String?) = coroutineScope {
        val col = database.getCollection<BlacklistUrl>("blacklist")
        col.insertOne(BlacklistUrl(url, reason))
        logger.debug { "Inserted $url into blacklist" }
    }

    suspend fun insertMany(repos: List<TopRepository>, collectionName: String) = coroutineScope {
        val col = database.getCollection<TopRepository>(collectionName)
        col.insertMany(repos)
        logger.debug { "Inserted ${repos.size} repos into $collectionName" }
    }

    suspend fun deleteCollection(collectionName: String) = coroutineScope {
        database.getCollection<TopRepository>(collectionName).drop()
        logger.debug { "Deleted collection $collectionName" }
    }

    suspend fun deleteManyByUrl(urls: List<String>, collectionName: String) = coroutineScope {
        val col = database.getCollection<Repository>(collectionName)
        val result = col.deleteMany(Repository::url `in` urls.toSet())
        logger.debug { "Removed $result from $collectionName" }
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
                        SetTo(Repository::ghid, repository.ghid),
                        SetTo(Repository::name, repository.name),
                        SetTo(Repository::description, repository.description),
                        SetTo(Repository::createdAt, repository.createdAt),
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
                        SetTo(Repository::milliStarsPerLine, repository.computeMilliStarsPerLine())
                    ),
                    upsert()
                )
                session.commitTransaction()
                logger.debug("Updated $repository to $language")
            }
        }
    }

    fun getCollection(collectionName: String): List<Repository> =
        database.getCollection<Repository>(collectionName).find().toList()

    fun getTopCollection(collectionName: String): List<TopRepository> =
        database.getCollection<TopRepository>(collectionName).find().toList()

    fun getBlacklistCollection(): List<String> =
        database.getCollection<BlacklistUrl>("blacklist").find().toList().map { it.url }
}

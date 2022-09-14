package db

import com.mongodb.client.MongoDatabase
import kotlinx.coroutines.coroutineScope
import models.Repository
import mu.KotlinLogging
import org.litote.kmongo.*

object MongoClient {
    private val user = System.getenv("MONGO_USER")!!
    private val secret = System.getenv("MONGO_SECRET")!!
    private val cluster = System.getenv("MONGO_CLUSTER")!!
    private val connectionString =
        "mongodb+srv://$user:$secret@$cluster/?retryWrites=true&w=majority"

    private val client = KMongo.createClient(connectionString)
    private val database: MongoDatabase = client.getDatabase("StarryLines")
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
        logger.info { "Inserted $repo into $collectionName" }
    }

    suspend fun deleteMany(repos: List<Repository>, collectionName: String) = coroutineScope {
        val col = database.getCollection<Repository>(collectionName)
        val result = col.deleteMany(Repository::url `in` repos.map { it.url }.toSet())
        logger.info { "Removed $result from $collectionName" }
    }

    fun isInCollection(repo: Repository, collectionName: String): Boolean =
        database.getCollection<Repository>(collectionName)
            .findOne(Repository::url eq repo.url) != null

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
                        SetTo(Repository::mStarsPerLine, repository.getMilliStarsPerLine()),
                        SetTo(Repository::languagePercent, repository.languagePercent)
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

    fun getCollection(collectionName: String): List<Repository> =
        database.getCollection<Repository>(collectionName).find().toList()
}

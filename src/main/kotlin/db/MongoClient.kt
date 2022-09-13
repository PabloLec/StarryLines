package db

import com.mongodb.client.MongoDatabase
import models.Repository
import org.litote.kmongo.*

object MongoClient {
    private val user = System.getenv("MONGO_USER")!!
    private val secret = System.getenv("MONGO_SECRET")!!
    private val connectionString =
        "mongodb+srv://$user:$secret@cluster-mongodb.yfa5utj.mongodb.net/?retryWrites=true&w=majority"

    private val client = KMongo.createClient(connectionString)
    private val database: MongoDatabase = client.getDatabase("StarryLines")

    fun close() {
        client.close()
    }

    fun upsertFromGHApi(repository: Repository, language: String) {
        val col = database.getCollection<Repository>(language)
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
            ),
            upsert()
        )
    }

    fun updateFromLoc(repository: Repository, language: String) {
        val col = database.getCollection<Repository>(language)
        col.updateOne(
            Repository::url eq repository.url,
            set(
                SetTo(Repository::loc, repository.loc),
                SetTo(Repository::locUpdateDate, repository.locUpdateDate),
                SetTo(Repository::mStarsPerLine, repository.getMilliStarsPerLine()),
            )
        )
    }

    fun getAllRepositoriesByLanguage(language: String): List<Repository> =
        database.getCollection<Repository>(language).find().toList()
}

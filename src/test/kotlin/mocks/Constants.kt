package test.mocks

import models.Repository
import models.Language
import models.TopRepository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

val blacklistRepoPrevious1 = Repository(
    "blacklistRepoPrevious1",
    "blacklistRepoPrevious1",
    "test",
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    0,
    "blacklistRepoPrevious1",
    "main",
    100,
    0,
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    null,
    null,
    null
)

val blacklistRepoPrevious2 = Repository(
    "blacklistRepoPrevious2",
    "blacklistRepoPrevious2",
    "test",
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    0,
    "blacklistRepoPrevious2",
    "main",
    100,
    0,
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    null,
    null,
    null
)

val blacklistRepo = Repository(
    "mockId0",
    "blacklistRepo",
    "test",
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    0,
    "blacklistRepo",
    "main",
    100,
    0,
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    null,
    null,
    null
)

val javaRepo = Repository(
    "mockId1",
    "javaRepo",
    "test",
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    10000,
    "javaRepo",
    "main",
    100,
    0,
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    null,
    null,
    null
)

val pythonRepo = Repository(
    "mockId2",
    "pythonRepo",
    "test",
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    20000,
    "pythonRepo",
    "main",
    90,
    0,
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    null,
    null,
    null
)

val topRepo1 = TopRepository(
    "topJavaRepo1",
    "test",
    LocalDateTime.now().toLocalDate(),
    10000,
    "topJavaRepo1",
    100,
    100,
    100
)

val topRepo2 = TopRepository(
    "topPythonRepo1",
    "test",
    LocalDateTime.now().toLocalDate(),
    20000,
    "topPythonRepo1",
    200,
    200,
    200
)

val topRepo3 = TopRepository(
    "topJavaRepo2",
    "test",
    LocalDateTime.now().toLocalDate(),
    30000,
    "topJavaRepo1",
    300,
    300,
    300
)

val topRepo4 = TopRepository(
    "topPythonRepo2",
    "test",
    LocalDateTime.now().toLocalDate(),
    40000,
    "topPythonRepo2",
    400,
    400,
    400
)

val fetchResult =
    mapOf(Language.JAVA to setOf(javaRepo), Language.PYTHON to setOf(pythonRepo, blacklistRepo))

val locCRepo = Repository(
    "mockId3",
    "CRepo",
    "test",
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    20000,
    "https://github.com/PabloLec/sl_test_c",
    "v2",
    90,
    0,
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    null,
    null,
    null
)

val locPythonRepo = Repository(
    "mockId4",
    "pythonRepo",
    "test",
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    20000,
    "https://github.com/PabloLec/sl_test_python",
    "master",
    90,
    0,
    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    null,
    null,
    null
)

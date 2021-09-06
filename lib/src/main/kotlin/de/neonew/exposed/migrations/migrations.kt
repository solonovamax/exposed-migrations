package de.neonew.exposed.migrations

import java.time.Clock
import java.time.Instant.now
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.reflections.Reflections
import org.slf4j.kotlin.debug
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.info

private val logger by getLogger()

internal lateinit var migrationsDatabase: Database

fun runMigrations(
    migrations: List<Migration>,
    database: Database = TransactionManager.defaultDatabase!!,
    clock: Clock = Clock.systemUTC()
                 ) {
    migrationsDatabase = database
    
    checkVersions(migrations)
    
    logger.info { "Running migrations on database ${database.url}" }
    
    val latestVersion = transaction(database) {
        createTableIfNotExists(database)
        MigrationEntity.all().maxByOrNull { it.version }?.version?.value ?: -1
    }
    
    logger.info { "Database version before migrations: $latestVersion" }
    
    for (migration in migrations.sortedBy { it.version }) {
        if (!shouldRun(latestVersion, migration))
            continue
        
        logger.info { "Running migration version ${migration.version}: ${migration.name}" }
        transaction(database) {
            migration.run()
            
            MigrationEntity.new {
                version = EntityID(migration.version, MigrationsTable)
                name = migration.name
                executedAt = now(clock)
            }
        }
    }
    
    logger.info { "Migrations finished successfully" }
}

private fun getTopLevelClasses(packageName: String): Set<Class<*>> {
    val reflections = Reflections(packageName)
    
    return reflections.getSubTypesOf(Migration::class.java)
}

private fun checkVersions(migrations: List<Migration>) {
    val sorted = migrations.map { it.version }.sorted()
    if ((1 .. migrations.size).toList() != sorted) {
        throw IllegalStateException("List of migrations version is not consecutive: $sorted")
    }
}

private fun createTableIfNotExists(database: Database) {
    if (MigrationsTable.exists()) {
        return
    }
    
    val tableNames = database.dialect.allTablesNames()
    
    when (tableNames.isEmpty()) {
        true  -> {
            logger.info { "Empty database found, creating table for migrations" }
            SchemaUtils.create(MigrationsTable)
        }
        
        false -> throw IllegalStateException("Tried to run migrations against a non-empty database without a Migrations table. This is not supported.")
    }
}

private fun shouldRun(latestVersion: Int, migration: Migration): Boolean {
    val run = latestVersion.let { migration.version > it }
    if (!run) {
        logger.debug { "Skipping migration version ${migration.version}: ${migration.name}" }
    }
    return run
}

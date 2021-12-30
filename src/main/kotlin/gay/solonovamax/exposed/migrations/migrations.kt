/*
 * exposed-migrations - A scuffed utility library for jetbrains Exposed to support migrations
 * Copyright (c) 2021 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file migrations.kt is part of exposed-migrations
 * Last modified on 06-09-2021 02:34 p.m.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * EXPOSED-MIGRATIONS IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package gay.solonovamax.exposed.migrations

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.reflections.Reflections
import org.slf4j.kotlin.*
import org.slf4j.kotlin.toplevel.*
import java.time.Clock
import java.time.Instant.now

private val logger by getLogger()

/**
 * Performs the migrations on the provided list of migrations
 *
 * @param migrations
 * @param database
 * @param clock
 */
fun runMigrations(
    migrations: List<Migration>,
    database: Database = TransactionManager.defaultDatabase!!,
    clock: Clock = Clock.systemUTC()
                 ) {
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
            migration(this)
            
            MigrationEntity.new {
                version = EntityID(migration.version, MigrationsTable)
                name = migration.name
                executedAt = now(clock)
            }
        }
    }
    
    logger.info { "Migrations finished successfully" }
}

fun loadMigrationsFrom(packageName: String): List<Migration> {
    return getTopLevelClasses(packageName).map {
        logger.debug("Instantiating migration class ${it.name}")
        
        val instance = it.getDeclaredConstructor().newInstance()
        
        if (instance is Migration)
            instance
        else
            throw IllegalArgumentException("There should only be Migrations in this list")
    }.sortedBy { it.version }
}

private fun getTopLevelClasses(packageName: String): Set<Class<*>> {
    val reflections = Reflections(packageName)
    
    return reflections.getSubTypesOf(Migration::class.java)
}

private fun checkVersions(migrations: List<Migration>) {
    val sorted = migrations.map { it.version }.sorted()
    if ((1..migrations.size).toList() != sorted) {
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
        
        false -> throw IllegalStateException(
                "Tried to run migrations against a non-empty database without a Migrations table. This is not supported.")
    }
}

private fun shouldRun(latestVersion: Int, migration: Migration): Boolean {
    val run = latestVersion.let { migration.version > it }
    if (!run) {
        logger.debug { "Skipping migration version ${migration.version}: ${migration.name}" }
    }
    return run
}

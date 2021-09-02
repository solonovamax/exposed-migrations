package de.neonew.exposed.migrations

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp

object MigrationsTable : IntIdTable() {
    val name = varchar("name", length = 400)
    val executedAt = timestamp("executed_at")
}

class MigrationEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MigrationEntity>(MigrationsTable)
    
    var version by MigrationsTable.id
    var name by MigrationsTable.name
    var executedAt by MigrationsTable.executedAt
}

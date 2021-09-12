# exposed-migrations

[![MIT license](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/gay.solonovamax/exposed-migrations.svg?style=for-the-badge&label=Maven%20Central)](https://search.maven.org/search?q=g:gay.solonovamax%20a:exposed-migrations)
[![Pure Kotlin](https://img.shields.io/badge/100%25-kotlin-blue.svg?style=for-the-badge)](https://kotlinlang.org/)
[![Discord Server](https://img.shields.io/discord/871114669761372221?color=7389D8&label=Discord&logo=discord&logoColor=8fa3ff&style=for-the-badge)](https://discord.solonovamax.gay)

A scuffed utility library for jetbrains Exposed to support migrations

This library is a super-basic migration tool for
the [Kotlin SQL Framework Exposed](https://github.com/JetBrains/Exposed) by
Jetbrains.

See [this issue](https://github.com/JetBrains/Exposed/issues/165) for more
information.

Currently, only migrations to a higher version are possible, downgrades are not
supported.

## Including

You can include Exposed Migrations in your project by adding the following:

### Maven

```xml
<dependency>
  <groupId>gay.solonovamax</groupId>
  <artifactId>exposed-migrations</artifactId>
  <version>4.0.0</version>
</dependency>
```

### Gradle Groovy DSL

```groovy
implementation 'gay.solonovamax:exposed-migrations:4.0.0'
```

### Gradle Kotlin DSL

```kotlin
implementation("gay.solonovamax:exposed-migrations:4.0.0")
```

## Sample usage

Put all your migrations in the same package, for
example `com.your.program.migration`.

Create a class named `MXXXX` like bellow, `XXXX` being the number of this
migration class.

*Note: you can append anything to `MXXXX` for extra context,
i.e. `M0001_FirstMigration`*

```kotlin
package com.your.program.migration

class M0001 : Migration() {
  /** a static snapshot of [SomeTable] */
  private class SomeTable : IntIdTable() {
    init {
      integer("someField")

      index(true, someField)
    }
  }

  override fun run() {
    SchemaUtils.create(SomeTestTable)
  }
}
```

and

```kotlin
val migrations = loadMigrationsFrom("com.your.program.migration")
runMigrations(migrations)
```

or list your migrations manually

```kotlin
runMigrations(listOf(M0001()))
```

The line above will find all classes named according to the
regex `^M(\\d+)_(.*)$` and apply them in order of the number after `M`.

## Implementation details

A table named `MIGRATIONS` is used to store all executed migrations. It is used
to find the current state of the database and to determine which migrations
still need to be executed.

## Credit

This is a fork
of [Suwayomi/exposed-migrations](https://github.com/Suwayomi/exposed-migrations)
which is in turn a fork of the
original [exposed-migrations](https://gitlab.com/andreas-mausch/exposed-migrations)
by [Andreas Mausch](https://gitlab.com/andreas-mausch).

## License

This software is licensed under `MIT`.

```text
MIT License

Copyright (c) 2021 solonovamax

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

package gay.solonovamax.exposed.migrations.helpers

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

import gay.solonovamax.exposed.migrations.migrationsDatabase

/**
 * Using plain name without said conversion may cause issues in different Locales or naming styles.
 * ref: https://garygregory.wordpress.com/2015/11/03/java-lowercase-conversion-turkey/
 */
internal fun String.toSqlName(): String =
    migrationsDatabase.identifierManager.let {
        it.quoteIfNecessary(
            it.inProperCase(this)
        )
    }

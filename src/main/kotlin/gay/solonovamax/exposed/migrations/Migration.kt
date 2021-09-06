/*
 * exposed-migrations - A scuffed utility library for jetbrains Exposed to support migrations
 * Copyright (c) 2021 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file Migration.kt is part of exposed-migrations
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

abstract class Migration {
    val name: String
    val version: Int

    init {
        val groups = Regex("^M(\\d+)_?(.*)$").matchEntire(this::class.simpleName!!)?.groupValues
            ?: throw IllegalArgumentException("Migration class name doesn't match convention")
        version = groups[1].toInt()
        name = groups[2]
    }

    abstract fun run()
}

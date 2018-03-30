/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.gradle.plugin.model

import org.jetbrains.kotlin.konan.KonanVersion
import java.io.Serializable

/**
 * An immutable representation of Konan's project model for gradle tooling API.
 * This model is shared with the client processes such as an IDE.
 */
interface KonanModel : Serializable {
    val artifacts: List<KonanArtifact>
}

interface KonanArtifact : Serializable {
    val name: String
    val path: String
}

/*
 * springboot-multimodule-kotlin skeleton.
 * Under no licences and warranty.
 */
package com.github.fj.restapi.persistence.consts.account

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.github.fj.lib.annotation.UndefinableEnum

/**
 * @author Francesco Jo(nimbusob@gmail.com)
 * @since 27 - Oct - 2018
 */
@UndefinableEnum
enum class LoginType(val key: String) {
    /** Basic authentication: user provided name and password */
    BASIC("b"),
    /** Guest authentication: only the last known access token is used for authorisation */
    GUEST("g"),
    UNDEFINED("");

    @JsonValue
    @Suppress("unused") // Used by Jackson upon serialising @JsonSerialize annotated classes
    fun toValue(): String {
        return key
    }

    companion object {
        @JsonCreator
        @JvmStatic
        fun byKey(key: String?) = LoginType.values().firstOrNull { it.key == key } ?: UNDEFINED
    }
}

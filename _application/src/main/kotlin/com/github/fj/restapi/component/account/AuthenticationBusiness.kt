/*
 * springboot-multimodule-kotlin skeleton.
 * Under no licences and warranty.
 */
package com.github.fj.restapi.component.account

import com.github.fj.restapi.dto.account.AccessToken
import com.github.fj.restapi.persistence.entity.MyAuthentication
import com.github.fj.restapi.persistence.entity.User

/**
 * @author Francesco Jo(nimbusob@gmail.com)
 * @since 30 - Oct - 2018
 */
interface AuthenticationBusiness {
    fun hash(data: ByteArray): ByteArray

    fun createAccessToken(user: User): MyAuthentication

    fun parseAccessToken(base62EncodedAccessToken: String): AccessToken
}
/*
 * springboot-multimodule-kotlin skeleton.
 * Under no licences and warranty.
 */
package com.github.fj.restapi.endpoint

/**
 * @author Francesco Jo(nimbusob@gmail.com)
 * @since 22 - Oct - 2018
 */
object ApiPaths {
    const val HELLO = "/hello"

    const val V1 = "v1"
    const val API_V1 = "/api/$V1"
    const val API_V1_ACCOUNT = "$API_V1/account"
}

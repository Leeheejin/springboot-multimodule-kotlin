/*
 * FCMClient
 *
 * Copyright 2018 Francesco Jo
 * Use of this software is subject to licence terms.
 */
package com.github.fj.fcmclient.httpv1.dto

import com.github.fj.fcmclient.PushMessage
import com.google.gson.Gson
import com.github.fj.fcmclient.httpv1.dto.config.Config
import com.github.fj.fcmclient.httpv1.dto.target.Target

/**
 * @author Francesco Jo(nimbusob@gmail.com)
 * @since 5 - Feb - 2018
 * @see <a href="https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages#resource-message">Firebase HTTP v1 API - Message</a>
 */
class HttpV1Message: PushMessage {
    /**
     * Arbitrary key/value payload.
     *
     * An object containing a list of `"key": value` pairs. Example:
     * ```
     * { "name": "wrench", "mass": "1.3kg", "count": "3" }
     * ```
     */
    val data: MutableMap<String, String> = HashMap()

    /**
     * Input only. An object containing information of recipient's platform
     * and notification format.
     */
    var config = Config.EMPTY

    /**
     * Union field `target`. Required. Input only.
     * Target to send a message to. `target` can be only one of the following:
     *
     * - `token`: Registration token to send a message to.
     * - `topic`: Topic name to send a message to, e.g. `weather`.
     *            Note: "/topics/" prefix should not be provided.
     * - `condition`: Condition to send a message to, e.g. `'foo' in topics && 'bar' in topics`.
     */
    var target = Target.EMPTY

    override fun toJsonString(): String = Gson().toJson(HashMap<String, Any>().apply {
        put("message", HashMap<String, Any>().apply {
            data.takeIf { it.isNotEmpty() }?.let { put("data", it) }
            config.takeIf { it.valueMap.isNotEmpty() }?.let { put(it.name, it.valueMap) }
            target.takeIf { it != Target.EMPTY }?.let { it.valuePair.run { put(first, second) } }
        })
    })
}

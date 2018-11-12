/*
 * springboot-multimodule-kotlin skeleton.
 * Under no licences and warranty.
 */
package com.github.fj.restapi.service.account

import com.github.fj.lib.annotation.AllOpen
import com.github.fj.lib.text.SemanticVersion
import com.github.fj.lib.text.getRandomCapitalAlphaNumericString
import com.github.fj.lib.text.isNullOrUnicodeBlank
import com.github.fj.lib.time.utcNow
import com.github.fj.restapi.component.account.AuthenticationBusiness
import com.github.fj.restapi.endpoint.v1.account.dto.AuthenticationResponseDto
import com.github.fj.restapi.endpoint.v1.account.dto.CreateAccountRequestDto
import com.github.fj.restapi.exception.account.AccountAlreadyExistException
import com.github.fj.restapi.persistence.consts.account.Gender
import com.github.fj.restapi.persistence.consts.account.LoginType
import com.github.fj.restapi.persistence.consts.account.Role
import com.github.fj.restapi.persistence.consts.account.Status
import com.github.fj.restapi.persistence.entity.Membership
import com.github.fj.restapi.persistence.entity.User
import com.github.fj.restapi.persistence.repository.UserRepository
import com.github.fj.restapi.util.extractInetAddress
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Service
import java.util.*
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest

/**
 * @author Francesco Jo(nimbusob@gmail.com)
 * @since 27 - Oct - 2018
 */
@AllOpen
@Service
class CreateAccountServiceImpl @Inject constructor(
        private val userRepo: UserRepository,
        private val authBusiness: AuthenticationBusiness
) : CreateAccountService {
    override fun createAccount(req: CreateAccountRequestDto, httpReq: HttpServletRequest):
            AuthenticationResponseDto {
        val credentialBytes = req.credential.value.toByteArray()
        val maybeUser = when (req.loginType) {
            LoginType.GUEST -> Optional.empty()
            LoginType.BASIC -> {
                val hashedCredential = authBusiness.hash(credentialBytes)
                userRepo.findByBasicCredential(requireNotNull(req.username), hashedCredential)
            }
            else -> throw HttpMessageNotReadableException("${req.loginType} login is not supported.",
                    ServletServerHttpRequest(httpReq))
        }

        if (maybeUser.isPresent) {
            throw AccountAlreadyExistException("Account already exists.")
        }

        val user = User().apply {
            val now = utcNow()
            val ipAddr = httpReq.extractInetAddress()

            var maybeUserWithIdToken: Optional<User>
            var newIdToken: String
            do {
                newIdToken = getRandomCapitalAlphaNumericString(CreateAccountService.ID_TOKEN_LENGTH)
                maybeUserWithIdToken = userRepo.findByIdToken(newIdToken)
            } while (maybeUserWithIdToken.isPresent)

            idToken = newIdToken
            status = Status.NORMAL
            role = Role.USER
            name = when (req.loginType) {
                LoginType.GUEST -> "$idToken@GUEST"
                LoginType.BASIC -> requireNotNull(req.username)
                else -> throw UnsupportedOperationException("${req.loginType} login is not supported.")
            }
            loginType = req.loginType
            platformType = req.platformType
            platformVersion = req.platformVersion
            appVersion = SemanticVersion.parse(req.appVersion)
            email = req.email ?: ""
            createdDate = now
            createdIp = ipAddr
            pushToken = req.pushToken.value
            invitedBy = if (req.invitedBy.isNullOrUnicodeBlank()) {
                Optional.empty()
            } else {
                userRepo.findByIdToken(requireNotNull(req.invitedBy))
            }.let {
                return@let if (it.isPresent) {
                    it.get().id
                } else {
                    0
                }
            }

            credential = when (req.loginType) {
                LoginType.GUEST -> ByteArray(0)
                LoginType.BASIC -> authBusiness.hash(credentialBytes)
                else -> throw UnsupportedOperationException("${req.loginType} login is not supported.")
            }
            member = Membership().apply {
                nickname = req.nickname
                gender = req.gender ?: Gender.UNDEFINED
                lastActiveTimestamp = now
                lastActiveIp = ipAddr
            }
        }.apply {
            setAccessToken(authBusiness.createAccessToken(this))
        }
        userRepo.save(user)

        return AuthenticationResponseDto.create(user)
    }
}
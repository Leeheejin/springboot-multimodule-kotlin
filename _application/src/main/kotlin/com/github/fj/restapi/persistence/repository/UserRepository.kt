/*
 * springboot-multimodule-kotlin skeleton.
 * Under no licences and warranty.
 */
package com.github.fj.restapi.persistence.repository

import com.github.fj.restapi.persistence.entity.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * @author Francesco Jo(nimbusob@gmail.com)
 * @since 23 - Aug - 2018
 */
@Repository
interface UserRepository : CrudRepository<User, Long>
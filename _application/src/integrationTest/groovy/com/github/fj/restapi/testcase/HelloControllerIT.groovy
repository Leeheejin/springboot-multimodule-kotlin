/*
 * springboot-multimodule-kotlin skeleton.
 * Under no licences and warranty.
 */
package com.github.fj.restapi.testcase

import com.github.fj.restapi.TestcaseTemplate
import com.github.fj.restapi.dto.hello.HelloRequestDto
import com.github.fj.restapi.dto.hello.HelloResponseDto
import com.github.fj.restapi.endpoint.ApiPaths
import org.springframework.http.HttpStatus

/**
 * @author Francesco Jo(nimbusob@gmail.com)
 * @since 23 - Aug - 2018
 */
class HelloControllerIT extends TestcaseTemplate {
    def "POST /hello echos user name"() {
        given:
        final name = "MY_NAME"
        final request = new HelloRequestDto(name)

        when:
        final response = post("${ApiPaths.VERSION}/hello", request)
        final responseDto = response.parseOk(HelloResponseDto)

        then:
        response.status == HttpStatus.OK
        responseDto.message == "POST Hello, $name"
    }
}

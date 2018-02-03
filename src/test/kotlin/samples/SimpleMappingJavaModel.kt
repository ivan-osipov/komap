package samples

import com.github.komap.mapping
import modelA.JAddress
import modelA.JUser
import modelB.JUserDto

fun main(args: Array<String>) {

    val mapper = mapping {

        plain<JUser, JUserDto> {
            strict(JUser::getUsername, JUserDto::setName)
            flexible(JUser::getAddress, JUserDto::setPlainAddress)
        }

        smart<JAddress, String> {
            "$country $city $street"
        }
    }

    val user = JUser("mr_holmes", JAddress("UK", "London", "Baker Street"))

    val userDto = mapper.mapTo<JUser, JUserDto>(user)

    println("$user\n\tmapped to\n\t$userDto")
}

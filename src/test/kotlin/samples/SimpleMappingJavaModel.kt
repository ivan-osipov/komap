package samples

import com.github.ivan_osipov.komap.mapping
import modelA.JAddress
import modelA.JUser
import modelB.JUserDto

fun main(args: Array<String>) {

    val mapper = mapping {
        // tag::fieldMappingChanges[]
        plain<JUser, JUserDto> {
            strict(JUser::getUsername, JUserDto::setName)
            flexible(JUser::getAddress, JUserDto::setPlainAddress)
        }
        // end::fieldMappingChanges[]
        smart<JAddress, String> {
            "$country $city $street"
        }
    }

    val user = JUser("mr_holmes", JAddress("UK", "London", "Baker Street"))

    val userDto = mapper.mapTo<JUser, JUserDto>(user)

    println("$user\n\tmapped to\n\t$userDto")
}

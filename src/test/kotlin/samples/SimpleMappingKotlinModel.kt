package samples

import com.github.ivan_osipov.komap.mapping
import modelA.KAddress
import modelA.KUser
import modelB.KUserDto


fun main(args: Array<String>) {
    // tag::createMapping[]
    val mapper = mapping {
        plain<KUser, KUserDto> {
            strict(KUser::username, KUserDto::name)
            flexible(KUser::address, KUserDto::plainAddress)
        }
        smart<KAddress, String> {
            "$country $city $street"
        }
    }
    // end::createMapping[]

    // tag::mapData[]
    val user = KUser("mr_holmes", KAddress("UK", "London", "Baker Street"))

    val userDto: KUserDto = mapper.mapTo(user)
    // end::mapData[]

    println("$user\n\tmapped to\n\t$userDto")
}


package samples

import com.github.komap.mapping
import modelA.Order
import modelB.LocalOrder
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.*


fun main(args: Array<String>) {
    val mapper = mapping {
        expand(DateTimeMappings)

        plain<Order, LocalOrder> {
            strict(Order::name, LocalOrder::name)
            flexible(Order::date, LocalOrder::date) //Date -> LocalDate
            flexible(Order::time, LocalOrder::time) //Time -> LocalTime
        }
    }

    val order = Order("for mr_holmes", Date(), Date()) //now

    val localOrder: LocalOrder = mapper.mapTo(order)

    println("$order\n\tmapped to\n\t$localOrder")
}

val DateTimeMappings = mapping {
    smart<Date, LocalDate> {
        toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }
    smart<Date, LocalTime> {
        toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
    }
}
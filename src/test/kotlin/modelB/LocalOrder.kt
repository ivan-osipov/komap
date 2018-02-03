package modelB

import java.time.LocalDate
import java.time.LocalTime

class LocalOrder {
    lateinit var name: String
    lateinit var date: LocalDate
    lateinit var time: LocalTime

    override fun toString(): String {
        return "LocalOrder(name='$name', date=$date, time=$time)"
    }

}
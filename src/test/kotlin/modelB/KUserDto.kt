package modelB

class KUserDto {
    lateinit var name: String
    lateinit var plainAddress: String

    override fun toString(): String {
        return "KUserDto(name='$name', plainAddress='$plainAddress')"
    }


}
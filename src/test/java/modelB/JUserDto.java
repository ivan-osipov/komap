package modelB;

public class JUserDto {

    private String name;

    private String plainAddress;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlainAddress() {
        return plainAddress;
    }

    public void setPlainAddress(String plainAddress) {
        this.plainAddress = plainAddress;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "name='" + name + '\'' +
                ", plainAddress='" + plainAddress + '\'' +
                '}';
    }
}

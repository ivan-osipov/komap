package modelA;

public class JUser {

    private String username;

    private JAddress address;

    public JUser(String username, JAddress address) {
        this.username = username;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public JAddress getAddress() {
        return address;
    }

    public void setAddress(JAddress address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", address=" + address +
                '}';
    }
}

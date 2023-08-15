package school.faang.user_service.pojo;

import lombok.Data;

@Data
public class Address {
    public String street;
    public String city;
    public String state;
    public String country;
    public String postalCode;
}

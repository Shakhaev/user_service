package school.faang.user_service.model;

import lombok.Data;

@Data
public class ContactInfo {
    private String email;
    private String phone;
    private Address address;
}

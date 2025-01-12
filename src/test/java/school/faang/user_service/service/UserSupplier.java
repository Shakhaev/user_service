package school.faang.user_service.service;

import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.ArrayList;
import java.util.List;

public class UserSupplier {
    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();

        Country countryRussia = Country.builder().id(1).title("Russia").build();
        Country countryUsa = Country.builder().id(2).title("USA").build();
        Country countryChina = Country.builder().id(3).title("China").build();

        Skill skill1 = Skill.builder().id(1).title("Skill 1").build();
        Skill skill2 = Skill.builder().id(2).title("Skill 2").build();
        Skill skill3 = Skill.builder().id(3).title("Skill 3").build();

        List<Skill> skillSet1 = new ArrayList<>(List.of(skill1));
        List<Skill> skillSet23 = new ArrayList<>(List.of(skill2, skill3));
        List<Skill> skillSet3 = new ArrayList<>(List.of(skill3));

        Contact contact1 = Contact.builder().contact("Contact 1").build();
        Contact contact2 = Contact.builder().contact("Contact 2").build();
        Contact contact3 = Contact.builder().contact("Contact 3").build();

        List<Contact> contacts1 = new ArrayList<>(List.of(contact1));
        List<Contact> contacts23 = new ArrayList<>(List.of(contact2, contact3));
        List<Contact> contacts3 = new ArrayList<>(List.of(contact3));

        User user1 = User.builder()
                .city("Moscow")
                .active(true)
                .id(1L)
                .country(new Country(1L, "Country 1", new ArrayList<>()))
                .goals(new ArrayList<>())
                .contacts(contacts1)
                .aboutMe("I'm Misha")
                .email("misha@mail.ru")
                .username("misha")
                .phone("123")
                .country(countryRussia)
                .skills(skillSet1)
                .experience(10)
                .build();
        users.add(user1);

        User user2 = User.builder()
                .city("Piter")
                .active(false)
                .id(2L)
                .country(new Country(2L, "Country 2", new ArrayList<>()))
                .goals(new ArrayList<>())
                .contacts(contacts23)
                .aboutMe("I'm Masha")
                .email("masha@mail.ru")
                .username("masha")
                .phone("456")
                .country(countryUsa)
                .skills(skillSet23)
                .experience(20)
                .build();
        users.add(user2);

        User user3 = User.builder()
                .city("Kazan")
                .active(true)
                .id(3L)
                .country(new Country(3L, "Country 3", new ArrayList<>()))
                .goals(new ArrayList<>())
                .contacts(contacts3)
                .aboutMe("I'm Kesha")
                .email("kesha@mail.ru")
                .username("kesha")
                .phone("789")
                .country(countryChina)
                .skills(skillSet3)
                .experience(30)
                .build();
        users.add(user3);

        return users;
    }
}

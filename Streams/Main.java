import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class Person {
    private int id;
    private String name;
    private int age;

    public Person(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person [id=" + id + ", name=" + name + ", age=" + age + "]";
    }
}

public class Main {
    public static void main(String[] args) {
        List<Person> personList = new ArrayList<>();
        personList.add(new Person(1, "Alice", 25));
        personList.add(new Person(2, "Bob", 30));
        personList.add(new Person(3, "Charlie", 35));

        int idToDelete = 2; 
        Optional<Integer> deletedId = personList.stream()
                .filter(person -> person.getId() == idToDelete)
                .map(person -> {
                    personList.remove(person); 
                    return person.getId(); 
                })
                .findFirst();

        if (deletedId.isPresent()) {
            System.out.println("Deleted Person ID: " + deletedId.get());
        } else {
            System.out.println("Person with the specified ID not found.");
        }
    }
}

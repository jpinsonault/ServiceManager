package servicemanager;


import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class Utils {
    public static <T> T firstMatch(Collection<T> collection, Predicate<T> predicate){
        Optional<T> result = collection.stream().filter(predicate).findFirst();

        if (result.isPresent()) {
            return result.get();
        }
        else {
            return null;
        }
    }
}

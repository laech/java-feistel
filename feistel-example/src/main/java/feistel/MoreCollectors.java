package feistel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collector;

import static java.util.Collections.unmodifiableList;

final class MoreCollectors {
    private MoreCollectors() {
    }

    static <T> Collector<T, ?, Void> split(int n, Consumer<List<T>> consumer) {
        return Collector.<T, List<T>, Void>of(
                () -> new ArrayList<>(n),
                (objects, o) -> {
                    objects.add(o);
                    drainIfNeeded(objects, false, n, consumer);
                },
                (objects, objects2) -> {
                    objects.addAll(objects2);
                    drainIfNeeded(objects, false, n, consumer);
                    return objects;
                },
                objects -> {
                    drainIfNeeded(objects, true, n, consumer);
                    return null;
                });
    }

    private static <T> void drainIfNeeded(
            List<T> objects, boolean finish, int n, Consumer<List<T>> consumer
    ) {
        if ((finish && !objects.isEmpty()) || objects.size() >= n) {
            consumer.accept(unmodifiableList(new ArrayList<>(objects)));
            objects.clear();
        }
    }
}

package hotel.booking;

import javafx.concurrent.Task;

public class BackgroundTask {
    // Generic method to run any database task in the background
    public static <V> void runInBackground(DatabaseTask<V> task, TaskCallback<V> callback) {
        Task<V> backgroundTask = new Task<>() {
            @Override
            protected V call() throws Exception {
                return task.run();
            }
        };

        backgroundTask.setOnSucceeded(event -> callback.onSuccess(backgroundTask.getValue()));
        backgroundTask.setOnFailed(event -> callback.onFailure(backgroundTask.getException()));

        Thread thread = new Thread(backgroundTask);
        thread.setDaemon(true);
        thread.start();
    }

    public interface DatabaseTask<V> {
        V run() throws Exception;
    }

    public interface TaskCallback<V> {
        void onSuccess(V result);
        void onFailure(Throwable throwable);
    }
}

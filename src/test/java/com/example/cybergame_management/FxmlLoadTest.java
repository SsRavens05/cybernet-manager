package com.example.cybergame_management;

import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FxmlLoadTest {

    @BeforeAll
    static void startJavaFx() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean started = new AtomicBoolean(false);

        try {
            Platform.startup(() -> {
                started.set(true);
                latch.countDown();
            });
        } catch (IllegalStateException alreadyStarted) {
            started.set(true);
            latch.countDown();
        }

        latch.await();
        assert started.get();
    }

    @Test
    void loadsMainView() throws Exception {
        load("main-view.fxml");
    }

    @Test
    void loadsEditedViews() throws Exception {
        load("quan-ly-nap-tien.fxml");
        load("quan-ly-khach-hang.fxml");
        load("update-khach-hang.fxml");
    }

    private void load(String fxml) throws Exception {
        URL resource = MainApp.class.getResource(fxml);
        assertNotNull(resource, fxml + " must exist");
        FXMLLoader.load(resource);
    }
}

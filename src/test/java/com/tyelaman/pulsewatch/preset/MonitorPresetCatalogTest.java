package com.tyelaman.pulsewatch.preset;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MonitorPresetCatalogTest {

    @Test
    void should_find_valid_preset_and_reject_unknown_key() {
        MonitorPresetCatalog catalog = new MonitorPresetCatalog();

        assertTrue(
                catalog.findByKey("jsonplaceholder").isPresent()
        );

        assertTrue(
                catalog.findByKey("random-api").isEmpty()
        );
    }
}
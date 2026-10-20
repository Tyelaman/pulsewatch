package com.tyelaman.pulsewatch.controller;

import com.tyelaman.pulsewatch.model.DemoSessionState;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo-session")
public class DemoSessionController {

    private static final String SESSION_STATE_KEY = "demoSessionState";

    @GetMapping("/status")
    public String getStatus(HttpSession session) {
        DemoSessionState state = getOrCreateState(session);

        return state.isHealthy() ? "UP" : "DOWN";
    }

    @PostMapping("/fail")
    public String simulateFailure(HttpSession session) {
        DemoSessionState state = getOrCreateState(session);

        state.simulateFailure();

        return "Demo session is now failing";
    }

    @PostMapping("/recover")
    public String recover(HttpSession session) {
        DemoSessionState state = getOrCreateState(session);

        state.recover();

        return "Demo session has recovered";
    }

    private DemoSessionState getOrCreateState(HttpSession session) {
        DemoSessionState state = (DemoSessionState)
                session.getAttribute(SESSION_STATE_KEY);

        if (state == null) {
            state = new DemoSessionState();
            session.setAttribute(SESSION_STATE_KEY, state);
        }

        return state;
    }
}
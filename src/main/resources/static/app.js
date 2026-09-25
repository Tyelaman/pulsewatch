const API_STATUS = "/api/status";
const API_INCIDENT = "/api/incidents/latest";

const DEMO_FAIL = "/demo/fail";
const DEMO_RECOVER = "/demo/recover";

const REFRESH_INTERVAL = 5000;

const serviceStatus = document.getElementById("service-status");
const responseTime = document.getElementById("response-time");
const httpStatus = document.getElementById("http-status");
const lastChecked = document.getElementById("last-checked");

const incidentStatus = document.getElementById("incident-status");
const incidentStarted = document.getElementById("incident-started");
const failureCount = document.getElementById("failure-count");
const aiSummary = document.getElementById("ai-summary");

const failButton = document.getElementById("fail-button");
const recoverButton = document.getElementById("recover-button");

let actionRunning = false;
let serverAvailable = false;


// FETCH CURRENT MONITOR STATUS

async function loadStatus() {
    try {
        const response = await fetch(API_STATUS);

        if (!response.ok) {
            throw new Error("Failed to load monitor status");
        }

        serverAvailable = true;
        updateButtons();

        if (response.status === 204) {
            serviceStatus.textContent = "Waiting for first check";
            serviceStatus.className = "";

            responseTime.textContent = "-- ms";
            httpStatus.textContent = "--";
            lastChecked.textContent = "--";

            return;
        }

        const result = await response.json();

        serviceStatus.textContent = result.status;
        serviceStatus.className =
            result.status === "UP" ? "status-up" : "status-down";

        responseTime.textContent =
            result.responseTimeMs + " ms";

        httpStatus.textContent =
            result.statusCode ?? "N/A";

        lastChecked.textContent =
            new Date(result.checkedAt).toLocaleString();
    }
    catch (error) {
        console.error("Status error:", error);

        serverAvailable = false;
        updateButtons();

        serviceStatus.textContent = "Unavailable";
        serviceStatus.className = "";

        responseTime.textContent = "-- ms";
        httpStatus.textContent = "--";
        lastChecked.textContent = "--";
    }
}


// FETCH LATEST INCIDENT

async function loadIncident() {
    try {
        const response = await fetch(API_INCIDENT);

        if (!response.ok) {
            throw new Error("Failed to load incident");
        }

        if (response.status === 204) {
            incidentStatus.textContent = "No incidents";
            incidentStatus.className = "";

            incidentStarted.textContent = "--";
            failureCount.textContent = "--";

            aiSummary.textContent =
                "No incident analysis available.";

            return;
        }

        const incident = await response.json();

        incidentStatus.textContent = incident.status;

        incidentStatus.className =
            incident.status === "OPEN"
                ? "status-open"
                : "status-resolved";

        incidentStarted.textContent =
            new Date(incident.startedAt).toLocaleString();

        failureCount.textContent =
            incident.failureCount;

        aiSummary.textContent =
            incident.aiSummary ||
            "AI analysis is being generated...";
    }
    catch (error) {
        console.error("Incident error:", error);

        incidentStatus.textContent = "Unavailable";
        incidentStatus.className = "";

        incidentStarted.textContent = "--";
        failureCount.textContent = "--";

        aiSummary.textContent =
            "Unable to retrieve incident information.";
    }
}


// UPDATE BUTTON AVAILABILITY

function updateButtons() {
    const disabled = actionRunning || !serverAvailable;

    failButton.disabled = disabled;
    recoverButton.disabled = disabled;
}


// REFRESH DASHBOARD

async function refreshDashboard() {
    await Promise.all([
        loadStatus(),
        loadIncident()
    ]);
}


// SEND DEMO COMMAND

async function sendDemoCommand(url) {
    if (actionRunning) {
        return;
    }

    actionRunning = true;
    updateButtons();

    try {
        const response = await fetch(url, {
            method: "POST"
        });

        if (!response.ok) {
            throw new Error("Demo command failed");
        }

        await refreshDashboard();
    }
    catch (error) {
        console.error("Demo command error:", error);

        alert(
            "Could not execute the demo command. " +
            "Check that PulseWatch is running."
        );
    }
    finally {
        actionRunning = false;
        updateButtons();
    }
}


// BUTTON EVENT LISTENERS

failButton.addEventListener("click", function () {
    sendDemoCommand(DEMO_FAIL);
});

recoverButton.addEventListener("click", function () {
    sendDemoCommand(DEMO_RECOVER);
});


// START AUTOMATIC REFRESH

async function startDashboard() {
    await refreshDashboard();

    setTimeout(startDashboard, REFRESH_INTERVAL);
}

startDashboard();
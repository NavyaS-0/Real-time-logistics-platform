async function trackShipment() {

    const shipmentId = document.getElementById("shipmentId").value.trim();
    const currentStatus = document.getElementById("currentStatus");
    const historyContainer = document.getElementById("history");
    const trackButton = document.getElementById("trackButton");

    // Validate input
    if (!shipmentId) {
        alert("Please enter a shipment ID.");
        return;
    }

    // Show loading state
    trackButton.disabled = true;
    trackButton.textContent = "Tracking...";

    currentStatus.innerHTML = `
        <div class="empty-state">
            <div class="empty-icon">⏳</div>
            <h3>Loading Shipment...</h3>
            <p>Retrieving the latest tracking information.</p>
        </div>
    `;

    historyContainer.innerHTML = `
        <div class="empty-state">
            <div class="empty-icon">⏳</div>
            <h3>Loading Journey...</h3>
            <p>Retrieving tracking history.</p>
        </div>
    `;

    try {

        const response = await fetch(
            `http://localhost:8082/api/tracking/${shipmentId}`
        );

        if (!response.ok) {
            throw new Error("Shipment not found");
        }

        const history = await response.json();

        // No tracking records
        if (!history || history.length === 0) {

            currentStatus.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">📦</div>
                    <h3>No Tracking Records</h3>
                    <p>No tracking information was found for shipment ${shipmentId}.</p>
                </div>
            `;

            historyContainer.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">🗺️</div>
                    <h3>No Tracking History</h3>
                    <p>This shipment does not have any tracking records yet.</p>
                </div>
            `;

            return;
        }

        // Latest tracking record
        const latest = history[history.length - 1];

        // ================================
        // CURRENT STATUS
        // ================================

        currentStatus.innerHTML = `
            <div style="
                display: grid;
                grid-template-columns: repeat(4, 1fr);
                gap: 20px;
            ">

                <div>
                    <small style="color:#64748b;">
                        SHIPMENT ID
                    </small>
                    <h3>#${shipmentId}</h3>
                </div>

                <div>
                    <small style="color:#64748b;">
                        CURRENT LOCATION
                    </small>
                    <h3>📍 ${latest.location}</h3>
                </div>

                <div>
                    <small style="color:#64748b;">
                        STATUS
                    </small>
                    <h3>${latest.status}</h3>
                </div>

                <div>
                    <small style="color:#64748b;">
                        LAST UPDATED
                    </small>
                    <h3>
                        ${new Date(latest.timestamp).toLocaleString()}
                    </h3>
                </div>

            </div>
        `;


        // ================================
        // SHIPMENT JOURNEY
        // ================================

        historyContainer.innerHTML = "";

        history.forEach((record, index) => {

            const item = document.createElement("div");

            item.className = "history-item";

            item.style.cssText = `
                position: relative;
                padding: 20px;
                margin-bottom: 15px;
                border-left: 4px solid #2563eb;
                background: #f8fafc;
                border-radius: 8px;
            `;

            item.innerHTML = `
                <div style="
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    gap: 20px;
                ">

                    <div>
                        <h3 style="margin-bottom: 5px;">
                            📍 ${record.location}
                        </h3>

                        <p style="
                            margin: 0;
                            color: #475569;
                        ">
                            Status: <strong>${record.status}</strong>
                        </p>
                    </div>

                    <div style="
                        text-align: right;
                        color: #64748b;
                        font-size: 13px;
                    ">
                        ${new Date(record.timestamp).toLocaleString()}
                    </div>

                </div>
            `;

            historyContainer.appendChild(item);
        });

    } catch (error) {

        console.error("Tracking error:", error);

        currentStatus.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">❌</div>
                <h3>Shipment Not Found</h3>
                <p>
                    We could not retrieve tracking information
                    for shipment ${shipmentId}.
                </p>
            </div>
        `;

        historyContainer.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">🗺️</div>
                <h3>No Journey Available</h3>
                <p>
                    Please check the shipment ID and try again.
                </p>
            </div>
        `;

    } finally {

        trackButton.disabled = false;
        trackButton.textContent = "Track Shipment";

    }
}
from fastapi import FastAPI
from pydantic import BaseModel
from typing import List


app = FastAPI(title="Alert Service")


alerts: List[dict] = []


class AlertRequest(BaseModel):
    shipment_id: int
    location: str
    status: str
    message: str


@app.get("/api/alerts")
def get_alerts():
    return alerts


@app.post("/api/alerts")
def create_alert(request: AlertRequest):

    alert = {
        "shipment_id": request.shipment_id,
        "location": request.location,
        "status": request.status,
        "message": request.message
    }

    alerts.append(alert)

    return alert


def add_alert(
    shipment_id: int,
    location: str,
    status: str,
    message: str
):

    alert = {
        "shipment_id": shipment_id,
        "location": location,
        "status": status,
        "message": message
    }

    alerts.append(alert)

    print("Alert saved:", alert)
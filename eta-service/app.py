from fastapi import FastAPI
from pydantic import BaseModel
import redis
import os


app = FastAPI(title="ETA Service")


# Redis connection
redis_client = redis.Redis(
    host=os.getenv("REDIS_HOST", "localhost"),
    port=int(os.getenv("REDIS_PORT", "6379")),
    decode_responses=True
)


# Test Redis connection when the service starts
@app.on_event("startup")
def startup_event():

    try:
        redis_client.ping()
        print("Redis connected successfully")
    except Exception as error:
        print("Redis connection error:", error)


class ETARequest(BaseModel):
    distance_km: float
    speed_kmph: float
    delay_minutes: float = 0


# Calculate ETA manually
@app.post("/api/eta")
def calculate_eta(request: ETARequest):

    if request.speed_kmph <= 0:
        return {
            "error": "Speed must be greater than 0"
        }

    travel_time_hours = (
        request.distance_km /
        request.speed_kmph
    )

    travel_time_minutes = (
        travel_time_hours * 60
    )

    eta_minutes = (
        travel_time_minutes +
        request.delay_minutes
    )

    return {
        "distance_km": request.distance_km,
        "speed_kmph": request.speed_kmph,
        "delay_minutes": request.delay_minutes,
        "estimated_time_minutes": round(
            eta_minutes,
            2
        )
    }


# Get ETA from Redis
@app.get("/api/eta/{shipment_id}")
def get_shipment_eta(shipment_id: int):

    redis_key = f"shipment:{shipment_id}:eta"

    eta = redis_client.get(redis_key)

    if eta is None:
        return {
            "shipment_id": shipment_id,
            "message": "ETA not found"
        }

    return {
        "shipment_id": shipment_id,
        "estimated_time_minutes": float(eta)
    }
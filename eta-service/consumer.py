from kafka import KafkaConsumer
import re
import redis
import os


# Redis connection
redis_client = redis.Redis(
    host=os.getenv("REDIS_HOST", "localhost"),
    port=int(os.getenv("REDIS_PORT", "6379")),
    decode_responses=True
)


# ETA data for shipments
shipment_data = {
    1: {"distance": 350, "speed": 55, "delay": 10},
    2: {"distance": 150, "speed": 50, "delay": 5},
    3: {"distance": 1600, "speed": 60, "delay": 20},
    4: {"distance": 1300, "speed": 50, "delay": 15},
    5: {"distance": 2100, "speed": 55, "delay": 10},
    6: {"distance": 1500, "speed": 50, "delay": 25},
    7: {"distance": 570, "speed": 55, "delay": 0},
    8: {"distance": 500, "speed": 50, "delay": 10},
    9: {"distance": 270, "speed": 55, "delay": 5},
    10: {"distance": 550, "speed": 50, "delay": 15}
}


def calculate_shipment_eta(shipment_id):

    data = shipment_data.get(shipment_id)

    if not data:
        return None

    travel_time = (
        data["distance"] / data["speed"]
    ) * 60

    return round(
        travel_time + data["delay"],
        2
    )


print("Connecting to Redis...")

try:
    redis_client.ping()
    print("Redis connected successfully")
except Exception as error:
    print("Redis connection error:", error)


print("Starting ETA Kafka consumer...")


consumer = KafkaConsumer(
    "shipment-tracking",
    bootstrap_servers=[
        os.getenv(
            "KAFKA_BOOTSTRAP_SERVERS",
            os.getenv("KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")
        )
    ],
    group_id="eta-redis-consumer",
    auto_offset_reset="earliest",
    enable_auto_commit=True
)


print("ETA Kafka consumer connected")
print("Waiting for tracking updates...")


for message in consumer:

    tracking_message = message.value.decode("utf-8")

    print(
        "Received tracking update:",
        tracking_message
    )

    match = re.search(
        r"Shipment (\d+)",
        tracking_message
    )

    if match:

        shipment_id = int(match.group(1))

        eta = calculate_shipment_eta(shipment_id)

        if eta is not None:

            # Store latest ETA in Redis
            redis_key = f"shipment:{shipment_id}:eta"

            redis_client.set(
                redis_key,
                eta
            )

            print(
                f"Estimated arrival time for Shipment "
                f"{shipment_id}: {eta} minutes"
            )

            print(
                f"Saved to Redis: "
                f"{redis_key} = {eta}"
            )

        else:

            print(
                f"No ETA data found for Shipment {shipment_id}"
            )
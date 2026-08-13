from kafka import KafkaConsumer
import re
import os

from app import add_alert


print("Starting Alert Kafka consumer...")


consumer = KafkaConsumer(
    "shipment-tracking",
    bootstrap_servers=[
        os.getenv(
            "KAFKA_BOOTSTRAP_SERVERS",
            os.getenv("KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")
        )
    ],
    group_id="alert-consumer",
    auto_offset_reset="earliest",
    enable_auto_commit=True
)


print("Alert Kafka consumer connected")
print("Waiting for tracking updates...")


for message in consumer:

    try:

        tracking_message = message.value.decode("utf-8")

        print("Received tracking update:")
        print(tracking_message)

        match = re.search(
            r"Shipment (\d+) moved to (.+?)(?: with status (.+))?$",
            tracking_message
        )

        if match:

            shipment_id = int(match.group(1))
            location = match.group(2)

            status = match.group(3)

            if status:
                status = status.strip()

            print(
                f"Parsed shipment: {shipment_id}"
            )

            print(
                f"Location: {location}"
            )

            print(
                f"Status: {status}"
            )

            if status == "OUT_FOR_DELIVERY":

                message_text = (
                    f"Shipment {shipment_id} "
                    f"is out for delivery"
                )

                add_alert(
                    shipment_id,
                    location,
                    status,
                    message_text
                )

                print(
                    f"ALERT: Shipment {shipment_id} "
                    f"is OUT FOR DELIVERY at {location}"
                )

            elif status == "DELIVERED":

                message_text = (
                    f"Shipment {shipment_id} "
                    f"has been delivered"
                )

                add_alert(
                    shipment_id,
                    location,
                    status,
                    message_text
                )

                print(
                    f"ALERT: Shipment {shipment_id} "
                    f"has been DELIVERED at {location}"
                )

        else:

            print(
                "Could not parse tracking message."
            )

    except Exception as error:

        print(
            "Error processing Kafka message:",
            error
        )
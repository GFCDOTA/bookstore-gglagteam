# ------------------------------------------------------------------------------
# SNS Topic — single fan-out topic for all bookstore domain events
# ------------------------------------------------------------------------------
resource "aws_sns_topic" "bookstore_events" {
  name = "bookstore-events"
}

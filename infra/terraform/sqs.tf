# ------------------------------------------------------------------------------
# SQS Queues — one pair (queue + DLQ) per bounded context
# ------------------------------------------------------------------------------

locals {
  queues = {
    catalog = {
      name          = "catalog-events"
      filter_events = ["OrderCreated", "OrderCancelled"]
    }
    order = {
      name          = "order-events"
      filter_events = ["StockReserved", "StockReservationFailed", "PaymentApproved", "PaymentRejected"]
    }
    payment = {
      name          = "payment-events"
      filter_events = ["OrderCreated"]
    }
    notification = {
      name          = "notification-events"
      filter_events = ["OrderConfirmed", "OrderCancelled", "PaymentApproved", "PaymentRejected"]
    }
  }
}

# --- Dead-letter queues ---
resource "aws_sqs_queue" "dlq" {
  for_each = local.queues
  name     = "${each.value.name}-dlq"
}

# --- Main queues with redrive policy ---
resource "aws_sqs_queue" "main" {
  for_each = local.queues
  name     = "${each.value.name}-queue"

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.dlq[each.key].arn
    maxReceiveCount     = var.sqs_max_receive_count
  })
}

# --- SQS policy to allow SNS to send messages ---
resource "aws_sqs_queue_policy" "allow_sns" {
  for_each  = local.queues
  queue_url = aws_sqs_queue.main[each.key].url

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "AllowSNSPublish"
        Effect    = "Allow"
        Principal = "*"
        Action    = "sqs:SendMessage"
        Resource  = aws_sqs_queue.main[each.key].arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_sns_topic.bookstore_events.arn
          }
        }
      }
    ]
  })
}

# --- SNS subscriptions with message filtering ---
resource "aws_sns_topic_subscription" "queue_subscription" {
  for_each  = local.queues
  topic_arn = aws_sns_topic.bookstore_events.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.main[each.key].arn

  raw_message_delivery = true

  filter_policy = jsonencode({
    eventType = each.value.filter_events
  })
}

# ------------------------------------------------------------------------------
# SSM Parameter Store — service discovery parameters
# ------------------------------------------------------------------------------
resource "aws_ssm_parameter" "sns_topic_arn" {
  name  = "/bookstore/${var.environment}/sns/topic-arn"
  type  = "String"
  value = aws_sns_topic.bookstore_events.arn
}

resource "aws_ssm_parameter" "sqs_queue_url" {
  for_each = local.queues
  name     = "/bookstore/${var.environment}/sqs/${each.value.name}-queue-url"
  type     = "String"
  value    = aws_sqs_queue.main[each.key].url
}

# ------------------------------------------------------------------------------
# Outputs — useful references for services and debugging
# ------------------------------------------------------------------------------
output "sns_topic_arn" {
  description = "ARN of the bookstore-events SNS topic"
  value       = aws_sns_topic.bookstore_events.arn
}

output "sqs_queue_urls" {
  description = "URLs of all main SQS queues"
  value = {
    for key, q in aws_sqs_queue.main : key => q.url
  }
}

output "sqs_dlq_urls" {
  description = "URLs of all dead-letter queues"
  value = {
    for key, q in aws_sqs_queue.dlq : key => q.url
  }
}

output "sqs_queue_arns" {
  description = "ARNs of all main SQS queues"
  value = {
    for key, q in aws_sqs_queue.main : key => q.arn
  }
}

output "ssm_parameter_names" {
  description = "Names of all SSM parameters created"
  value = merge(
    { sns_topic_arn = aws_ssm_parameter.sns_topic_arn.name },
    { for key, p in aws_ssm_parameter.sqs_queue_url : key => p.name }
  )
}

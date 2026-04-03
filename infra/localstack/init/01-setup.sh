#!/bin/bash
set -e

echo "=== Setting up LocalStack resources ==="

# Create SNS topic
awslocal sns create-topic --name bookstore-events

# Create SQS queues and DLQs
awslocal sqs create-queue --queue-name catalog-events-dlq
awslocal sqs create-queue --queue-name catalog-events-queue \
  --attributes '{"RedrivePolicy":"{\"deadLetterTargetArn\":\"arn:aws:sqs:us-east-1:000000000000:catalog-events-dlq\",\"maxReceiveCount\":\"3\"}"}'

awslocal sqs create-queue --queue-name order-events-dlq
awslocal sqs create-queue --queue-name order-events-queue \
  --attributes '{"RedrivePolicy":"{\"deadLetterTargetArn\":\"arn:aws:sqs:us-east-1:000000000000:order-events-dlq\",\"maxReceiveCount\":\"3\"}"}'

awslocal sqs create-queue --queue-name payment-events-dlq
awslocal sqs create-queue --queue-name payment-events-queue \
  --attributes '{"RedrivePolicy":"{\"deadLetterTargetArn\":\"arn:aws:sqs:us-east-1:000000000000:payment-events-dlq\",\"maxReceiveCount\":\"3\"}"}'

awslocal sqs create-queue --queue-name notification-events-dlq
awslocal sqs create-queue --queue-name notification-events-queue \
  --attributes '{"RedrivePolicy":"{\"deadLetterTargetArn\":\"arn:aws:sqs:us-east-1:000000000000:notification-events-dlq\",\"maxReceiveCount\":\"3\"}"}'

# Get topic ARN
TOPIC_ARN=$(awslocal sns list-topics --query 'Topics[0].TopicArn' --output text)

# Subscribe catalog queue to SNS with filter
CATALOG_QUEUE_ARN=$(awslocal sqs get-queue-attributes \
  --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/catalog-events-queue \
  --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)

awslocal sns subscribe \
  --topic-arn "$TOPIC_ARN" \
  --protocol sqs \
  --notification-endpoint "$CATALOG_QUEUE_ARN" \
  --attributes '{"FilterPolicy":"{\"eventType\":[\"OrderCreated\",\"OrderCancelled\"]}", "RawMessageDelivery":"true"}'

# Subscribe order queue to SNS with filter
ORDER_QUEUE_ARN=$(awslocal sqs get-queue-attributes \
  --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/order-events-queue \
  --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)

awslocal sns subscribe \
  --topic-arn "$TOPIC_ARN" \
  --protocol sqs \
  --notification-endpoint "$ORDER_QUEUE_ARN" \
  --attributes '{"FilterPolicy":"{\"eventType\":[\"StockReserved\",\"StockReservationFailed\",\"PaymentApproved\",\"PaymentRejected\"]}", "RawMessageDelivery":"true"}'

# Subscribe payment queue to SNS with filter
PAYMENT_QUEUE_ARN=$(awslocal sqs get-queue-attributes \
  --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/payment-events-queue \
  --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)

awslocal sns subscribe \
  --topic-arn "$TOPIC_ARN" \
  --protocol sqs \
  --notification-endpoint "$PAYMENT_QUEUE_ARN" \
  --attributes '{"FilterPolicy":"{\"eventType\":[\"OrderCreated\"]}", "RawMessageDelivery":"true"}'

# Subscribe notification queue to SNS with filter
NOTIFICATION_QUEUE_ARN=$(awslocal sqs get-queue-attributes \
  --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/notification-events-queue \
  --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)

awslocal sns subscribe \
  --topic-arn "$TOPIC_ARN" \
  --protocol sqs \
  --notification-endpoint "$NOTIFICATION_QUEUE_ARN" \
  --attributes '{"FilterPolicy":"{\"eventType\":[\"OrderConfirmed\",\"OrderCancelled\",\"PaymentApproved\",\"PaymentRejected\"]}", "RawMessageDelivery":"true"}'

# SSM Parameter Store
awslocal ssm put-parameter --name "/bookstore/local/sns/topic-arn" \
  --value "$TOPIC_ARN" --type String --overwrite

awslocal ssm put-parameter --name "/bookstore/local/sqs/catalog-queue-url" \
  --value "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/catalog-events-queue" --type String --overwrite

awslocal ssm put-parameter --name "/bookstore/local/sqs/order-queue-url" \
  --value "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/order-events-queue" --type String --overwrite

awslocal ssm put-parameter --name "/bookstore/local/sqs/payment-queue-url" \
  --value "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/payment-events-queue" --type String --overwrite

awslocal ssm put-parameter --name "/bookstore/local/sqs/notification-queue-url" \
  --value "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/notification-events-queue" --type String --overwrite

# Secrets Manager — values come from docker-compose environment
CATALOG_DB_USER=${CATALOG_DB_USER:-catalog_user}
CATALOG_DB_PASS=${CATALOG_DB_PASS:-changeme}
ORDER_DB_USER=${ORDER_DB_USER:-order_user}
ORDER_DB_PASS=${ORDER_DB_PASS:-changeme}
PAYMENT_DB_USER=${PAYMENT_DB_USER:-payment_user}
PAYMENT_DB_PASS=${PAYMENT_DB_PASS:-changeme}

awslocal secretsmanager create-secret --name "/bookstore/local/db/catalog" \
  --secret-string "{\"username\":\"${CATALOG_DB_USER}\",\"password\":\"${CATALOG_DB_PASS}\"}" || true

awslocal secretsmanager create-secret --name "/bookstore/local/db/order" \
  --secret-string "{\"username\":\"${ORDER_DB_USER}\",\"password\":\"${ORDER_DB_PASS}\"}" || true

awslocal secretsmanager create-secret --name "/bookstore/local/db/payment" \
  --secret-string "{\"username\":\"${PAYMENT_DB_USER}\",\"password\":\"${PAYMENT_DB_PASS}\"}" || true

echo "=== LocalStack setup complete ==="
echo "Topic ARN: $TOPIC_ARN"
awslocal sns list-subscriptions --query 'Subscriptions[*].[Protocol,Endpoint]' --output table

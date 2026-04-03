variable "aws_region" {
  description = "AWS region for MiniStack"
  type        = string
  default     = "us-east-1"
}

variable "ministack_endpoint" {
  description = "MiniStack endpoint URL"
  type        = string
  default     = "http://localhost:4566"
}

variable "environment" {
  description = "Environment name (e.g. local, dev, staging, prod)"
  type        = string
  default     = "local"
}

variable "sqs_max_receive_count" {
  description = "Maximum number of receives before sending to DLQ"
  type        = number
  default     = 3
}

variable "catalog_db_user" {
  description = "Catalog database username"
  type        = string
  default     = "catalog_user"
}

variable "catalog_db_password" {
  description = "Catalog database password"
  type        = string
  default     = "changeme"
  sensitive   = true
}

variable "order_db_user" {
  description = "Order database username"
  type        = string
  default     = "order_user"
}

variable "order_db_password" {
  description = "Order database password"
  type        = string
  default     = "changeme"
  sensitive   = true
}

variable "payment_db_user" {
  description = "Payment database username"
  type        = string
  default     = "payment_user"
}

variable "payment_db_password" {
  description = "Payment database password"
  type        = string
  default     = "changeme"
  sensitive   = true
}

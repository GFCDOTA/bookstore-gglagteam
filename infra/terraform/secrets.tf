# ------------------------------------------------------------------------------
# Secrets Manager — database credentials per bounded context
# ------------------------------------------------------------------------------
resource "aws_secretsmanager_secret" "db_catalog" {
  name = "/bookstore/${var.environment}/db/catalog"
}

resource "aws_secretsmanager_secret_version" "db_catalog" {
  secret_id = aws_secretsmanager_secret.db_catalog.id
  secret_string = jsonencode({
    username = var.catalog_db_user
    password = var.catalog_db_password
  })
}

resource "aws_secretsmanager_secret" "db_order" {
  name = "/bookstore/${var.environment}/db/order"
}

resource "aws_secretsmanager_secret_version" "db_order" {
  secret_id = aws_secretsmanager_secret.db_order.id
  secret_string = jsonencode({
    username = var.order_db_user
    password = var.order_db_password
  })
}

resource "aws_secretsmanager_secret" "db_payment" {
  name = "/bookstore/${var.environment}/db/payment"
}

resource "aws_secretsmanager_secret_version" "db_payment" {
  secret_id = aws_secretsmanager_secret.db_payment.id
  secret_string = jsonencode({
    username = var.payment_db_user
    password = var.payment_db_password
  })
}

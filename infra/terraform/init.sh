#!/bin/bash
set -e

echo "=== Initializing infrastructure with Terraform ==="

cd /terraform

terraform init -input=false
terraform apply -auto-approve -input=false \
  -var="ministack_endpoint=http://ministack:4566"

echo ""
echo "=== Terraform apply complete ==="
terraform output -json

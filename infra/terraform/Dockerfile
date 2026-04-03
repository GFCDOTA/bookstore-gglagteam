FROM hashicorp/terraform:1.9

WORKDIR /terraform

COPY *.tf ./
COPY terraform.tfvars ./
COPY init.sh ./

RUN chmod +x /terraform/init.sh

ENTRYPOINT ["/bin/sh", "/terraform/init.sh"]

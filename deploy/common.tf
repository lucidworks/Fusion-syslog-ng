variable "environment" {}

variable "environment_abbr" {}
variable "vpc" {}

variable "region" {
  default = "us-east-1"
}

variable "zone" {
  default = "us-east-1c"
}

variable "ami" {
  default = "ami-43a15f3e"
}

provider "aws" {
  access_key = ""
  secret_key = ""
  region     = "${var.region}"
}
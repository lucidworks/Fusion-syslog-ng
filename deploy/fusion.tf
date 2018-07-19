resource "aws_instance" "fusion" {
  count = 1
  ami = "${var.ami}"
  instance_type = "t2.2xlarge"
  availability_zone = "${var.zone}"
  key_name = "cto"

  root_block_device {
    volume_size = 100
  }

  tags {
    Name = "fusion"
    CostCenter = "cto"
  }

  connection {
    type = "ssh"
    user = "ubuntu"
    private_key = "${file("credential/cto.pem")}"
  }

  subnet_id = "subnet-eb3fd99d"

  provisioner "remote-exec" {
    inline = [
      "sudo apt update --fix-missing",
      "sudo apt install -y openjdk-8-jdk"
    ]
  }

  provisioner "file" {
    source = "file/fusion.tar.gz"
    destination = "/tmp/fusion.tar.gz"
  }

  provisioner "remote-exec" {
    inline = [
      "sudo mv /tmp/fusion.tar.gz /opt",
      "sudo tar -xzvf /opt/fusion.tar.gz",
      "sudo mv fusion/4.1.0-SNAPSHOT /opt/fusion",
      "sudo chown -R ubuntu /opt",
      "/opt/fusion/bin/fusion start"
    ]
  }
}
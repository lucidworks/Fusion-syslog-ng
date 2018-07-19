resource "aws_instance" "syslog-ng" {
  count = 1
  ami = "${var.ami}"
  instance_type = "t2.medium"
  availability_zone = "${var.zone}"

  key_name = "cto"

  tags {
    Name = "syslog-ng"
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
      "wget -qO - http://download.opensuse.org/repositories/home:/laszlo_budai:/syslog-ng/xUbuntu_16.04/Release.key | sudo apt-key add -",
      "echo \"deb http://download.opensuse.org/repositories/home:/laszlo_budai:/syslog-ng/xUbuntu_16.04 ./\" | sudo tee -a /etc/apt/sources.list",
      "sudo apt update",
      "sudo apt install -y syslog-ng-core syslog-ng-mod-java"
    ]
  }

  provisioner "file" {
    source = "configuration/syslog-ng.conf"
    destination = "/tmp/syslog-ng.conf"
  }

  provisioner "file" {
    source = "../build/libs/fusion-1.0-SNAPSHOT.jar"
    destination = "/tmp/fusion.jar"
  }

  provisioner "file" {
    source = "configuration/ld-syslog-ng.conf"
    destination = "/tmp/ld-syslog-ng.conf"
  }

  provisioner "remote-exec" {
    inline = [
      "sudo mv /tmp/syslog-ng.conf /etc/syslog-ng/syslog-ng.conf",
      "sudo mv /tmp/fusion.jar /usr/lib/syslog-ng/3.15/java-modules/fusion.jar",
      "sudo mv /tmp/ld-syslog-ng.conf /etc/ld.so.conf.d/ld-syslog-ng.conf",
      "sudo ldconfig -v",
      "sudo rm -rf /etc/default/syslog-ng.conf",
      "sudo /etc/init.d/syslog-ng start"
    ]
  }
}
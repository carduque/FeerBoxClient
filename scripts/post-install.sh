#!/bin/bash

sudo sh -c 'echo "vm.panic_on_oom=1" >> /etc/sysctl.conf'
sudo sh -c 'echo "kernel.panic=3" >> /etc/sysctl.conf'
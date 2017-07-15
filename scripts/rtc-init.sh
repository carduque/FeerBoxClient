#!/bin/bash
date
sudo timedatectl set-ntp 1
sleep 5
sudo hwclock -w
sudo hwclock -r
date
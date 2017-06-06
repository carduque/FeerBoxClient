#!/bin/bash
date
sudo ntpdate-debian
sudo hwclock -w
sudo hwclock -r
date
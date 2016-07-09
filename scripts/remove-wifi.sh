#!/bin/bash
cp /etc/wpa_supplicant/wpa_supplicant.conf /etc/wpa_supplicant/wpa_supplicant.conf.old
echo "BEFORE..."
cat /etc/wpa_supplicant/wpa_supplicant.conf


SSID_TO_DELETE=$1
sed -n "1 !H;1 h;$ {x;s/[[:space:]]*network={\n[[:space:]]*ssid=\"${SSID_TO_DELETE}\"[^}]*}//g;p;}" /etc/wpa_supplicant/wpa_supplicant.conf

echo "AFTER..."
cat /etc/wpa_supplicant/wpa_supplicant.conf
#!/bin/bash
cp /etc/wpa_supplicant/wpa_supplicant.conf /etc/wpa_supplicant/wpa_supplicant.conf.old

echo "">>/etc/wpa_supplicant/wpa_supplicant.conf
echo "network={">>/etc/wpa_supplicant/wpa_supplicant.conf
        echo "ssid=\"$1\"">>/etc/wpa_supplicant/wpa_supplicant.conf
        echo "psk=\"$2\"">>/etc/wpa_supplicant/wpa_supplicant.conf
        echo "proto=RSN">>/etc/wpa_supplicant/wpa_supplicant.conf
        echo "key_mgmt=WPA-PSK">>/etc/wpa_supplicant/wpa_supplicant.conf
        echo "pairwise=CCMP">>/etc/wpa_supplicant/wpa_supplicant.conf
        echo "auth_alg=OPEN">>/etc/wpa_supplicant/wpa_supplicant.conf
echo "}">>/etc/wpa_supplicant/wpa_supplicant.conf
echo "">>/etc/wpa_supplicant/wpa_supplicant.conf

cat /etc/wpa_supplicant/wpa_supplicant.conf
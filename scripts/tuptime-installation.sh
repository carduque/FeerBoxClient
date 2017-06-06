#!/bin/bash
if [ -x "$(command -v tuptime)" ]; then
echo 'Tuptime already installed. Aborting.' >&2
  exit 1
fi
curl -s https://raw.githubusercontent.com/rfrail3/tuptime/master/tuptime-install.sh | sudo bash
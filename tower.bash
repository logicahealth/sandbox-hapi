#!/bin/bash -ex

# host = Tower host
# username = Tower username
# password = Tower user password
# ID = ID of job template being launched.

echo "Configuring Tower Settings"
hostval=$(tower-cli config host $TOWER_HOST)
userval=$(tower-cli config username $TOWER_USERNAME)
passwordval=$(tower-cli config password $TOWER_PASSWORD)

if [[ $userval == "username: " ]] || [[ $passwordval == "password: " ]]
then
  echo "WARNING: Configuration has not been fully set";
  echo "   You will want to run the $ tower-cli config ";
  echo "   command for host, username, and password ";
fi

echo " current configuration settings:"
echo $hostval
echo $userval
echo $passwordval

#tower-cli config verify_ssl false

# Let's run a tower-cli job
tower-cli job launch --job-template $TOWER_TEMPLATE_ID --monitor
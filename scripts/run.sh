#!/bin/bash

# cd to sim folder, no matter where we are calling this script
cd "$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )/"

echo "Building Android Emulator"
# Build local docker image
docker build -t astrobee-android-sim .

rootdir=$(dirname "$(readlink -f "$0")")
cd $rootdir
export bootstrap_dir=${rootdir}/bootstrap
generator_dir=${rootdir}/generator

export pcd_dir=/tmp/pcd/

export XSOCK=/tmp/.X11-unix
export XAUTH=/tmp/.docker.xauth
touch $XAUTH
xauth nlist $DISPLAY | sed -e 's/^..../ffff/' | xauth -f $XAUTH nmerge -

# Run Simulator
#
export DISPLAY=$(ip route list default | awk '{print $3}'):0
export LIBGL_ALWAYS_INDIRECT=1
export ROS_IP=$(getent hosts llp | awk '{ print $1 }')

docker-compose build
docker-compose up
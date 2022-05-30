#!/bin/bash

##############################
# Static values
#

IMAGE="ghcr.io/pepperoni505/astrobee-krpc:astrobee_sim-base-3.0.0"

rootdir=$(dirname "$(readlink -f "$0")")
cd $rootdir
bootstrap_dir=${rootdir}/bootstrap
generator_dir=${rootdir}/generator

pcd_dir=/tmp/pcd/

XSOCK=/tmp/.X11-unix
XAUTH=/tmp/.docker.xauth
touch $XAUTH
xauth nlist $DISPLAY | sed -e 's/^..../ffff/' | xauth -f $XAUTH nmerge -

# Run Simulator
#

ROS_IP=$(getent hosts llp | awk '{ print $1 }')

docker run -it --rm --name astrobee \
        --volume=$XSOCK:$XSOCK:rw \
        --volume=$XAUTH:$XAUTH:rw \
        --volume=${pcd_dir}:/src/astrobee/src/simulation/pcd \
        --volume=${bootstrap_dir}:/tmp/bootstrap \
        --env="XAUTHORITY=${XAUTH}" \
        --env="DISPLAY" \
        --env="ROS_MASTER_URI=http://${ROS_IP}:11311" \
        --env="ROS_HOSTNAME=${ROS_IP}" \
        --user="astrobee" \
        --privileged \
        --network=host \
        $IMAGE \
        /astrobee_init.sh bash /tmp/bootstrap/run.sh



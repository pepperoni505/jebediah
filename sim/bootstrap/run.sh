#!/bin/sh

#####################
# Bootstrap scripts #
#####################

export ROS_IP=$(getent hosts llp | awk '{ print $1 }')
export ROS_MASTER_URI=http://${ROS_IP}:11311
export ROS_HOSTNAME=${ROS_IP}

# update env files
bash /tmp/bootstrap/copy_objects.sh

# run
/astrobee_init.sh roslaunch astrobee sim.launch dds:=false robot:=sim_pub rviz:=true
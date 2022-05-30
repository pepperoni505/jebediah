#!/bin/sh

#####################
# Bootstrap scripts #
#####################

# update env files
bash /tmp/bootstrap/copy_objects.sh

# run
/astrobee_init.sh roslaunch astrobee sim.launch dds:=false robot:=sim_pub rviz:=true
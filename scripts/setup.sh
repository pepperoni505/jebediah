#!/bin/bash
git_tag=v0.16.0

# Check Variables -------------------------------------------------
while getopts g option
do
    case $option in
        g)  gui="gui"
    esac
done

# cd to sim folder, no matter where we are calling this script
cd "$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )/../sim"

root_dir=$(pwd)
astrobee_dir=${root_dir}/astrobee/src/astrobee
if [ ! -e ${astrobee_dir} ]; then
    git clone https://github.com/nasa/astrobee.git \
        -b ${git_tag} \
        --depth 1 \
        ${astrobee_dir}
    cd ${astrobee_dir}
    git pull origin 4bfe9474097561b9965e89eed18688a788d12769
    git submodule update --init description/media
    cd -

    patch ./astrobee/src/astrobee/scripts/docker/build.sh ./patches/build.sh.patch
    patch ./astrobee/src/astrobee/scripts/docker/astrobee_base.Dockerfile ./patches/astrobee_base.Dockerfile.patch 
    patch ./astrobee/src/astrobee/scripts/docker/astrobee.Dockerfile ./patches/astrobee.Dockerfile.patch
    patch ./astrobee/src/astrobee/scripts/setup/install_desktop_packages.sh ./patches/install_desktop_packages.sh.patch
    patch ./astrobee/src/astrobee/scripts/setup/debians/build_install_debians.sh ./patches/build_install_debians.sh.patch
    cp ./patches/OpenCVDownload.patch ./astrobee/src/astrobee/scripts/setup/debians/opencv/patches
    echo -e "\nOpenCVDownload.patch" >> ./astrobee/src/astrobee/scripts/setup/debians/opencv/patches/series
fi
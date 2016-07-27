vim_plugin=$1/vim_plugin
sudo_usr=$SUDO_USER
usr_home=`eval echo "~$sudo_usr"`

su $SUDO_USER -c "mkdir -p $usr_home/.vim"

su $SUDO_USER -c "cp -a $vim_plugin/* $usr_home/.vim/"

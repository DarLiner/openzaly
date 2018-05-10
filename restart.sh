#!/bin/bash
PORT=$1
PORT2=$2

#### echo akaxin logo and desc
echo "

                    / \                  
                  /     \              
                /   *     \           
                \     *     \         
            / \   \     *   /   			
          /     \   \     /   / \                 ___       __  ___      ___      ___   ___  __  .__   __. 
        /     *   \   | /   /     \              /   \     |  |/  /     /   \     \  \ /  / |  | |  \ |  | 
      /     *     _ \ /   /     *   \           /  ^  \    |  .  /     /  ^  \     \  V  /  |  | |   \|  | 
      \   *     /   / \ -     *     /          /  /_\  \   |    <     /  /_\  \     >   <   |  | |  .    | 
        \     /   / |   \   *     /           /  _____  \  |  .  \   /  _____  \   /  .  \  |  | |  |\   | 
          \ /   /     \   \     /            /__/     \__\ |__|\__\ /__/     \__\ /__/ \__\ |__| |__| \__| 
              /   *     \   \ /       
              \     *     \       
                \     *   /    
                  \     /   
                    \ /    
    
Akaxin is an open source and free proprietary IM software，you can build private openzaly-server for everyone in any server.
openzaly-version : 0.5.4
java-version : JDK 1.8+
maven-version : 3.0+

"

##set tcp port
if [ -n $PORT ]; then
	PORT=2021
fi
	
##set http port
if [ -n $PORT2 ]; then
	PORT2=8080 
fi

sh stop.sh $PORT 1
sh start.sh $PORT $PORT2 1
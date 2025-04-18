#!/bin/bash

# Set JAVA_HOME
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-23.jdk/Contents/Home

# Function to run a node
run_node() {
    local node_number=$1
    echo "Starting Node $node_number..."
    mvn exec:java -Dexec.mainClass="org.example.RingLauncher" -Dexec.args="$node_number"
}

# If called with a node number, run that specific node
case "$1" in
    "node1")
        run_node 1
        ;;
    "node2")
        run_node 2
        ;;
    "node3")
        run_node 3
        ;;
    *)
        # Only open new terminals if no arguments are provided
        if [ -z "$1" ]; then
            osascript -e 'tell app "Terminal" to do script "cd '"$PWD"' && ./run_nodes.sh node1"'
            osascript -e 'tell app "Terminal" to do script "cd '"$PWD"' && ./run_nodes.sh node2"'
            osascript -e 'tell app "Terminal" to do script "cd '"$PWD"' && ./run_nodes.sh node3"'
        else
            echo "Usage: ./run_nodes.sh [node1|node2|node3]"
            exit 1
        fi
        ;;
esac 
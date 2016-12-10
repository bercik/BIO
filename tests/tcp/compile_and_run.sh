#!/bin/bash
echo "compile client.bio"
bioc client.bio -o client.cbio
echo "compile server.bio"
bioc server.bio -o server.cbio

echo "running server"
bio server.cbio &
sleep 1
echo "running client"
bio client.cbio &

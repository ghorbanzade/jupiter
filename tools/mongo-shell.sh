#!/bin/bash

MONGO_PASSWORD="password"
mongo "mongodb://cluster0-shard-00-00-0gnvj.mongodb.net:27017,cluster0-shard-00-01-0gnvj.mongodb.net:27017,cluster0-shard-00-02-0gnvj.mongodb.net:27017/test?replicaSet=Cluster0-shard-0" --authenticationDatabase admin --ssl --username pejman --password ${MONGO_PASSWORD}

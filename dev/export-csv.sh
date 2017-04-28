#!/bin/bash

# by default, we are using mongoDB Atlas cloud instance
# comment the following lines if exporting local database
MONGODB_PASSWORD="<PASSWORD>"
MONGODB_HOST="Cluster0-shard-0/cluster0-shard-00-00-0gnvj.mongodb.net:27017,cluster0-shard-00-01-0gnvj.mongodb.net:27017,cluster0-shard-00-02-0gnvj.mongodb.net:27017"
MONGODB_CONFIG="--ssl --host ${MONGODB_HOST} --authenticationDatabase admin -u pejman --password ${MONGODB_PASSWORD}"

# uncomment the following line if exporting local database
#MONGODB_CONFIG="localhost"

# rides
mongoexport ${MONGODB_CONFIG} --db jupiter --collection rides --type=csv --out rides.csv --fields customer_id,rider_ids,duration_total,distance_total,t_p1_p2,t_p2_d2,t_d2_d1,t_p2_d1,t_d1_d2,d_p1_p2,d_p2_d2,d_d2_d1,d_p2_d1,d_d1_d2,scenario,candidates_count,candidate_ids

# candidates
mongoexport ${MONGODB_CONFIG} --db jupiter --collection candidates --type=csv --out candidates.csv --fields customer_id,candidates_count,candidate_ids

# single_rides
mongoexport ${MONGODB_CONFIG} --db jupiter --collection single_rides --type=csv --out single_rides.csv --fields customer_id,google_distance,google_duration

#mongodump ${MONGODB_CONFIG}

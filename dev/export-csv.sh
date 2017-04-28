#!/bin/bash

print_usage () {
    echo "usage: ${0} [ atlas | local ]"
}

get_mongo_config () {
    local OUT="--host localhost"
    local MONGODB_HOSTNAME="<SECRET>"
    local MONGODB_USERNAME="<SECRET>"
    local MONGODB_PASSWORD="<SECRET>"
    if [[ $# -gt 0 ]]; then
        if [[ "$1" == "atlas" ]]; then
            OUT="--ssl --host ${MONGODB_HOSTNAME} --authenticationDatabase admin -u ${MONGODB_USERNAME} --password ${MONGODB_PASSWORD}"
        fi
    fi
    echo ${OUT};
}

task_export () {
    # by default, a local instance of mongoDB is used
    local MONGODB_CONFIG=$(get_mongo_config "$@")

    # rides
    mongoexport ${MONGODB_CONFIG} --db jupiter --collection rides --type=csv --out rides.csv --fields customer_id,rider_ids,duration_total,distance_total,t_p1_p2,t_p2_d2,t_d2_d1,t_p2_d1,t_d1_d2,d_p1_p2,d_p2_d2,d_d2_d1,d_p2_d1,d_d1_d2,scenario,candidates_count,candidate_ids

    # candidates
    mongoexport ${MONGODB_CONFIG} --db jupiter --collection candidates --type=csv --out candidates.csv --fields customer_id,candidates_count,candidate_ids

    # single_rides
    mongoexport ${MONGODB_CONFIG} --db jupiter --collection single_rides --type=csv --out single_rides.csv --fields customer_id,google_distance,google_duration

    #mongodump ${MONGODB_CONFIG}
}

if [ $# -eq 0 ]; then
    log_debug "no arguments provided"
    print_usage
    exit
fi

case "$1" in
    local | atlas)
        task_export "$1"
        ;;
    *)
        print_usage
        ;;
esac

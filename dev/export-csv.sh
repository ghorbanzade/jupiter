#!/bin/bash

mongoexport --host localhost --db jupiter --collection rides --type=csv --out result.csv --fields customer_id,rider_ids,duration_total,distance_total,t_p1_p2,t_p2_d2,t_d2_d1,t_p2_d1,t_d1_d2,d_p1_p2,d_p2_d2,d_d2_d1,d_p2_d1,d_d1_d2,scenario,candidates_count,candidate_ids
#mongoexport --host localhost --db jupiter --collection rides --type=csv --out result.csv --fields customer_id,candidates_count,candidate_ids
#mongoexport --host localhost --db jupiter --collection rides --type=csv --out result.csv --fields customer_id,google_distance,google_duration

#!/bin/bash

mongoexport --host localhost --db jupiter --collection result --type=csv --out result.csv --fields customer_id,rider_ids,duration_total,distance_total,scenario,candidates_count,candidate_ids

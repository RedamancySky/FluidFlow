#!/bin/bash
run_id=42384926431
while true; do
  curl -s "https://api.github.com/repos/RedamancySky/FluidFlow/actions/runs/$run_id" > .run_check2.json
  status=$(grep -o '"status": *"[^"]*"' .run_check2.json | head -1 | cut -d'"' -f4)
  conclusion=$(grep -o '"conclusion": *"[a-z_]*"' .run_check2.json | head -1 | cut -d'"' -f4)
  echo "status=$status conclusion=$conclusion"
  if [ "$status" = "completed" ]; then
    rm -f .run_check2.json
    break
  fi
  sleep 20
done

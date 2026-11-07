#!/bin/bash
run_id=42384751896
while true; do
  curl -s "https://api.github.com/repos/RedamancySky/FluidFlow/actions/runs/$run_id" > .run_check.json
  status=$(grep -o '"status": *"[^"]*"' .run_check.json | head -1 | cut -d'"' -f4)
  conclusion=$(grep -o '"conclusion": *"[a-z_]*"' .run_check.json | head -1 | cut -d'"' -f4)
  echo "status=$status conclusion=$conclusion"
  if [ "$status" = "completed" ]; then
    rm -f .run_check.json
    break
  fi
  sleep 20
done

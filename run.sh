#!/bin/bash
if [ -f .env ]; then
    set -a
    source .env
    set +a
    echo ".env loaded"
else
    echo "No .env file found"
fi

mvn spring-boot:run
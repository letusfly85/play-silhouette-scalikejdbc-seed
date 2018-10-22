#!/usr/bin/env bash

export ROOT_PATH="`pwd`"

export NETWORK=$1
if [ "${CI}" = "true" ]; then
    export NETWORK=host
fi

docker run --rm  \
   --network ${NETWORK} \
   -v ${ROOT_PATH}/conf/db/migration:/flyway/sql \
   -v ${ROOT_PATH}/conf/db/conf:/flyway/conf \
   boxfuse/flyway:5.1.4-alpine info migrate info

#!/bin/sh

i=1
while [ $i -le 10 ]; do
    echo "Iniciando execução com quantum de $i"
    ./run.sh $i

    i=$(expr $i + 1)
done
#!/bin/bash

./init-dashboard.sh
java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -XX:MaxRAM=512m -jar emp.jar


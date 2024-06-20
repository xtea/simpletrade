#!/bin/bash
function update(){
	echo 'update new version from github'
	git pull
	echo "update done."
}

function start(){
	cd simpletrade
	rm ~/log
	./mvnw clean
	nohup ./mvnw spring-boot:run > ~/log &
	echo "start up done."
}

function stop(){
	pid=`jps |grep App | awk '{print $1}'`
	kill -9 $pid
}

function main(){
	cd startup
	update
	stop
	start
}

main
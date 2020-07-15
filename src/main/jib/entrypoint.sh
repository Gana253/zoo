#!/bin/sh

echo "The application will start in ${MYSQL_SLEEP}s..." && sleep ${MYSQL_SLEEP}
exec java ${JAVA_OPTS} -noverify -XX:+AlwaysPreTouch -Djava.security.egd=file:/dev/./urandom -cp /app/resources/:/app/classes/:/app/libs/* "com.java.zoo.ZooApplication"  "$@"

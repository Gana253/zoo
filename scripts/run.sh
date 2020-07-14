echo Building - workspace Please wait...
cd ../
docker-compose -f src/main/docker/mysql.yml up -d
echo  Starting mysql instance. Going to sleep for 30 seconds before starting the application!!!
sleep 30
echo Starting the application!!!
mvn spring-boot:run
echo Build completed...
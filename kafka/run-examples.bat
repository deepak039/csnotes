@echo off
echo Kafka Tutorial - Running Examples
echo ================================

echo.
echo 1. Creating required topics...
kafka-topics.bat --create --topic orders --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1 --if-not-exists
kafka-topics.bat --create --topic payment-requests --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1 --if-not-exists
kafka-topics.bat --create --topic fulfillment-requests --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1 --if-not-exists
kafka-topics.bat --create --topic notifications --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1 --if-not-exists

echo.
echo 2. Compiling Java code...
mvn compile

echo.
echo 3. Running Simple Producer...
mvn exec:java -Dexec.mainClass="com.example.SimpleProducer"

echo.
echo 4. Running Simple Consumer...
timeout /t 2 /nobreak > nul
mvn exec:java -Dexec.mainClass="com.example.SimpleConsumer"

echo.
echo 5. Running Advanced Producer...
timeout /t 2 /nobreak > nul
mvn exec:java -Dexec.mainClass="com.example.AdvancedProducer"

echo.
echo Examples completed! Check the output above.
echo.
echo To run the complete order processing system:
echo mvn exec:java -Dexec.mainClass="com.example.OrderProcessingSystem"
echo.
echo To run consumer groups (run in separate terminals):
echo mvn exec:java -Dexec.mainClass="com.example.ConsumerGroupExample" -Dexec.args="consumer-1"
echo mvn exec:java -Dexec.mainClass="com.example.ConsumerGroupExample" -Dexec.args="consumer-2"

pause
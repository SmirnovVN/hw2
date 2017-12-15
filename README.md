#HW2
#####requirements
- Cloudera QuickStart VMs CDH 5.12
- 64-bit host OS and a virtualization product that can support a 64-bit guest OS
- 8+ GiB RAM
#####build
`mvn clean compile assembly:single`
#####prepare input
`python log.py 500`
#####start ignite server
`java -cp target/hw2-1.0-SNAPSHOT-jar-with-dependencies.jar ru.mephi.hw2.Server config.xml`
#####ingest
`java -cp target/hw2-1.0-SNAPSHOT-jar-with-dependencies.jar ru.mephi.hw2.LogIgnite config.xml log.log`
#####process
`java -Xms1024m -Xmx2048m -XX:MaxPermSize=256m -Djava.awt.headless=true -cp target/hw2-1.0-SNAPSHOT-jar-with-dependencies.jar ru.mephi.hw2.Main config.xml ~/spark`
#####process on cluster
`bin/spark-submit --class ru.mephi.hw2.Main --master local --deploy-mode client --executor-memory 1g target/hw2-1.0-SNAPSHOT-jar-with-dependencies.jar config.xml ~/spark`
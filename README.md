RecordReader
====
Reads customer json objects from file and prints customers within specified kilometre distance (or default 100)

To Run via command line:
1) navigate to project root
2) run 'mvn package'
3) run 'mvn spring-boot:run -Dspring-boot.run.arguments="{PATH_TO_CUSTOMERS_FILE}"' or 'mvn spring-boot:run -Dspring-boot.run.arguments="{PATH_TO_CUSTOMERS_FILE},{KILOMETRE_DISTANCE}"'

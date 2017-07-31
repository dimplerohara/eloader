#!/bin/bash
>/opt/customer/neo/apps/eLoader/microservices/filnet-crud-service/java.pid
/opt/customer/dctm/java/jdk1.7.0_55/bin/java -jar /opt/customer/neo/apps/eLoader/microservices/filnet-crud-service/cms-metadata-service-0.0.1.jar> /opt/customer/neo/apps/eLoader/microservices/filnet-crud-service/cms-metadata-service-0.0.1.log & echo $! > /opt/customer/neo/apps/eLoader/microservices/filnet-crud-service/java.pid

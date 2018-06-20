FROM maven:3.5-jdk-8 as builder
ARG DB_ROOT_PASSWORD=SET_A_PASSWORD

WORKDIR /root
RUN git clone https://github.com/AIPHES/mdswriter.git

# Update src/main/webapp/META-INF/context.xml with your database settings.
RUN echo "set database url to: jdbc:mysql://db:3306/mdswriter"
RUN sed -i -e 's|url=".*"|url="jdbc:mysql://db:3306/mdswriter"|g' /root/mdswriter/src/main/webapp/META-INF/context.xml
RUN echo "set database root password to: $DB_ROOT_PASSWORD"
RUN sed -i -e "s|password=\".*\"|password=\"$DB_ROOT_PASSWORD\"|g" /root/mdswriter/src/main/webapp/META-INF/context.xml
RUN echo "all database connection settings:" && cat /root/mdswriter/src/main/webapp/META-INF/context.xml && echo

# Build the software using mvn package
WORKDIR /root/mdswriter
RUN mvn package

FROM tomcat:9-jre8
# Deploy the war file to the application server.
COPY --from=builder /root/mdswriter/target/mdswriter-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/mdswriter.war

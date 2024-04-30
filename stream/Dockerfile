FROM eclipse-temurin:21.0.2_13-jdk-alpine

ENV APP_HOME=/app

RUN mkdir -p ${APP_HOME}/data && \
    mkdir -p ${APP_HOME}/libs && \
    mkdir -p ${APP_HOME}/dumps && \
    mkdir -p ${APP_HOME}/logs

RUN set -x && \
    apk add --update --no-cache \
        bash \
        shadow \
        curl \
        jattach && \
    rm -rf /var/cache/apk/*

COPY stream/target/kafka-streams.jar ${APP_HOME}/
COPY stream/start.sh ${APP_HOME}/
COPY stream/docker-entrypoint.sh /

RUN chmod +x ${APP_HOME}/start.sh && \
    chmod +x /docker-entrypoint.sh

ARG JMX_AGENT_VERSION=0.20.0
ARG METRICS_PORT=7171
RUN curl -so ${APP_HOME}/libs/jmx_prometheus_javaagent.jar \
    https://repo.maven.apache.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/${JMX_AGENT_VERSION}/jmx_prometheus_javaagent-${JMX_AGENT_VERSION}.jar
ENV JMX_OPTS="-javaagent:$APP_HOME/libs/jmx_prometheus_javaagent.jar=$METRICS_PORT:$APP_HOME/config/kafka-streams.yml"

# Add unpriviliged user
RUN set -x && \
    groupadd -r kafka --gid=1000 && \
    useradd -s /bin/bash -r -g kafka --uid=1000 kafka && \
    usermod -a -G 0 kafka

# Adapt grants
RUN chown -R kafka:kafka ${APP_HOME}/data && \
    chown -R kafka:kafka ${APP_HOME}/dumps && \
    chown -R kafka:kafka ${APP_HOME}/logs && \
    chown -R kafka:kafka ${APP_HOME}/logs

WORKDIR ${APP_HOME}
USER kafka

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["start"]

#FROM gliderlabs/alpine:latest
FROM python:3.4.9-alpine3.8

LABEL maintainer="elastest-users@googlegroups.com"
LABEL description="Builds the sentinel docker agent image."

COPY ./requirements.txt /
COPY ./sentinel-agent.conf /
COPY ./sentinel-docker-agent.py /

RUN apk --update add --virtual build-deps python3-dev build-base linux-headers
RUN apk --update add python3 py3-pip openssl ca-certificates git \
    && pip3 install --upgrade setuptools \
    && pip3 install -r /requirements.txt && rm /requirements.txt \
    && apk del build-deps

CMD ["/usr/bin/python3", "/sentinel-docker-agent.py"]

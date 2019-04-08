FROM golang:1.12.2-alpine3.9

RUN apk update && apk upgrade && \
    apk add --no-cache bash git openssh

WORKDIR /go/src/k8stats
COPY . .

RUN go get -d -v ./...
RUN go install -v ./...

CMD ["k8stats","-conf=/go/src/k8stats/config.cfg"]

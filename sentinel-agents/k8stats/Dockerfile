FROM golang:1.12.2-alpine3.9 AS builder

RUN apk update && apk upgrade && \
    apk add --no-cache bash git openssh

WORKDIR /go/src/k8stats
COPY . .

RUN go get k8s.io/klog && cd $GOPATH/src/k8s.io/klog && git checkout v0.4.0
RUN go get -d -v ./...
RUN go install -v ./...

FROM alpine:latest
RUN apk --no-cache add ca-certificates
WORKDIR /root/
COPY --from=builder /go/bin/k8stats /bin/
COPY --from=builder /go/src/k8stats/config.cfg .
RUN mkdir -p /root/.kube/
CMD ["k8stats","-conf=/root/config.cfg"]

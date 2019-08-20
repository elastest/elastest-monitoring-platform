FROM golang:1.12.2-alpine3.9

RUN apk update && apk upgrade && \
    apk add --no-cache bash git openssh

WORKDIR /go/src/k8stats
COPY . .

RUN go get k8s.io/klog && cd $GOPATH/src/k8s.io/klog && git checkout v0.4.0
RUN go get -d -v ./...
RUN go install -v ./...
RUN mkdir -p /root/.kube/

CMD ["k8stats","-conf=/go/src/k8stats/config.cfg"]

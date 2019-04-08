/*
 *  Copyright (c) 2019. Service Prototyping Lab, ZHAW
 *   All Rights Reserved.
 *
 *       Licensed under the Apache License, Version 2.0 (the "License"); you may
 *       not use this file except in compliance with the License. You may obtain
 *       a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *       Unless required by applicable law or agreed to in writing, software
 *       distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *       WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *       License for the specific language governing permissions and limitations
 *       under the License.
 *
 *
 *       Author: Piyush Harsh,
 *       URL: piyush-harsh.info
 *       Email: piyush.harsh@zhaw.ch
 */

package main

import (
	"context"
	"encoding/json"
	"flag"
	"fmt"
	"github.com/scalingdata/gcfg"
	"github.com/segmentio/kafka-go"
	"github.com/segmentio/kafka-go/snappy"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/clientcmd"
	metrics "k8s.io/metrics/pkg/client/clientset/versioned"
	"log"
	"os"
	"path/filepath"
	"time"
)

var writer *kafka.Writer

type Configuration struct {
	Kafka struct {
		Port     string
		Host     string
		Topic    string
		Series   string
		ClientId string
	}
}

type k8sdata struct {
	Agent          string `json:"agent"`
	Node           string `json:"node"`
	Podcount       int    `json:"podcount"`
	Servicecount   int    `json:"servicecount"`
	Namespacecount int    `json:"namespacecount"`
}

func main() {

	//reading config paramaters
	confFile := flag.String("conf", "./config.cfg", "configuration file path")
	flag.Parse()

	//placeholder code as the default value will ensure this situation will never arise
	if len(*confFile) == 0 {
		log.Println("Incorrect usage, try: k8s-agent -conf=/path/to/configuration/file")
		os.Exit(0)
	}

	var cfg Configuration

	err := gcfg.ReadFileInto(&cfg, *confFile)
	if err != nil {
		log.Fatalf("Failed to parse configuration data: %s\nCorrect usage: go-customerdb -conf=/path/to/configuration/file", err)
		os.Exit(1)
	}

	//////now overriding values from environment if available////
	if len(os.Getenv("host")) > 0 {
		cfg.Kafka.Host = os.Getenv("host")
	}

	if len(os.Getenv("port")) > 0 {
		cfg.Kafka.Port = os.Getenv("port")
	}

	if len(os.Getenv("topic")) > 0 {
		cfg.Kafka.Topic = os.Getenv("topic")
	}

	if len(os.Getenv("series")) > 0 {
		cfg.Kafka.Series = os.Getenv("series")
	}

	if len(os.Getenv("clientid")) > 0 {
		cfg.Kafka.ClientId = os.Getenv("clientid")
	}
	///////////////////////////////////////////////////////////////

	//establishing kafka connection first
	kafkaProducer, err := Configure([]string{cfg.Kafka.Host + ":" + cfg.Kafka.Port}, cfg.Kafka.ClientId, cfg.Kafka.Topic)
	if err != nil {
		panic(err.Error())
	}
	defer kafkaProducer.Close()

	//reading from Kube config file
	var kubeconfig *string
	if home := homeDir(); home != "" {
		kubeconfig = flag.String("kubeconfig", filepath.Join(home, ".kube", "config"), "(optional) absolute path to the kubeconfig file")
	} else {
		kubeconfig = flag.String("kubeconfig", "", "absolute path to the kubeconfig file")
	}
	flag.Parse()

	// use the current context in kubeconfig
	config, err := clientcmd.BuildConfigFromFlags("", *kubeconfig)
	if err != nil {
		panic(err.Error())
	}

	// create the clientset
	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		panic(err.Error())
	}

	mc, err := metrics.NewForConfig(config)

	for {
		//creating the sample point to send to sentinel
		dataPoint := new(k8sdata)
		dataPoint.Agent = "sentinel-generic-agent"

		pods, err := clientset.CoreV1().Pods("").List(metav1.ListOptions{})

		if err != nil {
			panic(err.Error())
		}
		fmt.Printf("There are %d pods in the cluster\n", len(pods.Items))
		dataPoint.Podcount = len(pods.Items)

		for _, pod := range pods.Items {
			fmt.Printf("Found pod [%s] in namespace [%s].\n", pod.GetName(), pod.GetClusterName())
		}

		services, err := clientset.CoreV1().Services("").List(metav1.ListOptions{})

		if err != nil {
			panic(err.Error())
		}
		fmt.Printf("There are %d services in the cluster\n", len(services.Items))
		dataPoint.Servicecount = len(services.Items)

		namespaces, err := clientset.CoreV1().Namespaces().List(metav1.ListOptions{})

		if err != nil {
			panic(err.Error())
		}
		fmt.Printf("There are %d namespaces in the cluster\n", len(namespaces.Items))
		dataPoint.Namespacecount = len(namespaces.Items)

		for _, namespace := range namespaces.Items {
			fmt.Printf("Found namespace [%s].\n", namespace.GetName())
		}

		metrices, err := mc.MetricsV1beta1().NodeMetricses().List(metav1.ListOptions{})

		if err != nil {
			panic(err.Error())
		}
		for _, nodeMetric := range metrices.Items {
			fmt.Printf("Found node with name [%s].\n", nodeMetric.GetName())

			dataPoint.Node = nodeMetric.GetName()

			nodeData, err := mc.MetricsV1beta1().NodeMetricses().Get(nodeMetric.GetName(), metav1.GetOptions{})
			if err != nil {
				panic(err.Error())
			}
			mapdata := nodeData.GetLabels()
			for k := range mapdata {
				fmt.Printf("Got a key [%s].\n", k)
			}
		}

		msgtosend, _ := json.Marshal(dataPoint)
		msg := string(msgtosend)
		println("Msg to send:", msg)
		err = Push(context.Background(), []byte(cfg.Kafka.Series), []byte(msg))
		if err != nil {
			panic(err.Error())
		}

		time.Sleep(10 * time.Second)
	}
}

func homeDir() string {
	if h := os.Getenv("HOME"); h != "" {
		return h
	}
	return os.Getenv("USERPROFILE") // windows
}

func Configure(kafkaBrokerUrls []string, clientId string, topic string) (w *kafka.Writer, err error) {
	dialer := &kafka.Dialer{
		Timeout:  10 * time.Second,
		ClientID: clientId,
	}

	config := kafka.WriterConfig{
		Brokers:          kafkaBrokerUrls,
		Topic:            topic,
		Balancer:         &kafka.LeastBytes{},
		Dialer:           dialer,
		WriteTimeout:     10 * time.Second,
		ReadTimeout:      10 * time.Second,
		CompressionCodec: snappy.NewCompressionCodec(),
	}
	w = kafka.NewWriter(config)
	writer = w
	return w, nil
}

func Push(parent context.Context, key, value []byte) (err error) {
	message := kafka.Message{
		Key:   key,
		Value: value,
		Time:  time.Now(),
	}
	return writer.WriteMessages(parent, message)
}

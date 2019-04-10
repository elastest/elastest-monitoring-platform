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
	"gopkg.in/yaml.v2"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/clientcmd"
	"log"
	"os"
	"path/filepath"
	"strconv"
	"time"
)

var writer *kafka.Writer

type Configuration struct {
	Kafka struct {
		Port        string
		Host        string
		Topic       string
		Series      string
		ClientId    string
		Periodicity string
	}
	K8s struct {
		ApiToken              string
		ClusterName           string
		ClusterServer         string
		ClusterCA             string
		ContextName           string
		ContextCluster        string
		ContextUser           string
		CurrentContext        string
		UserName              string
		UserClientCertificate string
		UserClientKey         string
		ConfigPath            string
	}
}

type k8sdata struct {
	Agent          string `json:"agent"`
	Node           string `json:"node"`
	Podcount       int    `json:"podcount"`
	Servicecount   int    `json:"servicecount"`
	Namespacecount int    `json:"namespacecount"`
	Cpu            string `json:"cpu"`
	Memory         string `json:"ram"`
}

type kubeconfig struct {
	ApiVersion     string      `yaml:"apiVersion"`
	Clusters       []Cluster   `yaml:"clusters"`
	Contexts       []K8Context `yaml:"contexts"`
	CurrentContext string      `yaml:"current-context"`
	Kind           string      `yaml:"kind"`
	Preferences    PrfObj      `yaml:"preferences"`
	Users          []User      `yaml:"users"`
}

type PrfObj struct {
}

type User struct {
	Name      string  `yaml:"name"`
	UserInner UserObj `yaml:"user"`
}

type UserObj struct {
	ClientCert string `yaml:"client-certificate"`
	ClientKey  string `yaml:"client-key"`
}

type Cluster struct {
	Name         string     `yaml:"name"`
	ClusterInner ClusterObj `yaml:"cluster"`
}

type ClusterObj struct {
	CertificateAuth string `yaml:"certificate-authority"`
	Server          string `yaml:"server"`
}

type K8Context struct {
	Name        string     `yaml:"name"`
	ConextInner ContextObj `yaml:"context"`
}

type ContextObj struct {
	ClusterName string `yaml:"cluster"`
	User        string `yaml:"user"`
}

type NodesStat struct {
	Items []NodeData `json:"items"`
}

type NodeData struct {
	Metadata NodeMetadata `json:"metadata"`
	Usage    NodeUsage    `json:"usage"`
}

type NodeMetadata struct {
	Name     string `json:"name"`
	SelfLink string `json:"selfLink"`
}

type NodeUsage struct {
	CPU    string `json:"cpu"`
	Memory string `json:"memory"`
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

	if len(os.Getenv("periodicity")) > 0 {
		cfg.Kafka.Periodicity = os.Getenv("periodicity")
	}

	if len(os.Getenv("clusterserver")) > 0 {
		cfg.K8s.ClusterServer = os.Getenv("clusterserver")
	}

	if len(os.Getenv("clustername")) > 0 {
		cfg.K8s.ClusterName = os.Getenv("clustername")
	}

	if len(os.Getenv("clusterca")) > 0 {
		cfg.K8s.ClusterCA = os.Getenv("clusterca")
	}

	if len(os.Getenv("contextname")) > 0 {
		cfg.K8s.ContextName = os.Getenv("contextname")
	}

	if len(os.Getenv("contextcluster")) > 0 {
		cfg.K8s.ContextCluster = os.Getenv("contextcluster")
	}

	if len(os.Getenv("contextuser")) > 0 {
		cfg.K8s.ContextUser = os.Getenv("contextuser")
	}

	if len(os.Getenv("username")) > 0 {
		cfg.K8s.UserName = os.Getenv("username")
	}

	if len(os.Getenv("userclientcertificate")) > 0 {
		cfg.K8s.UserClientCertificate = os.Getenv("userclientcertificate")
	}

	if len(os.Getenv("userclientkey")) > 0 {
		cfg.K8s.UserClientKey = os.Getenv("userclientkey")
	}

	if len(os.Getenv("configpath")) > 0 {
		cfg.K8s.ConfigPath = os.Getenv("configpath")
	}

	if len(os.Getenv("currentcontext")) > 0 {
		cfg.K8s.CurrentContext = os.Getenv("currentcontext")
	}

	if len(os.Getenv("apitoken")) > 0 {
		cfg.K8s.ApiToken = os.Getenv("apitoken")
	}
	///////////////////////////////////////////////////////////////

	home := homeDir()

	/// trying to recreate kubeconfig file within the code
	k8cfg := kubeconfig{}
	k8cfg.ApiVersion = "v1"
	k8cfg.Kind = "Config"
	k8cfg.CurrentContext = cfg.K8s.CurrentContext
	user := User{}
	user.Name = cfg.K8s.UserName
	user.UserInner.ClientCert = filepath.Join(home, ".kube", cfg.K8s.UserClientCertificate)
	user.UserInner.ClientKey = filepath.Join(home, ".kube", cfg.K8s.UserClientKey)
	k8cfg.Users = append(k8cfg.Users, user)
	cluster := Cluster{}
	cluster.Name = cfg.K8s.ClusterName
	cluster.ClusterInner.Server = cfg.K8s.ClusterServer
	cluster.ClusterInner.CertificateAuth = filepath.Join(home, ".kube", cfg.K8s.ClusterCA)
	k8cfg.Clusters = append(k8cfg.Clusters, cluster)
	k8context := K8Context{}
	k8context.Name = cfg.K8s.ContextName
	k8context.ConextInner.ClusterName = cfg.K8s.ContextCluster
	k8context.ConextInner.User = cfg.K8s.ContextUser
	k8cfg.Contexts = append(k8cfg.Contexts, k8context)

	y, err := yaml.Marshal(k8cfg)

	fmt.Printf("Kube config object generated in code. Value:\n----------------\n%s\n----------------\n", string(y))

	/// trying to create a temp kube_config file
	f, err := os.Create(cfg.K8s.ConfigPath)
	if err != nil {
		panic(err.Error())
	}
	defer f.Close()
	_, err = f.WriteString(string(y))
	err = f.Sync()
	///////////////////////////////////////////////////////////////

	//establishing kafka connection first
	kafkaProducer, err := Configure([]string{cfg.Kafka.Host + ":" + cfg.Kafka.Port}, cfg.Kafka.ClientId, cfg.Kafka.Topic)
	if err != nil {
		panic(err.Error())
	}
	defer kafkaProducer.Close()

	//reading from Kube config file
	var kubeconfig *string
	if home != "" {
		//kubeconfig = flag.String("kubeconfig", filepath.Join(home, ".kube", "config"), "(optional) absolute path to the kubeconfig file")
		kubeconfig = flag.String("kubeconfig", cfg.K8s.ConfigPath, "(optional) absolute path to the kubeconfig file")
	} else {
		kubeconfig = flag.String("kubeconfig", "", "absolute path to the kubeconfig file")
	}
	flag.Parse()

	fmt.Printf("Using the Kube config file located at: %s\n", *kubeconfig)

	// use the current context in kubeconfig
	config, err := clientcmd.BuildConfigFromFlags("", *kubeconfig)
	config.BearerToken = cfg.K8s.ApiToken
	//config.Insecure = false
	//config.Host = cfg.K8s.ClusterServer

	fmt.Printf("Verifying config data:\n\tHost: %s\n\tInsecure Mode: %t\n\tBearer Token: %s\n", config.Host, config.Insecure, config.BearerToken)
	if err != nil {
		panic(err.Error())
	}

	// create the clientset
	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		panic(err.Error())
	}

	//mc, err := metrics.NewForConfig(config)

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

		///////////////// getting node stats ////////////////
		data, err := clientset.RESTClient().Get().AbsPath("apis/metrics.k8s.io/v1beta1/nodes").DoRaw()
		var nodesData NodesStat
		err = json.Unmarshal(data, &nodesData)

		for _, node := range nodesData.Items {
			fmt.Printf("Received data on node [%s]: CPU usage: [%s], RAM: [%s]\n", node.Metadata.Name, node.Usage.CPU, node.Usage.Memory)
			dataPoint.Node = node.Metadata.Name
			dataPoint.Cpu = node.Usage.CPU
			dataPoint.Memory = node.Usage.Memory
		}

		/////////////////////////////////////////////////////

		//metrices, err := mc.MetricsV1beta1().NodeMetricses().List(metav1.ListOptions{})
		//
		//if err != nil {
		//	panic(err.Error())
		//}
		//for _, nodeMetric := range metrices.Items {
		//	fmt.Printf("Found node with name [%s].\n", nodeMetric.GetName())
		//	dataPoint.Node = nodeMetric.GetName()
		//
		//	nodeData, err := mc.MetricsV1beta1().NodeMetricses().Get(nodeMetric.GetName(), metav1.GetOptions{})
		//	if err != nil {
		//		panic(err.Error())
		//	}
		//	mapdata := nodeData.GetLabels()
		//	for k := range mapdata {
		//		fmt.Printf("Got a key [%s].\n", k)
		//	}
		//}

		msgtosend, _ := json.Marshal(dataPoint)
		msg := string(msgtosend)
		println("Msg to send:", msg)
		err = Push(context.Background(), []byte(cfg.Kafka.Series), []byte(msg))
		if err != nil {
			panic(err.Error())
		}
		duration, _ := strconv.Atoi(cfg.Kafka.Periodicity)
		time.Sleep(time.Duration(duration) * time.Second)
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

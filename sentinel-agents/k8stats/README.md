# Sentinel Kubernetes agent: Incluster deployment

EMP Kubernetes agent can be started in 2 modes, 'external' or 'incluster' mode. The agent can be started in an incluster mode via kubectl in this manner -

```bash
kubectl run <pod-name> --image=elastest/emp-k8s-agent --env="mode=incluster"
```

The container must to granted the Kubernetes admin prevelage to function in full capacity. This policy can be applied to 'default' namespace using 

```bash
kubectl apply -f rbac.yaml
```

The content of rbac.yaml file can be along the lines of the following snippet:

```yaml
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: ClusterRoleBinding
metadata:
  name: admin-rbac
subjects:
  - kind: ServiceAccount
    # Reference to upper's `metadata.name`
    name: default
    # Reference to upper's `metadata.namespace`
    namespace: default
roleRef:
  kind: ClusterRole
  name: cluster-admin
  apiGroup: rbac.authorization.k8s.io
```

*Metric server* should be already enabled and running in the Kubernetes deployment for retrieval of full spectrum of data by this agent process. If not then this agent will only report back a subset of metrics.

```
  Copyright (c) 2019. Zuercher Hochschule fuer Angewandte Wissenschaften
   All Rights Reserved.
 
      Licensed under the Apache License, Version 2.0 (the "License"); you may
      not use this file except in compliance with the License. You may obtain
      a copy of the License at
 
           http://www.apache.org/licenses/LICENSE-2.0
 
      Unless required by applicable law or agreed to in writing, software
      distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
      WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
      License for the specific language governing permissions and limitations
      under the License.
```


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


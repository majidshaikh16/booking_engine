## Kafka Commands

### Create Topics
```bash
docker exec -it kafka bash
kafka-topics --list --bootstrap-server localhost:9092
kafka-topics --create --topic payment-new --bootstrap-server localhost:9092
kafka-topics --delete --topic paument-events --bootstrap-server localhost:9092

##in one line docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```


# AWS EKS Cluster Setup Guide

## **Step 1: Install AWS CLI and Configure Access**

### **1.1: Install AWS CLI (if not installed)**
```sh
brew install awscli
```
Verify installation:
```sh
aws --version
```

### **1.2: Configure AWS CLI**
```sh
aws configure
```
It will ask for:
```
AWS Access Key ID [None]: AKIAEXAMPLE123456789
AWS Secret Access Key [None]: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
Default region name [None]: ap-south-1
Default output format [None]: json
```
Verify the configuration:
```sh
aws sts get-caller-identity
#output should be something like this
#{
#    "UserId": "AIDAT4GVRW5CCAI7345VX",
#    "Account": "266735826756",
#    "Arn": "arn:aws:iam::266735826756:user/majids-aws-cli-user"
#}
```

## **Step 2: Install and Configure `eksctl`**

### **2.1: Install `eksctl`**
```sh
brew install eksctl
brew install kubectl
```
Verify installation:
```sh
eksctl version
#output should be something like this
#0.204.0-dev+b073ca55e.2025-02-13T20:01:47Z

kubectl version --client
#output should be something like this
#Client Version: version.Info{Major:"1", Minor:"22", GitVersion:"v1.22.2", GitCommit:"8b5a19147530

```

## **Step 3: Create an EKS Cluster**

### **3.1: Create the EKS Cluster**
```sh
eksctl create cluster \
  --name hotel-booking-cluster \
  --region ap-south-1 \
  --nodegroup-name standard-workers \
  --node-type t3.medium \
  --nodes 2 \
  --nodes-min 2 \
  --nodes-max 4 \
  --managed
```

### Delete the EKS Cluster
```sh
eksctl delete cluster --name hotel-booking-cluster --region ap-south-1
```

### **3.2: Verify the EKS Cluster**
```sh
eksctl get cluster --region us-east-1
```
Expected output:
```
NAME                      REGION        STATUS
hotel-booking-cluster     us-east-1     ACTIVE
```

### **3.3: Verify Worker Nodes**
```sh
kubectl get nodes
```
Expected output:
```
NAME                             STATUS   ROLES    AGE     VERSION
ip-192-168-1-100.ec2.internal   Ready    <none>   2m      v1.24
ip-192-168-1-101.ec2.internal   Ready    <none>   2m      v1.24
```

---

📌 Stop Worker Nodes to Reduce Cost:
When you are not using EKS, you can stop the worker nodes to avoid EC2 charges:
```sh
aws ec2 stop-instances --instance-ids <INSTANCE_ID_1> <INSTANCE_ID_2>
```
### To get your instance IDs:
```sh
aws ec2 describe-instances --query "Reservations[*].Instances[*].InstanceId" --output text
```
## Stop all Ec2 in one command
```sh
aws ec2 stop-instances --instance-ids $(aws ec2 describe-instances --query "Reservations[*].Instances[*].InstanceId" --output text)
```
